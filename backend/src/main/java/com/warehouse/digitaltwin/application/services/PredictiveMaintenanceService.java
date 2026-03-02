package com.warehouse.digitaltwin.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.warehouse.digitaltwin.domain.model.Robot;
import com.warehouse.digitaltwin.domain.model.RobotState;
import org.springframework.stereotype.Service;

@Service

public class PredictiveMaintenanceService {

    private static final Logger log = LoggerFactory.getLogger(PredictiveMaintenanceService.class);

    private static final double MAINTENANCE_THRESHOLD_DISTANCE = 5000.0;
    private static final double BATTERY_DEGRADATION_THRESHOLD = 0.8; // 80% health

    // Simulate an AI model predicting failure probability
    public void analyzeRobotHealth(Robot robot) {
        if (robot.getState() == RobotState.ERROR || robot.getState() == RobotState.MAINTENANCE) {
            return;
        }

        // Feature 1: Distance traveled indicates motor wear
        if (robot.getDistanceTraveled() > MAINTENANCE_THRESHOLD_DISTANCE) {
            double failureProbability = calculateFailureProbability(robot.getDistanceTraveled());
            if (Math.random() < failureProbability) {
                log.warn(
                        "AI Predictive Maintenance: Robot {} flagged for motor maintenance due to wear and tear. Distance: {}",
                        robot.getId(), robot.getDistanceTraveled());
                robot.setState(RobotState.MAINTENANCE);
            }
        }

        // Feature 2: Battery health degradation
        // A random simulation of battery sudden drop
        if (robot.getBatteryLevel() < 30.0 && Math.random() < 0.05) {
            log.error("AI Predictive Maintenance: Robot {} predicted battery failure imminent! Forcing error state.",
                    robot.getId());
            robot.setState(RobotState.ERROR);
            robot.setBatteryLevel(0.0);
        }
    }

    private double calculateFailureProbability(double distanceTraveled) {
        // Simple sigmoid or linear probability based on distance over threshold
        double overage = distanceTraveled - MAINTENANCE_THRESHOLD_DISTANCE;
        return Math.min(0.8, overage / 10000.0); // Max 80% chance to fail per tick if extremely worn
    }
}
