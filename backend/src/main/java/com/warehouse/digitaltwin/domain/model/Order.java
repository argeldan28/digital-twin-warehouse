package com.warehouse.digitaltwin.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private UUID id;
    private int priority;
    private LocalDateTime createdAt;
    private LocalDateTime slaTarget;
    private OrderState state;
    private List<OrderItem> items;
    private LocalDateTime completedAt;

    public Order() {
    }

    public Order(UUID id, int priority, LocalDateTime createdAt, LocalDateTime slaTarget, OrderState state,
            List<OrderItem> items, LocalDateTime completedAt) {
        this.id = id;
        this.priority = priority;
        this.createdAt = createdAt;
        this.slaTarget = slaTarget;
        this.state = state;
        this.items = items;
        this.completedAt = completedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSlaTarget() {
        return slaTarget;
    }

    public void setSlaTarget(LocalDateTime slaTarget) {
        this.slaTarget = slaTarget;
    }

    public OrderState getState() {
        return state;
    }

    public void setState(OrderState state) {
        this.state = state;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
