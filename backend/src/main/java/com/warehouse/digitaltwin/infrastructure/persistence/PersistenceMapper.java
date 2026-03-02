package com.warehouse.digitaltwin.infrastructure.persistence;

import com.warehouse.digitaltwin.domain.model.*;
import com.warehouse.digitaltwin.infrastructure.persistence.entities.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class PersistenceMapper {

    public WarehouseEntity toEntity(Warehouse warehouse) {
        if (warehouse == null)
            return null;

        WarehouseEntity entity = WarehouseEntity.builder()
                .id(warehouse.getId())
                .width(warehouse.getWidth())
                .height(warehouse.getHeight())
                .build();

        entity.setGridNodes(warehouse.getGrid().stream()
                .map(node -> toEntity(node, entity))
                .collect(Collectors.toList()));

        entity.setRobots(warehouse.getRobots().stream()
                .map(robot -> toEntity(robot, entity))
                .collect(Collectors.toList()));

        return entity;
    }

    public GridNodeEntity toEntity(GridNode node, WarehouseEntity warehouseEntity) {
        if (node == null)
            return null;
        return GridNodeEntity.builder()
                .x(node.getX())
                .y(node.getY())
                .type(node.getType())
                .warehouse(warehouseEntity)
                .build();
    }

    public RobotEntity toEntity(Robot robot, WarehouseEntity warehouseEntity) {
        if (robot == null)
            return null;
        return RobotEntity.builder()
                .id(robot.getId())
                .state(robot.getState())
                .batteryLevel(robot.getBatteryLevel())
                .speed(robot.getSpeed())
                .warehouse(warehouseEntity)
                .build();
    }

    public OrderEntity toEntity(Order order, WarehouseEntity warehouseEntity) {
        if (order == null)
            return null;
        OrderEntity entity = OrderEntity.builder()
                .id(order.getId())
                .priority(order.getPriority())
                .createdAt(order.getCreatedAt())
                .slaTarget(order.getSlaTarget())
                .completedAt(order.getCompletedAt())
                .state(order.getState())
                .warehouse(warehouseEntity)
                .build();

        if (order.getItems() != null) {
            entity.setItems(order.getItems().stream()
                    .map(item -> toEntity(item, entity))
                    .collect(Collectors.toList()));
        }
        return entity;
    }

    public OrderItemEntity toEntity(OrderItem item, OrderEntity orderEntity) {
        if (item == null)
            return null;
        return OrderItemEntity.builder()
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .order(orderEntity)
                .build();
    }
}
