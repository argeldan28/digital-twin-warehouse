package com.warehouse.digitaltwin.infrastructure.persistence.repositories;

import com.warehouse.digitaltwin.infrastructure.persistence.entities.GridNodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GridNodeRepository extends JpaRepository<GridNodeEntity, UUID> {
    Optional<GridNodeEntity> findByWarehouseIdAndXAndY(UUID warehouseId, int x, int y);
}
