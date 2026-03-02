package com.warehouse.digitaltwin.domain.model;

import java.util.List;
import java.util.UUID;

public class Warehouse {
    private UUID id;
    private int width;
    private int height;
    private List<GridNode> grid;
    private List<Robot> robots;
    private List<Order> activeOrders;
    private List<Order> completedOrders;
    private WarehouseKpis kpis;

    public Warehouse() {
        this.completedOrders = new java.util.ArrayList<>();
    }

    public Warehouse(UUID id, int width, int height, List<GridNode> grid, List<Robot> robots, List<Order> activeOrders,
            List<Order> completedOrders, WarehouseKpis kpis) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.grid = grid;
        this.robots = robots;
        this.activeOrders = activeOrders;
        this.completedOrders = completedOrders;
        this.kpis = kpis;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<GridNode> getGrid() {
        return grid;
    }

    public void setGrid(List<GridNode> grid) {
        this.grid = grid;
    }

    public List<Robot> getRobots() {
        return robots;
    }

    public void setRobots(List<Robot> robots) {
        this.robots = robots;
    }

    public List<Order> getActiveOrders() {
        return activeOrders;
    }

    public void setActiveOrders(List<Order> activeOrders) {
        this.activeOrders = activeOrders;
    }

    public List<Order> getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(List<Order> completedOrders) {
        this.completedOrders = completedOrders;
    }

    public WarehouseKpis getKpis() {
        return kpis;
    }

    public void setKpis(WarehouseKpis kpis) {
        this.kpis = kpis;
    }

    public GridNode getNode(int x, int y) {
        if (grid == null)
            return null;
        return grid.stream()
                .filter(node -> node.getX() == x && node.getY() == y)
                .findFirst()
                .orElse(null);
    }
}
