package com.warehouse.digitaltwin.domain.model;

import java.util.Objects;

public class GridNode {
    private int x;
    private int y;
    private NodeType type;

    // Optional: Only used if this grid node is a SHELF
    private InventoryItem inventory;

    public GridNode() {
    }

    public GridNode(int x, int y, NodeType type, InventoryItem inventory) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.inventory = inventory;
    }

    public GridNode(int x, int y, NodeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.inventory = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        GridNode gridNode = (GridNode) o;
        return x == gridNode.x && y == gridNode.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public InventoryItem getInventory() {
        return inventory;
    }

    public void setInventory(InventoryItem inventory) {
        this.inventory = inventory;
    }
}
