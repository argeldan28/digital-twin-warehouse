import os
import re

base_path = r"c:\GaldusSDAI\SDAI-Sacchi\digita-twin-warehouse\backend\src\main\java\com\warehouse\digitaltwin"

# Let's fix PersistenceMapper.java
pm_file = os.path.join(base_path, "infrastructure", "persistence", "PersistenceMapper.java")
with open(pm_file, 'r', encoding='utf-8') as f:
    pm_content = f.read()

# Replace WarehouseEntity builder
# The pattern is entity.method().method().build() etc. but it might be easier to just 
# write a quick script that replaces the entire mapper methods manually since there are only 4 methods

mapper_code = """
package com.warehouse.digitaltwin.infrastructure.persistence;

import com.warehouse.digitaltwin.domain.model.*;
import com.warehouse.digitaltwin.infrastructure.persistence.entities.*;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class PersistenceMapper {

    public WarehouseEntity toWarehouseEntity(Warehouse domain) {
        if (domain == null) return null;
        WarehouseEntity entity = new WarehouseEntity();
        entity.setId(domain.getId());
        entity.setWidth(domain.getWidth());
        entity.setHeight(domain.getHeight());
        
        if (domain.getGrid() != null) {
            entity.setGridNodes(domain.getGrid().stream().map(this::toGridNodeEntity).collect(Collectors.toList()));
        }
        if (domain.getRobots() != null) {
            entity.setRobots(domain.getRobots().stream().map(this::toRobotEntity).collect(Collectors.toList()));
        }
        return entity;
    }

    public GridNodeEntity toGridNodeEntity(GridNode domain) {
        if (domain == null) return null;
        GridNodeEntity entity = new GridNodeEntity();
        entity.setId(domain.getId());
        entity.setX(domain.getX());
        entity.setY(domain.getY());
        entity.setType(domain.getType().name());
        return entity;
    }

    public RobotEntity toRobotEntity(Robot domain) {
        if (domain == null) return null;
        RobotEntity entity = new RobotEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setState(domain.getState());
        entity.setBatteryLevel(domain.getBatteryLevel());
        entity.setSpeed(domain.getSpeed());
        if (domain.getCurrentNode() != null) {
            entity.setCurrentNodeId(domain.getCurrentNode().getId());
        }
        return entity;
    }

    public OrderEntity toOrderEntity(Order domain) {
        if (domain == null) return null;
        OrderEntity entity = new OrderEntity();
        entity.setId(domain.getId());
        entity.setPriority(domain.getPriority());
        entity.setState(domain.getState());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setCompletedAt(domain.getCompletedAt());
        if (domain.getItems() != null) {
            entity.setItems(domain.getItems().stream().map(this::toOrderItemEntity).collect(Collectors.toList()));
        }
        return entity;
    }

    public OrderItemEntity toOrderItemEntity(OrderItem domain) {
        if (domain == null) return null;
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(domain.getId());
        entity.setProductName(domain.getProductName());
        entity.setQuantity(domain.getQuantity());
        if (domain.getLocation() != null) {
            entity.setLocationId(domain.getLocation().getId());
        }
        return entity;
    }
}
"""

with open(pm_file, 'w', encoding='utf-8') as f:
    f.write(mapper_code)

print("Fixed PersistenceMapper.java")
