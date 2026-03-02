package com.warehouse.digitaltwin.infrastructure.persistence.entities;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "warehouses")
public class WarehouseEntity {

    @Id
    private UUID id;

    private String name;
    private int width;
    private int height;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GridNodeEntity> gridNodes;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RobotEntity> robots;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    public List<GridNodeEntity> getGridNodes() { return gridNodes; }
    public void setGridNodes(List<GridNodeEntity> gridNodes) { this.gridNodes = gridNodes; }
    public List<RobotEntity> getRobots() { return robots; }
    public void setRobots(List<RobotEntity> robots) { this.robots = robots; }
}
