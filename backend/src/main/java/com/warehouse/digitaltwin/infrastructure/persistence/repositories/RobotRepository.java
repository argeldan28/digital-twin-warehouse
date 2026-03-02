package com.warehouse.digitaltwin.infrastructure.persistence.repositories;

import com.warehouse.digitaltwin.infrastructure.persistence.entities.RobotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RobotRepository extends JpaRepository<RobotEntity, UUID> {
}
