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

    // For MVP, track a single active warehouse session from the frontend
    private Warehouse currentWarehouse;

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation() {
        if (currentWarehouse == null) {
            currentWarehouse = warehouseFactory.createDefaultWarehouse();
        }
        simulationService.startSimulation(currentWarehouse);
        log.info("Started default simulation session: {}", currentWarehouse.getId());
        return ResponseEntity.ok("Simulation started for warehouse: " + currentWarehouse.getId());
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSimulation() {
        if (currentWarehouse != null) {
            simulationService.stopSimulation(currentWarehouse.getId());
            log.info("Stopped default simulation session: {}", currentWarehouse.getId());
        }
        return ResponseEntity.ok("Simulation stopped");
    }

    @PostMapping("/orders/inject")
    public ResponseEntity<String> injectOrder() {
        if (currentWarehouse == null) {
            return ResponseEntity.badRequest().body("Simulation not started.");
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
        item.setLocation(getRandomTargetNode());
        order.setItems(Collections.singletonList(item));

        currentWarehouse.getActiveOrders().add(order);
        log.info("Injected new order: {}", order.getId());

        return ResponseEntity.ok("Order injected: " + order.getId());
    }

    @PostMapping("/scenarios/stress")
    public ResponseEntity<String> triggerStressTest() {
        if (currentWarehouse == null) {
            return ResponseEntity.badRequest().body("Simulation not started.");
        }

        // Stress test injects 50 random orders at once
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
            item.setLocation(getRandomTargetNode());
            order.setItems(Collections.singletonList(item));

            currentWarehouse.getActiveOrders().add(order);
        }

        log.info("Scenario Stress Test Triggered! 50 orders injected.");
        return ResponseEntity.ok("Stress Test Triggered. 50 orders injected.");
    }

    @GetMapping("/scenarios/replay/{warehouseId}")
    public ResponseEntity<?> getReplayLogs(@PathVariable UUID warehouseId) {
        if (warehouseId == null) {
            return ResponseEntity.badRequest().body("Invalid warehouse ID");
        }
        var logs = eventLogRepository.findByWarehouseIdOrderByTimestampAsc(warehouseId);
        log.info("Fetched {} logs for replay of warehouse {}", logs.size(), warehouseId);
        return ResponseEntity.ok(logs);
    }

    private com.warehouse.digitaltwin.domain.model.GridNode getRandomTargetNode() {
        if (currentWarehouse == null || currentWarehouse.getGrid() == null || currentWarehouse.getGrid().isEmpty()) {
            return null;
        }
        Random random = new Random();
        // Return a random node from the grid that is NOT an OBSTACLE.
        return currentWarehouse.getGrid().stream()
                .filter(node -> node.getType() != com.warehouse.digitaltwin.domain.model.NodeType.OBSTACLE)
                .skip(random.nextInt((int) currentWarehouse.getGrid().stream()
                        .filter(node -> node.getType() != com.warehouse.digitaltwin.domain.model.NodeType.OBSTACLE)
                        .count()))
                .findFirst()
                .orElse(currentWarehouse.getGrid().get(0));
    }
}
