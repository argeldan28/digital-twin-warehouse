package com.warehouse.digitaltwin.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.warehouse.digitaltwin.domain.model.Order;
import com.warehouse.digitaltwin.domain.model.OrderState;
import com.warehouse.digitaltwin.domain.model.Robot;
import com.warehouse.digitaltwin.domain.model.RobotState;
import com.warehouse.digitaltwin.domain.model.Task;
import com.warehouse.digitaltwin.domain.model.Warehouse;
import com.warehouse.digitaltwin.infrastructure.persistence.PersistenceService;
import com.warehouse.digitaltwin.application.services.PredictiveMaintenanceService;
import com.warehouse.digitaltwin.application.services.EventLoggerService;
import com.warehouse.digitaltwin.application.services.OrderAssignmentService;

import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimulationEngine implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(SimulationEngine.class);

    private final Warehouse warehouse;
    private final SimpMessagingTemplate messagingTemplate;
    private final PersistenceService persistenceService;
    private final PredictiveMaintenanceService predictiveMaintenanceService;
    private final EventLoggerService eventLoggerService;
    private final OrderAssignmentService orderAssignmentService;

    public SimulationEngine(Warehouse warehouse, SimpMessagingTemplate messagingTemplate,
            PersistenceService persistenceService, PredictiveMaintenanceService predictiveMaintenanceService,
            EventLoggerService eventLoggerService, OrderAssignmentService orderAssignmentService) {
        this.warehouse = warehouse;
        this.messagingTemplate = messagingTemplate;
        this.persistenceService = persistenceService;
        this.predictiveMaintenanceService = predictiveMaintenanceService;
        this.eventLoggerService = eventLoggerService;
        this.orderAssignmentService = orderAssignmentService;
    }

    private final AtomicBoolean running = new AtomicBoolean(false);
    private static final int TICK_RATE_MS = 1000;

    public void stop() {
        running.set(false);
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    @Override
    public void run() {
        running.set(true);
        log.info("Simulation Engine started for warehouse: {}", warehouse.getId());

        while (running.get()) {
            long startTime = System.currentTimeMillis();

            assignOrders();
            updateRobots();

            updateKpis();

            broadcastState();

            if (eventLoggerService != null) {
                eventLoggerService.logState(warehouse);
            }

            sleepUntilNextTick(startTime);
        }
        log.info("Simulation Engine stopped.");
    }

    private void broadcastState() {
        messagingTemplate.convertAndSend("/topic/warehouse/state/" + warehouse.getId(), warehouse);
    }

    private void assignOrders() {
        if (orderAssignmentService != null) {
            for (Order order : warehouse.getActiveOrders()) {
                if (order.getState() == OrderState.PENDING) {
                    orderAssignmentService.assignOrderToRobot(warehouse, order);
                }
            }
        }
    }

    private void updateRobots() {
        for (Robot robot : warehouse.getRobots()) {
            if (robot.getState() == RobotState.MOVING && robot.getAssignedTask() != null) {
                moveRobot(robot);
                // Persist state async
                persistenceService.saveRobotStateAsync(robot, warehouse);
            }
            // Battery drain logic
            if (robot.getState() != RobotState.CHARGING) {
                robot.setBatteryLevel(Math.max(0, robot.getBatteryLevel() - 0.01));
                if (robot.getBatteryLevel() < 10 && robot.getState() != RobotState.ERROR) {
                    log.trace("Robot {} battery low!", robot.getId());
                }
            }
        }
    }

    private void moveRobot(Robot robot) {
        Task task = robot.getAssignedTask();
        if (task.getPathIndex() < task.getPath().size() - 1) {
            task.setPathIndex(task.getPathIndex() + 1);
            robot.setCurrentNode(task.getPath().get(task.getPathIndex()));

            // Add distance based on speed
            robot.setDistanceTraveled(robot.getDistanceTraveled() + Math.max(1.0, robot.getSpeed()));

            log.info("Robot {} moved to [{}, {}]", robot.getId(), robot.getCurrentNode().getX(),
                    robot.getCurrentNode().getY());

            // Analyze health via AI simulation
            if (predictiveMaintenanceService != null) {
                predictiveMaintenanceService.analyzeRobotHealth(robot);
            }

        } else {
            robot.setState(RobotState.IDLE);
            robot.setAssignedTask(null);

            // Handle order completion
            if (task.getOrder() != null) {
                Order order = task.getOrder();
                order.setState(OrderState.COMPLETED);
                order.setCompletedAt(java.time.LocalDateTime.now());
                warehouse.getActiveOrders().remove(order);
                warehouse.getCompletedOrders().add(order);
                log.info("Order {} COMPLETED by robot {}", order.getId(), robot.getId());
            }

            log.info("Robot {} completed its task.", robot.getId());
        }
    }

    private void updateKpis() {
        int activeRobots = warehouse.getRobots().size();
        double avgBattery = warehouse.getRobots().stream()
                .mapToDouble(Robot::getBatteryLevel)
                .average()
                .orElse(0.0);

        long movingRobots = warehouse.getRobots().stream()
                .filter(r -> r.getState() == RobotState.MOVING)
                .count();

        double utilization = activeRobots > 0 ? (double) movingRobots / activeRobots : 0.0;

        // Correct Throughput: Orders completed in the last hour
        java.time.LocalDateTime oneHourAgo = java.time.LocalDateTime.now().minusHours(1);
        long completedInLastHour = warehouse.getCompletedOrders().stream()
                .filter(o -> o.getCompletedAt() != null && o.getCompletedAt().isAfter(oneHourAgo))
                .count();

        com.warehouse.digitaltwin.domain.model.WarehouseKpis kpis = new com.warehouse.digitaltwin.domain.model.WarehouseKpis();
        kpis.setOrdersCompleted(warehouse.getCompletedOrders().size());
        kpis.setAverageSlaCompliance(utilization);
        kpis.setCurrentThroughput((double) completedInLastHour);

        warehouse.setKpis(kpis);
    }

    private void sleepUntilNextTick(long startTime) {
        long elapsed = System.currentTimeMillis() - startTime;
        long sleepTime = TICK_RATE_MS - elapsed;
        if (sleepTime > 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running.set(false);
            }
        }
    }

    public final SimpMessagingTemplate getMessagingTemplate() {
        return messagingTemplate;
    }

    public final PersistenceService getPersistenceService() {
        return persistenceService;
    }

    public final PredictiveMaintenanceService getPredictiveMaintenanceService() {
        return predictiveMaintenanceService;
    }

    public final EventLoggerService getEventLoggerService() {
        return eventLoggerService;
    }

    public final OrderAssignmentService getOrderAssignmentService() {
        return orderAssignmentService;
    }
}
