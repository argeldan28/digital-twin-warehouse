package com.warehouse.digitaltwin.infrastructure.persistence.entities;

import com.warehouse.digitaltwin.domain.model.NodeType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "grid_nodes")
public class GridNodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int x;
    private int y;

    @Enumerated(EnumType.STRING)
    private NodeType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public NodeType getType() { return type; }
    public void setType(NodeType type) { this.type = type; }
    public WarehouseEntity getWarehouse() { return warehouse; }
    public void setWarehouse(WarehouseEntity warehouse) { this.warehouse = warehouse; }
}
