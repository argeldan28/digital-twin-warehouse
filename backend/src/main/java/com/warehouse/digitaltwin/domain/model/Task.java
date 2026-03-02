package com.warehouse.digitaltwin.domain.model;

import java.util.UUID;
import java.util.List;

public class Task {
    private Order order;
    private int pathIndex;
    private UUID id;
    private UUID orderId;
    private TaskType type;
    private GridNode targetLocation;
    private List<GridNode> currentPath;

    public Task() {
    }

    public Task(UUID id, UUID orderId, TaskType type, GridNode targetLocation, List<GridNode> currentPath) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.targetLocation = targetLocation;
        this.currentPath = currentPath;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public TaskType getType() {
        return type;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public GridNode getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(GridNode targetLocation) {
        this.targetLocation = targetLocation;
    }

    public List<GridNode> getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(List<GridNode> currentPath) {
        this.currentPath = currentPath;
    }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }
    public int getPathIndex() { return pathIndex; }
    public void setPathIndex(int pathIndex) { this.pathIndex = pathIndex; }
    public java.util.List<GridNode> getPath() { return currentPath; }
    }
