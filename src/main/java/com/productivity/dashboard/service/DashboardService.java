package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.DashboardSummary;

/**
 * Service interface for dashboard analytics
 */
public interface DashboardService {

    /**
     * Get dashboard summary with all analytics metrics
     * @return dashboard summary with metrics and productivity scores
     */
    DashboardSummary getSummary();
}