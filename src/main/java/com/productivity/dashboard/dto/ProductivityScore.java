package com.productivity.dashboard.dto;

/**
 * DTO for individual user productivity scores
 */
public class ProductivityScore {

    private Long userId;
    private String userName;
    private double score;
    private long totalTasks;
    private long completedTasks;

    // Default constructor
    public ProductivityScore() {}

    // Constructor
    public ProductivityScore(Long userId, String userName, double score, long totalTasks, long completedTasks) {
        this.userId = userId;
        this.userName = userName;
        this.score = score;
        this.totalTasks = totalTasks;
        this.completedTasks = completedTasks;
    }

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

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
}