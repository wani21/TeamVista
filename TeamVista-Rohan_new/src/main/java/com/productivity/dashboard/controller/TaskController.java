package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.ApiResponse;
import com.productivity.dashboard.dto.TaskCreateRequest;
import com.productivity.dashboard.dto.TaskUpdateRequest;
import com.productivity.dashboard.exception.ForbiddenException;
import com.productivity.dashboard.model.Role;
import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TaskStatus;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.service.AuthService;
import com.productivity.dashboard.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;


import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Create a new task (MANAGER only)
     * POST /api/tasks
     * Request: {"title": "Task Title", "description": "Task Description", "priority": "HIGH", "dueDate": "2024-12-31", "assignedToId": 1}
     * Response: {"success": true, "message": "Task created successfully", "data": {...}}
     */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Task>> createTask(@Valid @RequestBody TaskCreateRequest request) {
        Task task = taskService.createTask(request);
        return ResponseEntity.ok(ApiResponse.success("Task created successfully", task));
    }
    
    /**
     * Get all tasks with optional filters
     * GET /api/tasks?assignedTo=1&status=PENDING
     * Response: {"success": true, "message": "Tasks retrieved successfully", "data": [...]}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> getAllTasks(
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) TaskStatus status) {
        List<Task> tasks = taskService.getAllTasks(assignedTo, status);
        return ResponseEntity.ok(ApiResponse.success("Tasks retrieved successfully", tasks));
    }
    
    /**
     * Get task by ID
     * GET /api/tasks/1
     * Response: {"success": true, "message": "Task retrieved successfully", "data": {...}}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> getTaskById(@PathVariable Long id) {
        Task task = taskService.getTaskById(id);
        return ResponseEntity.ok(ApiResponse.success("Task retrieved successfully", task));
    }
    
    /**
     * Update task (MANAGER or assigned employee for status update)
     * PUT /api/tasks/1
     * Request: {"title": "Updated Title", "status": "IN_PROGRESS"}
     * Response: {"success": true, "message": "Task updated successfully", "data": {...}}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> updateTask(@PathVariable Long id, 
                                                       @RequestBody TaskUpdateRequest request) {
        User currentUser = authService.getCurrentUser();
        Task existingTask = taskService.getTaskById(id);
        
        // Check permissions: MANAGER can update anything, EMPLOYEE can only update status of assigned tasks
        if (currentUser.getRole() == Role.EMPLOYEE) {
            if (!existingTask.getAssignedTo().getId().equals(currentUser.getId())) {
                throw new ForbiddenException("You can only update your own tasks");
            }
            // Employee can only update status
            if (request.getTitle() != null || request.getDescription() != null || 
                request.getPriority() != null || request.getDueDate() != null || 
                request.getAssignedToId() != null) {
                throw new ForbiddenException("Employees can only update task status");
            }
        }
        
        Task task = taskService.updateTask(id, request);
        return ResponseEntity.ok(ApiResponse.success("Task updated successfully", task));
    }
    
    /**
     * Mark task as completed
     * PUT /api/tasks/1/complete
     * Response: {"success": true, "message": "Task completed successfully", "data": {...}}
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<Task>> completeTask(@PathVariable Long id) {
        User currentUser = authService.getCurrentUser();
        Task existingTask = taskService.getTaskById(id);
        
        // Check permissions: MANAGER can complete any task, EMPLOYEE can only complete assigned tasks
        if (currentUser.getRole() == Role.EMPLOYEE && 
            !existingTask.getAssignedTo().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You can only complete your own tasks");
        }
        
        Task task = taskService.completeTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task completed successfully", task));
    }
    
    /**
     * Delete task (MANAGER only)
     * DELETE /api/tasks/1
     * Response: {"success": true, "message": "Task deleted successfully"}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.ok(ApiResponse.success("Task deleted successfully", null));
    }
    
    /**
     * Search tasks by keyword
     * GET /api/tasks/search?keyword=feature
     * Response: {"success": true, "message": "Tasks found", "data": [...]}
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Task>>> searchTasks(@RequestParam String keyword) {
        List<Task> tasks = taskService.searchTasks(keyword);
        return ResponseEntity.ok(ApiResponse.success("Tasks found", tasks));
    }
}