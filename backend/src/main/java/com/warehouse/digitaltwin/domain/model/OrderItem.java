package com.warehouse.digitaltwin.domain.model;

import java.util.UUID;

public class OrderItem {
    private UUID id;
    private GridNode location;
    private String productName;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(UUID id, GridNode location, String productName, int quantity) {
        this.id = id;
        this.location = location;
        this.productName = productName;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public GridNode getLocation() {
        return location;
    }

    public void setLocation(GridNode location) {
        this.location = location;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
