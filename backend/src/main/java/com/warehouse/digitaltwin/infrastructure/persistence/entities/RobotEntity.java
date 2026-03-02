package com.warehouse.digitaltwin.infrastructure.persistence.entities;

import com.warehouse.digitaltwin.domain.model.RobotState;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "robots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RobotEntity {

    @Id
    private UUID id;

    @Enumerated(EnumType.STRING)
    private RobotState state;

    private double batteryLevel;
    private double speed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_node_id")
    private GridNodeEntity currentNode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id")
    private WarehouseEntity warehouse;
}
