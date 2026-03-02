package com.warehouse.digitaltwin.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.warehouse.digitaltwin.domain.model.*;
import com.warehouse.digitaltwin.engine.pathfinding.AStarPathfinder;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service

public class OrderAssignmentService {

    private static final Logger log = LoggerFactory.getLogger(OrderAssignmentService.class);

    private final AStarPathfinder pathfinder = new AStarPathfinder();

    public void assignOrderToRobot(Warehouse warehouse, Order order) {
        // Simple logic: find first IDLE robot
        Robot assignedRobot = warehouse.getRobots().stream()
                .filter(r -> r.getState() == RobotState.IDLE)
                .findFirst()
                .orElse(null);

        if (assignedRobot == null) {
            log.warn("No idle robots available for order: {}", order.getId());
            return;
        }

        // For MVP, we pick the first item's location as the target
        if (order.getItems().isEmpty())
            return;
        GridNode target = order.getItems().get(0).getLocation();

        List<GridNode> path = pathfinder.findPath(warehouse, assignedRobot.getCurrentNode(), target);

        if (path.isEmpty()) {
            log.error("No path found for robot {} to target [{}, {}]", assignedRobot.getId(), target.getX(),
                    target.getY());
            return;
        }

        Task task = new Task();
        task.setId(UUID.randomUUID());
        task.setOrder(order);
        task.setCurrentPath(path);
        task.setPathIndex(0);

        assignedRobot.setAssignedTask(task);
        assignedRobot.setState(RobotState.MOVING);
        order.setState(OrderState.IN_PROGRESS);

        log.info("Assigned order {} to robot {}. Path length: {}", order.getId(), assignedRobot.getId(), path.size());
    }
}
