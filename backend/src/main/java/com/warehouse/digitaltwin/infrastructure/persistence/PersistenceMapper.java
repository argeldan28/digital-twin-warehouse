
package com.warehouse.digitaltwin.infrastructure.persistence;

import com.warehouse.digitaltwin.domain.model.*;
import com.warehouse.digitaltwin.infrastructure.persistence.entities.*;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;
import java.util.List;

@Component
public class PersistenceMapper {

    public WarehouseEntity toWarehouseEntity(Warehouse domain) {
        if (domain == null)
            return null;
        WarehouseEntity entity = new WarehouseEntity();
        entity.setId(domain.getId());
        entity.setWidth(domain.getWidth());
        entity.setHeight(domain.getHeight());

        if (domain.getGrid() != null) {
            List<GridNodeEntity> gridNodes = domain.getGrid().stream().map(g -> {
                GridNodeEntity gn = toGridNodeEntity(g);
                gn.setWarehouse(entity);
                return gn;
            }).collect(Collectors.toList());
            entity.setGridNodes(gridNodes);
        }

        if (domain.getRobots() != null) {
            List<RobotEntity> robots = domain.getRobots().stream().map(r -> {
                RobotEntity re = toRobotEntity(r);
                re.setWarehouse(entity);
                if (r.getCurrentNode() != null && entity.getGridNodes() != null) {
                    for (GridNodeEntity gn : entity.getGridNodes()) {
                        if (gn.getX() == r.getCurrentNode().getX() && gn.getY() == r.getCurrentNode().getY()) {
                            re.setCurrentNode(gn);
                            break;
                        }
                    }
                }
                return re;
            }).collect(Collectors.toList());
            entity.setRobots(robots);
        }
        return entity;
    }

    public GridNodeEntity toGridNodeEntity(GridNode domain) {
        if (domain == null)
            return null;
        GridNodeEntity entity = new GridNodeEntity();
        entity.setX(domain.getX());
        entity.setY(domain.getY());
        entity.setType(domain.getType());
        return entity;
    }

    public RobotEntity toRobotEntity(Robot domain) {
        if (domain == null)
            return null;
        RobotEntity entity = new RobotEntity();
        entity.setId(domain.getId());
        entity.setState(domain.getState());
        entity.setBatteryLevel(domain.getBatteryLevel());
        entity.setSpeed(domain.getSpeed());
        // Deliberately skipped setting currentNode here to avoid transient issues
        // when saving warehouse as a whole, it gets populated inside toWarehouseEntity.
        return entity;
    }

    public OrderEntity toOrderEntity(Order domain) {
        if (domain == null)
            return null;
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
        if (domain == null)
            return null;
        OrderItemEntity entity = new OrderItemEntity();
        entity.setId(domain.getId());
        entity.setProductName(domain.getProductName());
        entity.setQuantity(domain.getQuantity());
        if (domain.getLocation() != null) {
            entity.setLocation(toGridNodeEntity(domain.getLocation()));
        }
        return entity;
    }
}
