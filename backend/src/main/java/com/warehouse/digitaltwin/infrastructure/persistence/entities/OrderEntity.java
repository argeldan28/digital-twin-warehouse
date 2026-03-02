package com.warehouse.digitaltwin.infrastructure.persistence.entities;

import com.warehouse.digitaltwin.domain.model.OrderState;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    private UUID id;

    private int priority;
    private LocalDateTime createdAt;
    private LocalDateTime slaTarget;
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    private OrderState state;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> items;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getSlaTarget() { return slaTarget; }
    public void setSlaTarget(LocalDateTime slaTarget) { this.slaTarget = slaTarget; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public OrderState getState() { return state; }
    public void setState(OrderState state) { this.state = state; }
    public List<OrderItemEntity> getItems() { return items; }
    public void setItems(List<OrderItemEntity> items) { this.items = items; }
    public WarehouseEntity getWarehouse() { return warehouse; }
    public void setWarehouse(WarehouseEntity warehouse) { this.warehouse = warehouse; }
}
