package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.ApiResponse;
import com.productivity.dashboard.dto.DashboardSummary;
import com.productivity.dashboard.dto.EnhancedDashboardSummary;
import com.productivity.dashboard.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    
    @Autowired
    private TaskService taskService;
    
    /**
     * Get dashboard summary with analytics
     * GET /api/dashboard/summary
     * Response: {
     *   "success": true,
     *   "message": "Dashboard summary retrieved successfully",
     *   "data": {
     *     "totalTasks": 10,
     *     "completedTasks": 6,
     *     "pendingTasks": 4,
     *     "onTimeCompletionPercent": 60.0,
     *     "productivityScores": {"John Doe": 75.0, "Jane Smith": 85.0}
     *   }
     * }
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<DashboardSummary>> getDashboardSummary() {
        DashboardSummary summary = taskService.getDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Dashboard summary retrieved successfully", summary));
    }
    
    /**
     * Get enhanced dashboard summary with detailed analytics
     * GET /api/dashboard/enhanced
     * Response: Enhanced dashboard with user stats, overdue tasks, high priority tasks, etc.
     */
    @GetMapping("/enhanced")
    public ResponseEntity<ApiResponse<EnhancedDashboardSummary>> getEnhancedDashboardSummary() {
        EnhancedDashboardSummary summary = taskService.getEnhancedDashboardSummary();
        return ResponseEntity.ok(ApiResponse.success("Enhanced dashboard retrieved successfully", summary));
    }
}