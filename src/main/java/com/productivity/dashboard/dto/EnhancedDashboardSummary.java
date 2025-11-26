package com.productivity.dashboard.dto;

import java.util.List;
import java.util.Map;

public class EnhancedDashboardSummary {
    
    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private double onTimeCompletionPercent;
    private Map<String, Double> productivityScores;
    private List<UserTaskStats> userStats;
    private long overdueTasks;
    private long highPriorityTasks;
    
    public EnhancedDashboardSummary() {
    }
    
    public EnhancedDashboardSummary(long totalTasks, long completedTasks, long pendingTasks, 
                                   long inProgressTasks, double onTimeCompletionPercent,
                                   Map<String, Double> productivityScores, List<UserTaskStats> userStats,
                                   long overdueTasks, long highPriorityTasks) {
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.inProgressTasks = inProgressTasks;
        this.onTimeCompletionPercent = onTimeCompletionPercent;
        this.productivityScores = productivityScores;
        this.userStats = userStats;
        this.overdueTasks = overdueTasks;
        this.highPriorityTasks = highPriorityTasks;
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
    
    public long getInProgressTasks() {
        return inProgressTasks;
    }
    
    public void setInProgressTasks(long inProgressTasks) {
        this.inProgressTasks = inProgressTasks;
    }
    
    public double getOnTimeCompletionPercent() {
        return onTimeCompletionPercent;
    }
    
    public void setOnTimeCompletionPercent(double onTimeCompletionPercent) {
        this.onTimeCompletionPercent = onTimeCompletionPercent;
    }
    
    public Map<String, Double> getProductivityScores() {
        return productivityScores;
    }
    
    public void setProductivityScores(Map<String, Double> productivityScores) {
        this.productivityScores = productivityScores;
    }
    
    public List<UserTaskStats> getUserStats() {
        return userStats;
    }
    
    public void setUserStats(List<UserTaskStats> userStats) {
        this.userStats = userStats;
    }
    
    public long getOverdueTasks() {
        return overdueTasks;
    }
    
    public void setOverdueTasks(long overdueTasks) {
        this.overdueTasks = overdueTasks;
    }
    
    public long getHighPriorityTasks() {
        return highPriorityTasks;
    }
    
    public void setHighPriorityTasks(long highPriorityTasks) {
        this.highPriorityTasks = highPriorityTasks;
    }
}
