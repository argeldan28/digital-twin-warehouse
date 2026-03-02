package com.warehouse.digitaltwin.infrastructure.persistence.entities;

import com.warehouse.digitaltwin.domain.model.RobotState;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "robots")
public class RobotEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RobotState state;

    private double batteryLevel;
    private double speed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_node_id")
    private GridNodeEntity currentNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public RobotState getState() { return state; }
    public void setState(RobotState state) { this.state = state; }
    public double getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(double batteryLevel) { this.batteryLevel = batteryLevel; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public GridNodeEntity getCurrentNode() { return currentNode; }
    public void setCurrentNode(GridNodeEntity currentNode) { this.currentNode = currentNode; }
    public WarehouseEntity getWarehouse() { return warehouse; }
    public void setWarehouse(WarehouseEntity warehouse) { this.warehouse = warehouse; }
}
