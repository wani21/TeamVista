package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.DashboardSummary;
import com.productivity.dashboard.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for dashboard analytics endpoints
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * Get dashboard summary with analytics
     * GET /api/dashboard/summary
     * 
     * Response: {
     *   "totalTasks": 10,
     *   "completedTasks": 6,
     *   "pendingTasks": 4,
     *   "onTimeCompletionPercent": 83.33,
     *   "productivityScores": [
     *     {"userId": 1, "userName": "John Doe", "score": 75.0, "totalTasks": 4, "completedTasks": 3},
     *     {"userId": 2, "userName": "Jane Smith", "score": 100.0, "totalTasks": 3, "completedTasks": 3}
     *   ]
     * }
     */
    @GetMapping("/summary")
    public ResponseEntity<DashboardSummary> getDashboardSummary() {
        DashboardSummary summary = dashboardService.getSummary();
        return ResponseEntity.ok(summary);
    }
}