package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.ActivityDTO;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.Activity;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.ActivityRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing user activities and audit trail
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {
    
    private static final Logger logger = LoggerFactory.getLogger(ActivityServiceImpl.class);
    
    @Autowired
    private ActivityRepository activityRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public void logActivity(User user, String action, String entityType, Long entityId, String details) {
        logger.debug("Logging activity - User: {}, Action: {}, EntityType: {}, EntityId: {}", 
            user.getEmail(), action, entityType, entityId);
        
        try {
            Activity activity = new Activity(user, action, entityType, entityId, details);
            activityRepository.save(activity);
            logger.trace("Activity logged successfully with ID: {}", activity.getId());
        } catch (Exception e) {
            logger.error("Failed to log activity for user: {}, action: {} - Error: {}", 
                user.getEmail(), action, e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityDTO> getUserActivities(Long userId, Pageable pageable) {
        logger.info("Fetching activities for user ID: {}, page: {}, size: {}", 
            userId, pageable.getPageNumber(), pageable.getPageSize());
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new NotFoundException("User not found");
            });
        
        Page<Activity> activities = activityRepository.findByUserOrderByCreatedAtDesc(user, pageable);
        logger.debug("Found {} activities for user: {}", activities.getTotalElements(), user.getEmail());
        
        return activities.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityDTO> getTeamActivities(List<Long> userIds, Pageable pageable) {
        logger.info("Fetching team activities for {} users, page: {}, size: {}", 
            userIds.size(), pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Activity> activities = activityRepository.findTeamActivities(userIds, pageable);
        logger.debug("Found {} team activities", activities.getTotalElements());
        
        return activities.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<ActivityDTO> getAllActivities(Pageable pageable) {
        logger.info("Fetching all activities, page: {}, size: {}", 
            pageable.getPageNumber(), pageable.getPageSize());
        
        Page<Activity> activities = activityRepository.findAllActivities(pageable);
        logger.debug("Found {} total activities", activities.getTotalElements());
        
        return activities.map(this::convertToDTO);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ActivityDTO> getUserActivitiesInRange(Long userId, LocalDateTime start, LocalDateTime end) {
        logger.info("Fetching activities for user ID: {} between {} and {}", userId, start, end);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new NotFoundException("User not found");
            });
        
        List<Activity> activities = activityRepository
            .findByUserAndCreatedAtBetweenOrderByCreatedAtDesc(user, start, end);
        logger.debug("Found {} activities in date range for user: {}", activities.size(), user.getEmail());
        
        return activities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long getUserActivityCount(Long userId, LocalDateTime since) {
        logger.info("Counting activities for user ID: {} since {}", userId, since);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                logger.error("User not found with ID: {}", userId);
                return new NotFoundException("User not found");
            });
        
        Long count = activityRepository.countUserActivitiesSince(user, since);
        logger.debug("User {} has {} activities since {}", user.getEmail(), count, since);
        
        return count;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ActivityDTO> getRecentActivities(int limit) {
        logger.info("Fetching {} recent activities", limit);
        
        LocalDateTime since = LocalDateTime.now().minusHours(24); // Last 24 hours
        Pageable pageable = PageRequest.of(0, limit);
        
        List<Activity> activities = activityRepository.findRecentActivities(since, pageable);
        logger.debug("Found {} recent activities", activities.size());
        
        return activities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private ActivityDTO convertToDTO(Activity activity) {
        ActivityDTO dto = new ActivityDTO();
        dto.setId(activity.getId());
        dto.setUserId(activity.getUser().getId());
        dto.setUserName(activity.getUser().getName());
        dto.setUserEmail(activity.getUser().getEmail());
        dto.setAction(activity.getAction());
        dto.setEntityType(activity.getEntityType());
        dto.setEntityId(activity.getEntityId());
        dto.setDetails(activity.getDetails());
        dto.setCreatedAt(activity.getCreatedAt());
        return dto;
    }
}