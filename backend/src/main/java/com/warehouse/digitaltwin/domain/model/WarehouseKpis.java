package com.warehouse.digitaltwin.domain.model;

public class WarehouseKpis {
    private int ordersCompleted;
    private double averageSlaCompliance;
    private double currentThroughput;

    public WarehouseKpis() {
    }

    public WarehouseKpis(int ordersCompleted, double averageSlaCompliance, double currentThroughput) {
        this.ordersCompleted = ordersCompleted;
        this.averageSlaCompliance = averageSlaCompliance;
        this.currentThroughput = currentThroughput;
    }

    public int getOrdersCompleted() {
        return ordersCompleted;
    }

    public void setOrdersCompleted(int ordersCompleted) {
        this.ordersCompleted = ordersCompleted;
    }

    public double getAverageSlaCompliance() {
        return averageSlaCompliance;
    }

    public void setAverageSlaCompliance(double averageSlaCompliance) {
        this.averageSlaCompliance = averageSlaCompliance;
    }

    public double getCurrentThroughput() {
        return currentThroughput;
    }

    public void setCurrentThroughput(double currentThroughput) {
        this.currentThroughput = currentThroughput;
    }
}
