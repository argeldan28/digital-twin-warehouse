package com.warehouse.digitaltwin.infrastructure.persistence.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private GridNodeEntity location;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "quantity")
    private int quantity;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public OrderEntity getOrder() { return order; }
    public void setOrder(OrderEntity order) { this.order = order; }
    public GridNodeEntity getLocation() { return location; }
    public void setLocation(GridNodeEntity location) { this.location = location; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
