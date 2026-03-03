package com.warehouse.digitaltwin.infrastructure.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.warehouse.digitaltwin.domain.model.Robot;
import com.warehouse.digitaltwin.domain.model.Warehouse;
import com.warehouse.digitaltwin.infrastructure.persistence.entities.WarehouseEntity;
import com.warehouse.digitaltwin.infrastructure.persistence.repositories.GridNodeRepository;
import com.warehouse.digitaltwin.infrastructure.persistence.repositories.RobotRepository;
import com.warehouse.digitaltwin.infrastructure.persistence.repositories.WarehouseRepository;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class PersistenceService {

    private static final Logger log = LoggerFactory.getLogger(PersistenceService.class);

    private final WarehouseRepository warehouseRepository;
    private final RobotRepository robotRepository;
    private final GridNodeRepository gridNodeRepository;
    private final PersistenceMapper mapper;

    public PersistenceService(WarehouseRepository warehouseRepository, RobotRepository robotRepository,
            GridNodeRepository gridNodeRepository, PersistenceMapper mapper) {
        this.warehouseRepository = warehouseRepository;
        this.robotRepository = robotRepository;
        this.gridNodeRepository = gridNodeRepository;
        this.mapper = mapper;
    }

    @Transactional
    public void saveInitialWarehouse(Warehouse warehouse) {
        WarehouseEntity entity = mapper.toWarehouseEntity(warehouse);
        warehouseRepository.save(entity);
    }

    @Async
    @Transactional
    public void saveRobotStateAsync(Robot robot, Warehouse warehouse) {
        robotRepository.findById(robot.getId()).ifPresent(entity -> {
            entity.setState(robot.getState());
            entity.setBatteryLevel(robot.getBatteryLevel());

            // Find the corresponding GridNodeEntity for the robot's current position
            if (robot.getCurrentNode() != null) {
                gridNodeRepository.findByWarehouseIdAndXAndY(warehouse.getId(),
                        robot.getCurrentNode().getX(),
                        robot.getCurrentNode().getY()).ifPresent(entity::setCurrentNode);
            }

            robotRepository.save(entity);
        });
    }

    public final WarehouseRepository getWarehouseRepository() {
        return warehouseRepository;
    }

    public final RobotRepository getRobotRepository() {
        return robotRepository;
    }

    public final GridNodeRepository getGridNodeRepository() {
        return gridNodeRepository;
    }

    public final PersistenceMapper getMapper() {
        return mapper;
    }
}
