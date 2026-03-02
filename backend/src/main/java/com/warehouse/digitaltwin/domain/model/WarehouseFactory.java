package com.warehouse.digitaltwin.domain.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class WarehouseFactory {

    private static final Logger log = LoggerFactory.getLogger(WarehouseFactory.class);

    public Warehouse createDefaultWarehouse() {
        int width = 20;
        int height = 15;
        List<GridNode> grid = new ArrayList<>();

        Random random = new Random();
        List<Product> catalog = Arrays.asList(
                new Product(UUID.randomUUID(), "SKU-LAP-01", "Gaming Laptop", "Electronics"),
                new Product(UUID.randomUUID(), "SKU-PHO-02", "Smartphone Pro", "Electronics"),
                new Product(UUID.randomUUID(), "SKU-MON-03", "Ultrawide Monitor", "Electronics"),
                new Product(UUID.randomUUID(), "SKU-CAB-04", "USB-C Cable", "Accessories"),
                new Product(UUID.randomUUID(), "SKU-KEY-05", "Mechanical Keyboard", "Peripherals"));

        // Generate Grid
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                NodeType type = NodeType.WALKABLE;
                InventoryItem inventory = null;
                // Add some shelves
                if (x % 4 == 0 && y > 2 && y < height - 2) {
                    type = NodeType.SHELF;
                    // Randomly stock the shelf
                    Product randomProd = catalog.get(random.nextInt(catalog.size()));
                    int qty = random.nextInt(50) + 1;
                    GridNode locInfo = new GridNode(x, y, NodeType.SHELF, null); // don't infinitely recurse
                    inventory = new InventoryItem(UUID.randomUUID(), randomProd, qty, locInfo);
                }
                // Add stations
                if ((x == 0 && y == 0) || (x == width - 1 && y == 0)) {
                    type = NodeType.STATION;
                }
                grid.add(new GridNode(x, y, type, inventory));
            }
        }

        // Generate Robots
        List<Robot> robots = new ArrayList<>();
        GridNode startNode = grid.stream().filter(n -> n.getType() == NodeType.WALKABLE).findFirst()
                .orElse(grid.get(0));
        robots.add(new Robot("Android 1", startNode, 100.0, RobotState.IDLE, 1.0, 0.0, null));
        robots.add(new Robot("Android 2", grid.get(10), 100.0, RobotState.IDLE, 1.0, 0.0, null));
        robots.add(new Robot("Android 3", grid.get(20), 100.0, RobotState.IDLE, 1.0, 0.0, null));

        WarehouseKpis kpis = WarehouseKpis.builder()
                .activeRobots(3)
                .averageBatteryLevel(100.0)
                .averageRobotUtilization(0)
                .pendingOrders(0)
                .totalOrdersCompleted(0)
                .throughputPerHour(0)
                .build();

        return Warehouse.builder()
                .id(UUID.randomUUID())
                .width(width)
                .height(height)
                .grid(grid)
                .robots(robots)
                .activeOrders(new ArrayList<>())
                .completedOrders(new ArrayList<>())
                .kpis(kpis)
                .build();
    }
}
