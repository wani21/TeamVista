package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.DashboardSummary;
import com.productivity.dashboard.dto.TaskCreateRequest;
import com.productivity.dashboard.dto.TaskUpdateRequest;
import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TaskStatus;
import com.productivity.dashboard.model.User;

import java.util.List;

public interface TaskService {
    
    Task createTask(TaskCreateRequest request);
    
    List<Task> getAllTasks(Long assignedToId, TaskStatus status);
    
    Task getTaskById(Long id);
    
    Task updateTask(Long id, TaskUpdateRequest request);
    
    Task completeTask(Long id);
    
    void deleteTask(Long id);
    
    List<Task> searchTasks(String keyword);
    
    DashboardSummary getDashboardSummary();
    
    com.productivity.dashboard.dto.EnhancedDashboardSummary getEnhancedDashboardSummary();
    
    com.productivity.dashboard.dto.UserTaskStats getUserTaskStats(Long userId);
}