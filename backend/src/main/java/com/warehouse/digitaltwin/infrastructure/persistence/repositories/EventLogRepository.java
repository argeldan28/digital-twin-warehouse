package com.warehouse.digitaltwin.infrastructure.persistence.repositories;

import com.warehouse.digitaltwin.infrastructure.persistence.entities.EventLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventLogRepository extends JpaRepository<EventLogEntity, UUID> {
    List<EventLogEntity> findByWarehouseIdOrderByTimestampAsc(UUID warehouseId);
}
