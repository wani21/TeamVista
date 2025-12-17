package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.ActivityDTO;
import com.productivity.dashboard.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing user activities and audit trail
 */
public interface ActivityService {
    
    /**
     * Log a user activity for audit trail
     */
    void logActivity(User user, String action, String entityType, Long entityId, String details);
    
    /**
     * Get paginated activities for a specific user
     */
    Page<ActivityDTO> getUserActivities(Long userId, Pageable pageable);
    
    /**
     * Get paginated activities for a team (multiple users)
     */
    Page<ActivityDTO> getTeamActivities(List<Long> userIds, Pageable pageable);
    
    /**
     * Get all activities (admin only)
     */
    Page<ActivityDTO> getAllActivities(Pageable pageable);
    
    /**
     * Get user activities within a date range
     */
    List<ActivityDTO> getUserActivitiesInRange(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Count user activities since a specific date
     */
    Long getUserActivityCount(Long userId, LocalDateTime since);
    
    /**
     * Get recent activities for activity feed
     */
    List<ActivityDTO> getRecentActivities(int limit);
}