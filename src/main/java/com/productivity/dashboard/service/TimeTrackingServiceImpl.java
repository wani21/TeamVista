package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.TimeEntryDTO;
import com.productivity.dashboard.dto.TimeEntryRequest;
import com.productivity.dashboard.exception.BadRequestException;
import com.productivity.dashboard.exception.ForbiddenException;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TimeEntry;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.TaskRepository;
import com.productivity.dashboard.repository.TimeEntryRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation for time tracking functionality
 */
@Service
@Transactional
public class TimeTrackingServiceImpl implements TimeTrackingService {
    
    private static final Logger logger = LoggerFactory.getLogger(TimeTrackingServiceImpl.class);
    
    @Autowired
    private TimeEntryRepository timeEntryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private ActivityService activityService;
    
    @Override
    public TimeEntryDTO startTimer(User user, Long taskId, String description) {
        logger.info("Starting timer for user: {}, task: {}", user.getEmail(), taskId);
        
        // Check if user already has a running timer
        Optional<TimeEntry> runningTimer = timeEntryRepository.findRunningTimerByUser(user);
        if (runningTimer.isPresent()) {
            logger.warn("User {} already has a running timer", user.getEmail());
            throw new BadRequestException("You already have a running timer. Please stop it first.");
        }
        
        Task task = null;
        if (taskId != null) {
            task = taskRepository.findById(taskId)
                .orElseThrow(() -> {
                    logger.error("Task not found: {}", taskId);
                    return new NotFoundException("Task not found");
                });
        }
        
        TimeEntry timeEntry = new TimeEntry(user, task, LocalDateTime.now(), description);
        timeEntry.setIsManual(false);
        TimeEntry saved = timeEntryRepository.save(timeEntry);
        
        logger.info("Timer started successfully with ID: {}", saved.getId());
        
        // Log activity
        String taskInfo = task != null ? " for task: " + task.getTitle() : "";
        activityService.logActivity(user, "TIMER_STARTED", "TimeEntry", saved.getId(), 
            "Started timer" + taskInfo);
        
        return convertToDTO(saved);
    }
    
    @Override
    public TimeEntryDTO stopTimer(User user) {
        logger.info("Stopping timer for user: {}", user.getEmail());
        
        TimeEntry timeEntry = timeEntryRepository.findRunningTimerByUser(user)
            .orElseThrow(() -> {
                logger.warn("No running timer found for user: {}", user.getEmail());
                return new NotFoundException("No running timer found");
            });
        
        LocalDateTime endTime = LocalDateTime.now();
        timeEntry.setEndTime(endTime);
        
        // Calculate duration in minutes
        Duration duration = Duration.between(timeEntry.getStartTime(), endTime);
        timeEntry.setDurationMinutes(duration.toMinutes());
        
        TimeEntry saved = timeEntryRepository.save(timeEntry);
        logger.info("Timer stopped. Duration: {} minutes", saved.getDurationMinutes());
        
        // Log activity
        String taskInfo = timeEntry.getTask() != null ? " for task: " + timeEntry.getTask().getTitle() : "";
        activityService.logActivity(user, "TIMER_STOPPED", "TimeEntry", saved.getId(), 
            "Stopped timer" + taskInfo + " - Duration: " + saved.getDurationMinutes() + " minutes");
        
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public TimeEntryDTO getRunningTimer(User user) {
        logger.debug("Checking for running timer for user: {}", user.getEmail());
        
        Optional<TimeEntry> runningTimer = timeEntryRepository.findRunningTimerByUser(user);
        return runningTimer.map(this::convertToDTO).orElse(null);
    }
    
    @Override
    public TimeEntryDTO createManualEntry(User user, TimeEntryRequest request) {
        logger.info("Creating manual time entry for user: {}", user.getEmail());
        
        if (request.getStartTime() == null || request.getEndTime() == null) {
            logger.error("Start time and end time are required for manual entry");
            throw new BadRequestException("Start time and end time are required");
        }
        
        if (request.getEndTime().isBefore(request.getStartTime())) {
            logger.error("End time cannot be before start time");
            throw new BadRequestException("End time cannot be before start time");
        }
        
        Task task = null;
        if (request.getTaskId() != null) {
            task = taskRepository.findById(request.getTaskId())
                .orElseThrow(() -> new NotFoundException("Task not found"));
        }
        
        TimeEntry timeEntry = new TimeEntry(user, task, request.getStartTime(), request.getDescription());
        timeEntry.setEndTime(request.getEndTime());
        timeEntry.setIsManual(true);
        
        // Calculate duration
        Duration duration = Duration.between(request.getStartTime(), request.getEndTime());
        timeEntry.setDurationMinutes(duration.toMinutes());
        
        TimeEntry saved = timeEntryRepository.save(timeEntry);
        logger.info("Manual time entry created with ID: {}, duration: {} minutes", 
            saved.getId(), saved.getDurationMinutes());
        
        // Log activity
        activityService.logActivity(user, "TIME_ENTRY_CREATED", "TimeEntry", saved.getId(), 
            "Created manual time entry - Duration: " + saved.getDurationMinutes() + " minutes");
        
        return convertToDTO(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeEntryDTO> getUserTimeEntries(Long userId, LocalDateTime start, LocalDateTime end) {
        logger.info("Fetching time entries for user: {} between {} and {}", userId, start, end);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        List<TimeEntry> entries = timeEntryRepository.findByUserAndDateRange(user, start, end);
        logger.debug("Found {} time entries", entries.size());
        
        return entries.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getTotalMinutesWorked(Long userId, LocalDateTime start, LocalDateTime end) {
        logger.info("Calculating total minutes worked for user: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
        
        Long totalMinutes = timeEntryRepository.getTotalMinutesByUserAndDateRange(user, start, end);
        totalMinutes = totalMinutes != null ? totalMinutes : 0L;
        
        logger.debug("User {} worked {} minutes ({} hours)", userId, totalMinutes, totalMinutes / 60.0);
        return totalMinutes;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeEntryDTO> getTeamTimeEntries(List<Long> userIds, LocalDateTime start, LocalDateTime end) {
        logger.info("Fetching team time entries for {} users", userIds.size());
        
        List<TimeEntry> entries = timeEntryRepository.findByUserIdsAndDateRange(userIds, start, end);
        logger.debug("Found {} team time entries", entries.size());
        
        return entries.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public void deleteTimeEntry(Long id, User user) {
        logger.info("Deleting time entry: {} by user: {}", id, user.getEmail());
        
        TimeEntry timeEntry = timeEntryRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Time entry not found"));
        
        // Check if user owns this time entry or is a manager
        if (!timeEntry.getUser().getId().equals(user.getId()) && 
            !user.getRole().name().equals("MANAGER") && 
            !user.getRole().name().equals("ADMIN")) {
            logger.warn("User {} attempted to delete time entry {} they don't own", user.getEmail(), id);
            throw new ForbiddenException("You can only delete your own time entries");
        }
        
        timeEntryRepository.delete(timeEntry);
        logger.info("Time entry {} deleted successfully", id);
        
        // Log activity
        activityService.logActivity(user, "TIME_ENTRY_DELETED", "TimeEntry", id, 
            "Deleted time entry");
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<TimeEntryDTO> getTaskTimeEntries(Long taskId) {
        logger.info("Fetching time entries for task: {}", taskId);
        
        List<TimeEntry> entries = timeEntryRepository.findByTaskId(taskId);
        logger.debug("Found {} time entries for task", entries.size());
        
        return entries.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private TimeEntryDTO convertToDTO(TimeEntry entry) {
        TimeEntryDTO dto = new TimeEntryDTO();
        dto.setId(entry.getId());
        dto.setUserId(entry.getUser().getId());
        dto.setUserName(entry.getUser().getName());
        
        if (entry.getTask() != null) {
            dto.setTaskId(entry.getTask().getId());
            dto.setTaskTitle(entry.getTask().getTitle());
        }
        
        dto.setStartTime(entry.getStartTime());
        dto.setEndTime(entry.getEndTime());
        dto.setDurationMinutes(entry.getDurationMinutes());
        dto.setDescription(entry.getDescription());
        dto.setIsManual(entry.getIsManual());
        dto.setIsRunning(entry.getEndTime() == null);
        
        return dto;
    }
}