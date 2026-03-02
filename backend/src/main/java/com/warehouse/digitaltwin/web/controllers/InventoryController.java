package com.warehouse.digitaltwin.web.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.warehouse.digitaltwin.application.services.SimulationService;
import com.warehouse.digitaltwin.domain.model.InventoryItem;
import com.warehouse.digitaltwin.domain.model.NodeType;
import com.warehouse.digitaltwin.domain.model.Warehouse;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")


public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final SimulationService simulationService;

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getWarehouseInventory() {
        Warehouse currentWarehouse = simulationService.getCurrentWarehouse();
        if (currentWarehouse == null) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<InventoryItem> inventory = currentWarehouse.getGrid().stream()
                .filter(node -> node.getType() == NodeType.SHELF)
                .map(node -> node.getInventory())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        log.info("Fetched {} inventory items", inventory.size());
        return ResponseEntity.ok(inventory);
    }
}
