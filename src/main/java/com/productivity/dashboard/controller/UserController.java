package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.ApiResponse;
import com.productivity.dashboard.dto.UserTaskStats;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.service.AuthService;
import com.productivity.dashboard.service.TaskService;
import com.productivity.dashboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private TaskService taskService;
    
    /**
     * Get all users (for task assignment dropdown)
     * GET /api/users
     * Response: {"success": true, "message": "Users retrieved successfully", "data": [...]}
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Users retrieved successfully", users));
    }
    
    /**
     * Get current logged-in user profile
     * GET /api/users/me
     * Response: {"success": true, "message": "User profile retrieved", "data": {...}}
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<User>> getCurrentUser() {
        User user = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", user));
    }
    
    /**
     * Get user by ID
     * GET /api/users/1
     * Response: {"success": true, "message": "User retrieved successfully", "data": {...}}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }
    
    /**
     * Get task statistics for a specific user
     * GET /api/users/1/stats
     * Response: {"success": true, "message": "User statistics retrieved", "data": {...}}
     */
    @GetMapping("/{id}/stats")
    public ResponseEntity<ApiResponse<UserTaskStats>> getUserTaskStats(@PathVariable Long id) {
        UserTaskStats stats = taskService.getUserTaskStats(id);
        return ResponseEntity.ok(ApiResponse.success("User statistics retrieved", stats));
    }
}
