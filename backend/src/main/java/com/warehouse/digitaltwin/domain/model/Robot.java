package com.warehouse.digitaltwin.domain.model;

import java.util.UUID;

public class Robot {
    private UUID id;
    private String name;
    private GridNode currentNode;
    private RobotState state;
    private double batteryLevel;
    private UUID currentOrderId;

    public Robot() {
    }

    public Robot(UUID id, String name, GridNode currentNode, RobotState state, double batteryLevel,
            UUID currentOrderId) {
        this.id = id;
        this.name = name;
        this.currentNode = currentNode;
        this.state = state;
        this.batteryLevel = batteryLevel;
        this.currentOrderId = currentOrderId;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GridNode getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(GridNode currentNode) {
        this.currentNode = currentNode;
    }

    public RobotState getState() {
        return state;
    }

    public void setState(RobotState state) {
        this.state = state;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(double batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public UUID getCurrentOrderId() {
        return currentOrderId;
    }

    public void setCurrentOrderId(UUID currentOrderId) {
        this.currentOrderId = currentOrderId;
    }
}
