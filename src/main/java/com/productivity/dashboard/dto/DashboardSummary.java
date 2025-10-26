package com.productivity.dashboard.dto;

import java.util.List;

/**
 * DTO for dashboard summary containing all analytics metrics
 */
public class DashboardSummary {

    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private double onTimeCompletionPercent;
    private List<ProductivityScore> productivityScores;

    // Default constructor
    public DashboardSummary() {}

    // Constructor
    public DashboardSummary(long totalTasks, long completedTasks, long pendingTasks, 
                           double onTimeCompletionPercent, List<ProductivityScore> productivityScores) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.onTimeCompletionPercent = onTimeCompletionPercent;
        this.productivityScores = productivityScores;
    }

    // Getters and Setters
    public long getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(long totalTasks) {
        this.totalTasks = totalTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public double getOnTimeCompletionPercent() {
        return onTimeCompletionPercent;
    }

    public void setOnTimeCompletionPercent(double onTimeCompletionPercent) {
        this.onTimeCompletionPercent = onTimeCompletionPercent;
    }

    public List<ProductivityScore> getProductivityScores() {
        return productivityScores;
    }

    public void setProductivityScores(List<ProductivityScore> productivityScores) {
        this.productivityScores = productivityScores;
    }
}