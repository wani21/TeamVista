package com.productivity.dashboard.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * DTO for time entry creation requests
 */
public class TimeEntryRequest {
    
    private Long taskId;
    
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private String description;
    
    private Boolean isManual = false;
    
    // Getters and Setters
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Boolean getIsManual() {
        return isManual;
    }
    
    public void setIsManual(Boolean isManual) {
        this.isManual = isManual;
    }
}