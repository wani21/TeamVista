package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.ApiResponse;
import com.productivity.dashboard.dto.TimeEntryDTO;
import com.productivity.dashboard.dto.TimeEntryRequest;
import com.productivity.dashboard.service.AuthService;
import com.productivity.dashboard.service.TimeTrackingService;
import com.productivity.dashboard.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for time tracking functionality
 */
@RestController
@RequestMapping("/api/time-tracking")
@CrossOrigin(origins = "*")
public class TimeTrackingController {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeTrackingController.class);
    
    @Autowired
    private TimeTrackingService timeTrackingService;
    
    @Autowired
    private AuthService authService;
    
    /**
     * Start a timer for the current user
     * POST /api/time-tracking/start?taskId=1&description=Working on feature
     */
    @PostMapping("/start")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> startTimer(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) String description) {
        
        logger.info("POST /api/time-tracking/start - taskId: {}", taskId);
        
        try {
            User currentUser = authService.getCurrentUser();
            TimeEntryDTO entry = timeTrackingService.startTimer(currentUser, taskId, description);
            
            logger.info("Timer started successfully for user: {}", currentUser.getEmail());
            
            return ResponseEntity.ok(ApiResponse.success("Timer started successfully", entry));
        } catch (Exception e) {
            logger.error("Failed to start timer - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Stop the running timer for the current user
     * POST /api/time-tracking/stop
     */
    @PostMapping("/stop")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> stopTimer() {
        logger.info("POST /api/time-tracking/stop");
        
        try {
            User currentUser = authService.getCurrentUser();
            TimeEntryDTO entry = timeTrackingService.stopTimer(currentUser);
            
            logger.info("Timer stopped successfully for user: {}, duration: {} minutes", 
                currentUser.getEmail(), entry.getDurationMinutes());
            
            return ResponseEntity.ok(ApiResponse.success("Timer stopped successfully", entry));
        } catch (Exception e) {
            logger.error("Failed to stop timer - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get the currently running timer for the current user
     * GET /api/time-tracking/running
     */
    @GetMapping("/running")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> getRunningTimer() {
        logger.info("GET /api/time-tracking/running");
        
        try {
            User currentUser = authService.getCurrentUser();
            TimeEntryDTO entry = timeTrackingService.getRunningTimer(currentUser);
            
            if (entry != null) {
                logger.debug("Running timer found for user: {}", currentUser.getEmail());
                return ResponseEntity.ok(ApiResponse.success("Running timer found", entry));
            } else {
                logger.debug("No running timer for user: {}", currentUser.getEmail());
                return ResponseEntity.ok(ApiResponse.success("No running timer", null));
            }
        } catch (Exception e) {
            logger.error("Failed to get running timer - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Create a manual time entry
     * POST /api/time-tracking/manual
     */
    @PostMapping("/manual")
    public ResponseEntity<ApiResponse<TimeEntryDTO>> createManualEntry(@Valid @RequestBody TimeEntryRequest request) {
        logger.info("POST /api/time-tracking/manual - taskId: {}", request.getTaskId());
        
        try {
            User currentUser = authService.getCurrentUser();
            TimeEntryDTO entry = timeTrackingService.createManualEntry(currentUser, request);
            
            logger.info("Manual time entry created successfully, duration: {} minutes", entry.getDurationMinutes());
            
            return ResponseEntity.ok(ApiResponse.success("Time entry created successfully", entry));
        } catch (Exception e) {
            logger.error("Failed to create manual time entry - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get time entries for a user within date range
     * GET /api/time-tracking/user/{userId}?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<List<TimeEntryDTO>>> getUserTimeEntries(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("GET /api/time-tracking/user/{} - start: {}, end: {}", userId, start, end);
        
        try {
            List<TimeEntryDTO> entries = timeTrackingService.getUserTimeEntries(userId, start, end);
            
            logger.info("Successfully retrieved {} time entries for user: {}", entries.size(), userId);
            
            return ResponseEntity.ok(ApiResponse.success("Time entries retrieved successfully", entries));
        } catch (Exception e) {
            logger.error("Failed to retrieve time entries for user {} - Error: {}", userId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get total hours worked by a user
     * GET /api/time-tracking/user/{userId}/total?start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping("/user/{userId}/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getTotalHoursWorked(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("GET /api/time-tracking/user/{}/total - start: {}, end: {}", userId, start, end);
        
        try {
            Long totalMinutes = timeTrackingService.getTotalMinutesWorked(userId, start, end);
            double totalHours = totalMinutes / 60.0;
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("totalMinutes", totalMinutes);
            result.put("totalHours", totalHours);
            result.put("startDate", start);
            result.put("endDate", end);
            
            logger.info("User {} worked {} hours ({} minutes)", userId, totalHours, totalMinutes);
            
            return ResponseEntity.ok(ApiResponse.success("Total hours calculated successfully", result));
        } catch (Exception e) {
            logger.error("Failed to calculate total hours for user {} - Error: {}", userId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get team time entries
     * GET /api/time-tracking/team?userIds=1,2,3&start=2024-01-01T00:00:00&end=2024-12-31T23:59:59
     */
    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<TimeEntryDTO>>> getTeamTimeEntries(
            @RequestParam List<Long> userIds,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        logger.info("GET /api/time-tracking/team - {} users, start: {}, end: {}", userIds.size(), start, end);
        
        try {
            List<TimeEntryDTO> entries = timeTrackingService.getTeamTimeEntries(userIds, start, end);
            
            logger.info("Successfully retrieved {} team time entries", entries.size());
            
            return ResponseEntity.ok(ApiResponse.success("Team time entries retrieved successfully", entries));
        } catch (Exception e) {
            logger.error("Failed to retrieve team time entries - Error: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Get time entries for a specific task
     * GET /api/time-tracking/task/{taskId}
     */
    @GetMapping("/task/{taskId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApiResponse<List<TimeEntryDTO>>> getTaskTimeEntries(@PathVariable Long taskId) {
        logger.info("GET /api/time-tracking/task/{}", taskId);
        
        try {
            List<TimeEntryDTO> entries = timeTrackingService.getTaskTimeEntries(taskId);
            
            logger.info("Successfully retrieved {} time entries for task: {}", entries.size(), taskId);
            
            return ResponseEntity.ok(ApiResponse.success("Task time entries retrieved successfully", entries));
        } catch (Exception e) {
            logger.error("Failed to retrieve time entries for task {} - Error: {}", taskId, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Delete a time entry
     * DELETE /api/time-tracking/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTimeEntry(@PathVariable Long id) {
        logger.info("DELETE /api/time-tracking/{}", id);
        
        try {
            User currentUser = authService.getCurrentUser();
            timeTrackingService.deleteTimeEntry(id, currentUser);
            
            logger.info("Time entry {} deleted successfully", id);
            
            return ResponseEntity.ok(ApiResponse.success("Time entry deleted successfully", null));
        } catch (Exception e) {
            logger.error("Failed to delete time entry {} - Error: {}", id, e.getMessage());
            throw e;
        }
    }
}