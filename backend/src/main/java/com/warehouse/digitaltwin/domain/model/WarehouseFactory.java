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
        Robot r1 = new Robot();
        r1.setId(UUID.randomUUID());
        r1.setName("Android 1");
        r1.setCurrentNode(startNode);
        r1.setBatteryLevel(100.0);
        r1.setState(RobotState.IDLE);
        r1.setSpeed(1.0);
        r1.setDistanceTraveled(0.0);
        robots.add(r1);

        Robot r2 = new Robot();
        r2.setId(UUID.randomUUID());
        r2.setName("Android 2");
        r2.setCurrentNode(grid.get(10));
        r2.setBatteryLevel(100.0);
        r2.setState(RobotState.IDLE);
        r2.setSpeed(1.0);
        r2.setDistanceTraveled(0.0);
        robots.add(r2);

        Robot r3 = new Robot();
        r3.setId(UUID.randomUUID());
        r3.setName("Android 3");
        r3.setCurrentNode(grid.get(20));
        r3.setBatteryLevel(100.0);
        r3.setState(RobotState.IDLE);
        r3.setSpeed(1.0);
        r3.setDistanceTraveled(0.0);
        robots.add(r3);

        WarehouseKpis kpis = new WarehouseKpis();
        kpis.setOrdersCompleted(0);
        kpis.setAverageSlaCompliance(100.0);
        kpis.setCurrentThroughput(0.0);

        Warehouse w = new Warehouse();
        w.setId(UUID.randomUUID());
        w.setWidth(width);
        w.setHeight(height);
        w.setGrid(grid);
        w.setRobots(robots);
        w.setActiveOrders(new ArrayList<>());
        w.setCompletedOrders(new ArrayList<>());
        w.setKpis(kpis);

        return w;
    }
}
