package com.warehouse.digitaltwin.domain;

import com.warehouse.digitaltwin.domain.model.GridNode;
import com.warehouse.digitaltwin.domain.model.NodeType;
import com.warehouse.digitaltwin.domain.model.Robot;
import com.warehouse.digitaltwin.domain.model.RobotState;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DomainModelTest {

    @Test
    void testGridNodeEquality() {
        GridNode node1 = new GridNode(10, 20, NodeType.WALKABLE);
        GridNode node2 = new GridNode(10, 20, NodeType.WALKABLE);
        GridNode node3 = new GridNode(10, 21, NodeType.WALKABLE);

        assertEquals(node1, node2);
        assertNotEquals(node1, node3);
    }

    @Test
    void testRobotCreation() {
        GridNode startNode = new GridNode(0, 0, NodeType.STATION);
        Robot robot = Robot.builder()
                .id(UUID.randomUUID())
                .currentNode(startNode)
                .batteryLevel(100.0)
                .state(RobotState.IDLE)
                .speed(1.0)
                .build();

        assertNotNull(robot.getId());
        assertEquals(RobotState.IDLE, robot.getState());
        assertEquals(100.0, robot.getBatteryLevel(), 0.001);
        assertEquals(startNode, robot.getCurrentNode());

        // Test state transition (simulating a use case)
        robot.setState(RobotState.MOVING);
        assertEquals(RobotState.MOVING, robot.getState());
    }
}
