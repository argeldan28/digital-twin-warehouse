package com.warehouse.digitaltwin.domain.model;

import java.util.UUID;

public class Product {
    private UUID id;
    private String sku;
    private String name;
    private String category;

    public Product() {
    }

    public Product(UUID id, String sku, String name, String category) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.category = category;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
