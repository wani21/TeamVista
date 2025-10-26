package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.TaskRequest;
import com.productivity.dashboard.dto.TaskResponse;
import com.productivity.dashboard.model.TaskStatus;
import com.productivity.dashboard.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for task management endpoints
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    /**
     * Create a new task (MANAGER only)
     * POST /api/tasks
     * 
     * Request: {"title": "Complete project", "description": "Finish the dashboard", "status": "PENDING", "priority": "HIGH", "dueDate": "2025-12-31", "assignedToId": 2}
     * Response: {"id": 1, "title": "Complete project", "description": "Finish the dashboard", "status": "PENDING", "priority": "HIGH", "dueDate": "2025-12-31", "assignedTo": {"id": 2, "name": "John Doe", "email": "john@example.com"}, "createdAt": "2025-10-26T10:30:00"}
     */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest taskRequest, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        TaskResponse response = taskService.createTask(taskRequest, currentUserId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all tasks with optional filters
     * GET /api/tasks?assignedTo=2&status=PENDING
     * 
     * Response: [{"id": 1, "title": "Complete project", ...}, {"id": 2, "title": "Review code", ...}]
     */
    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(
            @RequestParam(required = false) Long assignedTo,
            @RequestParam(required = false) TaskStatus status) {
        List<TaskResponse> tasks = taskService.getTasks(assignedTo, status);
        return ResponseEntity.ok(tasks);
    }

    /**
     * Get task by ID
     * GET /api/tasks/1
     * 
     * Response: {"id": 1, "title": "Complete project", "description": "Finish the dashboard", ...}
     */
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.getTaskById(id);
        return ResponseEntity.ok(task);
    }

    /**
     * Update task (MANAGER or assigned employee for status update)
     * PUT /api/tasks/1
     * 
     * Request: {"title": "Updated project", "description": "Updated description", "status": "IN_PROGRESS", "priority": "MEDIUM", "dueDate": "2025-12-31", "assignedToId": 2}
     * Response: {"id": 1, "title": "Updated project", "description": "Updated description", "status": "IN_PROGRESS", ...}
     */
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id, 
                                                 @Valid @RequestBody TaskRequest taskRequest, 
                                                 Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        TaskResponse response = taskService.updateTask(id, taskRequest, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * Mark task as completed
     * PUT /api/tasks/1/complete
     * 
     * Response: {"id": 1, "title": "Complete project", "status": "COMPLETED", "completedDate": "2025-10-26", ...}
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id, Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();
        TaskResponse response = taskService.completeTask(id, currentUserId);
        return ResponseEntity.ok(response);
    }
}