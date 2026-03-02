package com.warehouse.digitaltwin.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.warehouse.digitaltwin.domain.model.Warehouse;
import com.warehouse.digitaltwin.engine.SimulationEngine;
import com.warehouse.digitaltwin.infrastructure.persistence.PersistenceService;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service


public class SimulationService {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);

    private final SimpMessagingTemplate messagingTemplate;
    private final PersistenceService persistenceService;
    private final PredictiveMaintenanceService predictiveMaintenanceService;
    private final EventLoggerService eventLoggerService;
    private final OrderAssignmentService orderAssignmentService;
    private final Map<UUID, SimulationEngine> activeEngines = new ConcurrentHashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public void startSimulation(Warehouse warehouse) {
        if (activeEngines.containsKey(warehouse.getId())) {
            log.warn("Simulation already running for warehouse: {}", warehouse.getId());
            return;
        }

        // Persist initial state
        persistenceService.saveInitialWarehouse(warehouse);

        SimulationEngine engine = new SimulationEngine(warehouse, messagingTemplate, persistenceService,
                predictiveMaintenanceService, eventLoggerService, orderAssignmentService);
        activeEngines.put(warehouse.getId(), engine);
        executorService.submit(engine);
        log.info("Initialed simulation for warehouse: {}", warehouse.getId());
    }

    public void stopSimulation(UUID warehouseId) {
        SimulationEngine engine = activeEngines.remove(warehouseId);
        if (engine != null) {
            engine.stop();
            log.info("Stopping simulation for warehouse: {}", warehouseId);
        }
    }

    public Warehouse getCurrentWarehouse() {
        if (activeEngines.isEmpty()) {
            return null;
        }
        // For MVP, just return the first/only active simulation
        return activeEngines.values().iterator().next().getWarehouse();
    }
    public final SimpMessagingTemplate getMessagingTemplate() { return messagingTemplate; }
    public void setMessagingTemplate(final SimpMessagingTemplate messagingTemplate) { this.messagingTemplate = messagingTemplate; }
    public final PersistenceService getPersistenceService() { return persistenceService; }
    public void setPersistenceService(final PersistenceService persistenceService) { this.persistenceService = persistenceService; }
    public final PredictiveMaintenanceService getPredictiveMaintenanceService() { return predictiveMaintenanceService; }
    public void setPredictiveMaintenanceService(final PredictiveMaintenanceService predictiveMaintenanceService) { this.predictiveMaintenanceService = predictiveMaintenanceService; }
    public final EventLoggerService getEventLoggerService() { return eventLoggerService; }
    public void setEventLoggerService(final EventLoggerService eventLoggerService) { this.eventLoggerService = eventLoggerService; }
    public final OrderAssignmentService getOrderAssignmentService() { return orderAssignmentService; }
    public void setOrderAssignmentService(final OrderAssignmentService orderAssignmentService) { this.orderAssignmentService = orderAssignmentService; }
}
