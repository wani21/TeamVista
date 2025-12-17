package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.TimeEntryDTO;
import com.productivity.dashboard.dto.TimeEntryRequest;
import com.productivity.dashboard.model.User;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for time tracking functionality
 */
public interface TimeTrackingService {
    
    /**
     * Start a timer for a user (optionally linked to a task)
     */
    TimeEntryDTO startTimer(User user, Long taskId, String description);
    
    /**
     * Stop the running timer for a user
     */
    TimeEntryDTO stopTimer(User user);
    
    /**
     * Get the currently running timer for a user
     */
    TimeEntryDTO getRunningTimer(User user);
    
    /**
     * Create a manual time entry
     */
    TimeEntryDTO createManualEntry(User user, TimeEntryRequest request);
    
    /**
     * Get time entries for a user within date range
     */
    List<TimeEntryDTO> getUserTimeEntries(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Get total minutes worked by user in date range
     */
    Long getTotalMinutesWorked(Long userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Get team time entries
     */
    List<TimeEntryDTO> getTeamTimeEntries(List<Long> userIds, LocalDateTime start, LocalDateTime end);
    
    /**
     * Delete a time entry
     */
    void deleteTimeEntry(Long id, User user);
    
    /**
     * Get time entries for a specific task
     */
    List<TimeEntryDTO> getTaskTimeEntries(Long taskId);
}