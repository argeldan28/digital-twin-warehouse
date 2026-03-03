package com.warehouse.digitaltwin.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.warehouse.digitaltwin.application.services.SimulationService;
import com.warehouse.digitaltwin.domain.model.Order;
import com.warehouse.digitaltwin.domain.model.OrderItem;
import com.warehouse.digitaltwin.domain.model.OrderState;
import com.warehouse.digitaltwin.domain.model.Warehouse;
import com.warehouse.digitaltwin.domain.model.WarehouseFactory;
import com.warehouse.digitaltwin.infrastructure.persistence.repositories.EventLogRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/simulation")

public class SimulationController {

    private static final Logger log = LoggerFactory.getLogger(SimulationController.class);

    private final SimulationService simulationService;
    private final WarehouseFactory warehouseFactory;
    private final EventLogRepository eventLogRepository;

    public SimulationController(SimulationService simulationService, WarehouseFactory warehouseFactory,
            EventLogRepository eventLogRepository) {
        this.simulationService = simulationService;
        this.warehouseFactory = warehouseFactory;
        this.eventLogRepository = eventLogRepository;
    }

    // For MVP, we'll keep a list of active warehouses
    private final java.util.Map<UUID, Warehouse> activeWarehouses = new java.util.concurrent.ConcurrentHashMap<>();

    @PostMapping("/warehouses/spawn")
    public ResponseEntity<Warehouse> spawnWarehouse() {
        Warehouse newWarehouse = warehouseFactory.createDefaultWarehouse();
        activeWarehouses.put(newWarehouse.getId(), newWarehouse);
        simulationService.startSimulation(newWarehouse);
        log.info("Spawned new warehouse facility: {}", newWarehouse.getId());
        return ResponseEntity.ok(newWarehouse);
    }

    @GetMapping("/warehouses")
    public ResponseEntity<java.util.Collection<Warehouse>> getWarehouses() {
        return ResponseEntity.ok(activeWarehouses.values());
    }

    @PostMapping("/start/{id}")
    public ResponseEntity<String> startSimulation(@PathVariable UUID id) {
        Warehouse w = activeWarehouses.get(id);
        if (w != null) {
            simulationService.startSimulation(w);
            return ResponseEntity.ok("Simulation started for warehouse: " + id);
        }
        return ResponseEntity.badRequest().body("Warehouse not found");
    }

    @PostMapping("/stop/{id}")
    public ResponseEntity<String> stopSimulation(@PathVariable UUID id) {
        if (activeWarehouses.containsKey(id)) {
            simulationService.stopSimulation(id);
            log.info("Stopped simulation for warehouse: {}", id);
        }
        return ResponseEntity.ok("Simulation stopped");
    }

    @PostMapping("/orders/inject/{id}")
    public ResponseEntity<String> injectOrder(@PathVariable UUID id) {
        Warehouse w = activeWarehouses.get(id);
        if (w == null) {
            return ResponseEntity.badRequest().body("Warehouse not found.");
        }

        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setPriority((int) (Math.random() * 5) + 1);
        order.setState(OrderState.PENDING);
        order.setCreatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setId(UUID.randomUUID());
        item.setProductName("Dummy Item");
        item.setQuantity(1);
        item.setLocation(getRandomTargetNode(w));
        order.setItems(Collections.singletonList(item));

        w.getActiveOrders().add(order);
        log.info("Injected new order: {} into warehouse {}", order.getId(), id);

        return ResponseEntity.ok("Order injected: " + order.getId());
    }

    @PostMapping("/scenarios/stress/{id}")
    public ResponseEntity<String> triggerStressTest(@PathVariable UUID id) {
        Warehouse w = activeWarehouses.get(id);
        if (w == null) {
            return ResponseEntity.badRequest().body("Warehouse not found.");
        }

        for (int i = 0; i < 50; i++) {
            Order order = new Order();
            order.setId(UUID.randomUUID());
            order.setPriority((int) (Math.random() * 5) + 1);
            order.setState(OrderState.PENDING);
            order.setCreatedAt(LocalDateTime.now());

            OrderItem item = new OrderItem();
            item.setId(UUID.randomUUID());
            item.setProductName("Stress Item " + i);
            item.setQuantity(1);
            item.setLocation(getRandomTargetNode(w));
            order.setItems(Collections.singletonList(item));

            w.getActiveOrders().add(order);
        }

        return ResponseEntity.ok("Stress Test Triggered. 50 orders injected.");
    }

    @GetMapping("/scenarios/replay/{warehouseId}")
    public ResponseEntity<?> getReplayLogs(@PathVariable UUID warehouseId) {
        if (warehouseId == null) {
            return ResponseEntity.badRequest().body("Invalid warehouse ID");
        }
        var logs = eventLogRepository.findByWarehouseIdOrderByTimestampAsc(warehouseId);
        return ResponseEntity.ok(logs);
    }

    private com.warehouse.digitaltwin.domain.model.GridNode getRandomTargetNode(Warehouse w) {
        if (w == null || w.getGrid() == null || w.getGrid().isEmpty()) {
            return null;
        }
        Random random = new Random();
        return w.getGrid().stream()
                .filter(node -> node.getType() != com.warehouse.digitaltwin.domain.model.NodeType.OBSTACLE)
                .skip(random.nextInt((int) w.getGrid().stream()
                        .filter(node -> node.getType() != com.warehouse.digitaltwin.domain.model.NodeType.OBSTACLE)
                        .count()))
                .findFirst()
                .orElse(w.getGrid().get(0));
    }
}
