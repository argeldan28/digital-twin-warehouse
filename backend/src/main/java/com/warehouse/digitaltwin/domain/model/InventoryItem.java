package com.warehouse.digitaltwin.domain.model;

import java.util.UUID;

public class InventoryItem {
    private UUID id;
    private Product product;
    private int quantity;
    private GridNode location;

    public InventoryItem() {
    }

    public InventoryItem(UUID id, Product product, int quantity, GridNode location) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public GridNode getLocation() {
        return location;
    }

    public void setLocation(GridNode location) {
        this.location = location;
    }
}
