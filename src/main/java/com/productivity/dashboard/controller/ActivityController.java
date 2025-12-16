package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.ActivityDTO;
import com.productivity.dashboard.dto.ApiResponse;
import com.productivity.dashboard.service.ActivityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for managing user activities and audit trail
 */
@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {
    
    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    
    @Autowired
    private ActivityService activityService;
    
    /**
     * Get user activities with pagination
     * GET /api/activities/user/{userId}?page=0&size=20
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getUserActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("GET /api/activities/user/{} - page: {}, size: {}", userId, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ActivityDTO> activities = activityService.getUserActivities(userId, pageable);
            
            logger.info("Successfully retrieved {} activities for user {}", 
                activities.getNumberOfElements(), userId);
            
            return ResponseEntity.ok(ApiResponse.success("Activities retrieved successfully", activities));
        } catch (Exception e) {
            logger.error("Failed to retrieve activities for user {} - Error: {}", userId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get team activities with pagination
     * GET /api/activities/team?userIds=1,2,3&page=0&size=20
     */
    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getTeamActivities(
            @RequestParam List<Long> userIds,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("GET /api/activities/team - userIds: {}, page: {}, size: {}", userIds, page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ActivityDTO> activities = activityService.getTeamActivities(userIds, pageable);
            
            logger.info("Successfully retrieved {} team activities", activities.getNumberOfElements());
            
            return ResponseEntity.ok(ApiResponse.success("Team activities retrieved successfully", activities));
        } catch (Exception e) {
            logger.error("Failed to retrieve team activities - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get all activities (admin only)
     * GET /api/activities/all?page=0&size=20
     */
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        logger.info("GET /api/activities/all - page: {}, size: {}", page, size);
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ActivityDTO> activities = activityService.getAllActivities(pageable);
            
            logger.info("Successfully retrieved {} total activities", activities.getNumberOfElements());
            
            return ResponseEntity.ok(ApiResponse.success("All activities retrieved successfully", activities));
        } catch (Exception e) {
            logger.error("Failed to retrieve all activities - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get user activities within date range
     * GET /api/activities/user/{userId}/range?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping("/user/{userId}/range")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getUserActivitiesInRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("GET /api/activities/user/{}/range - start: {}, end: {}", userId, start, end);
        
        try {
            List<ActivityDTO> activities = activityService.getUserActivitiesInRange(userId, start, end);
            
            logger.info("Successfully retrieved {} activities in date range for user {}", 
                activities.size(), userId);
            
            return ResponseEntity.ok(ApiResponse.success("Activities in range retrieved successfully", activities));
        } catch (Exception e) {
            logger.error("Failed to retrieve activities in range for user {} - Error: {}", userId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get user activity count since specific date
     * GET /api/activities/user/{userId}/count?since=2024-01-01T00:00:00
     */
    @GetMapping("/user/{userId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Long>> getUserActivityCount(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        
        logger.info("GET /api/activities/user/{}/count - since: {}", userId, since);
        
        try {
            Long count = activityService.getUserActivityCount(userId, since);
            
            logger.info("User {} has {} activities since {}", userId, count, since);
            
            return ResponseEntity.ok(ApiResponse.success("Activity count retrieved successfully", count));
        } catch (Exception e) {
            logger.error("Failed to count activities for user {} - Error: {}", userId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get recent activities for activity feed
     * GET /api/activities/recent?limit=10
     */
    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<ActivityDTO>>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {
        
        logger.info("GET /api/activities/recent - limit: {}", limit);
        
        try {
            List<ActivityDTO> activities = activityService.getRecentActivities(limit);
            
            logger.info("Successfully retrieved {} recent activities", activities.size());
            
            return ResponseEntity.ok(ApiResponse.success("Recent activities retrieved successfully", activities));
        } catch (Exception e) {
            logger.error("Failed to retrieve recent activities - Error: {}", e.getMessage());
            throw e;
        }
    }
}