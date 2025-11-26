package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.DashboardSummary;
import com.productivity.dashboard.dto.EnhancedDashboardSummary;
import com.productivity.dashboard.dto.TaskCreateRequest;
import com.productivity.dashboard.dto.TaskUpdateRequest;
import com.productivity.dashboard.dto.UserTaskStats;
import com.productivity.dashboard.model.Priority;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TaskStatus;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.TaskRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Task createTask(TaskCreateRequest request) {
        logger.info("Creating new task: {} assigned to user ID: {}", request.getTitle(), request.getAssignedToId());
        
        User assignedUser = userRepository.findById(request.getAssignedToId())
            .orElseThrow(() -> {
                logger.error("Task creation failed: User not found with id: {}", request.getAssignedToId());
                return new NotFoundException("User not found with id: " + request.getAssignedToId());
            });
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(assignedUser);
        
        Task savedTask = taskRepository.save(task);
        logger.info("Task created successfully with ID: {} - {}", savedTask.getId(), savedTask.getTitle());
        return savedTask;
    }
    
    @Override
    public List<Task> getAllTasks(Long assignedToId, TaskStatus status) {
        if (assignedToId != null && status != null) {
            User user = userRepository.findById(assignedToId)
                .orElseThrow(() -> new NotFoundException("User not found"));
            return taskRepository.findByAssignedToAndStatus(user, status);
        } else if (assignedToId != null) {
            User user = userRepository.findById(assignedToId)
                .orElseThrow(() -> new NotFoundException("User not found"));
            return taskRepository.findByAssignedTo(user);
        } else if (status != null) {
            return taskRepository.findByStatus(status);
        } else {
            return taskRepository.findAll();
        }
    }
    
    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Task not found with id: " + id));
    }
    
    @Override
    public Task updateTask(Long id, TaskUpdateRequest request) {
        Task task = getTaskById(id);
        
        if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedToId())
                .orElseThrow(() -> new NotFoundException("User not found"));
            task.setAssignedTo(assignedUser);
        }
        
        return taskRepository.save(task);
    }
    
    @Override
    public Task completeTask(Long id) {
        Task task = getTaskById(id);
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedDate(LocalDate.now());
        return taskRepository.save(task);
    }
    
    @Override
    public void deleteTask(Long id) {
        logger.info("Deleting task with ID: {}", id);
        Task task = getTaskById(id);
        taskRepository.delete(task);
        logger.info("Task deleted successfully: {} - {}", id, task.getTitle());
    }
    
    @Override
    public List<Task> searchTasks(String keyword) {
        logger.info("Searching tasks with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            logger.debug("Empty keyword, returning all tasks");
            return taskRepository.findAll();
        }
        List<Task> results = taskRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword);
        logger.info("Found {} tasks matching keyword: {}", results.size(), keyword);
        return results;
    }
    
    @Override
    public DashboardSummary getDashboardSummary() {
        logger.info("Generating dashboard summary");
        long totalTasks = taskRepository.countTotalTasks();
        long completedTasks = taskRepository.countCompletedTasks();
        long pendingTasks = taskRepository.countPendingTasks();
        logger.debug("Dashboard stats - Total: {}, Completed: {}, Pending: {}", totalTasks, completedTasks, pendingTasks);
        
        // Calculate on-time completion percentage (simplified)
        double onTimePercent = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        
        // Calculate productivity scores per user (simplified)
        Map<String, Double> productivityScores = new HashMap<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            List<Task> userTasks = taskRepository.findByAssignedTo(user);
            long userCompletedTasks = userTasks.stream()
                .mapToLong(task -> task.getStatus() == TaskStatus.COMPLETED ? 1 : 0)
                .sum();
            double score = userTasks.size() > 0 ? (double) userCompletedTasks / userTasks.size() * 100 : 0;
            productivityScores.put(user.getName(), score);
        }
        
        return new DashboardSummary(totalTasks, completedTasks, pendingTasks, onTimePercent, productivityScores);
    }
    
    @Override
    public EnhancedDashboardSummary getEnhancedDashboardSummary() {
        List<Task> allTasks = taskRepository.findAll();
        long totalTasks = allTasks.size();
        long completedTasks = allTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count();
        long pendingTasks = allTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.PENDING)
            .count();
        long inProgressTasks = allTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .count();
        
        // Calculate on-time completion percentage
        long completedOnTime = allTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED && 
                           task.getCompletedDate() != null && 
                           task.getDueDate() != null &&
                           !task.getCompletedDate().isAfter(task.getDueDate()))
            .count();
        double onTimePercent = completedTasks > 0 ? (double) completedOnTime / completedTasks * 100 : 0;
        
        // Calculate overdue tasks
        LocalDate today = LocalDate.now();
        long overdueTasks = allTasks.stream()
            .filter(task -> task.getStatus() != TaskStatus.COMPLETED &&
                           task.getDueDate() != null &&
                           task.getDueDate().isBefore(today))
            .count();
        
        // Calculate high priority tasks
        long highPriorityTasks = allTasks.stream()
            .filter(task -> task.getPriority() == Priority.HIGH &&
                           task.getStatus() != TaskStatus.COMPLETED)
            .count();
        
        // Calculate productivity scores per user
        Map<String, Double> productivityScores = new HashMap<>();
        List<UserTaskStats> userStatsList = new ArrayList<>();
        List<User> users = userRepository.findAll();
        
        for (User user : users) {
            UserTaskStats stats = getUserTaskStats(user.getId());
            userStatsList.add(stats);
            productivityScores.put(user.getName(), stats.getCompletionRate());
        }
        
        return new EnhancedDashboardSummary(totalTasks, completedTasks, pendingTasks, inProgressTasks,
                                           onTimePercent, productivityScores, userStatsList,
                                           overdueTasks, highPriorityTasks);
    }
    
    @Override
    public UserTaskStats getUserTaskStats(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
        List<Task> userTasks = taskRepository.findByAssignedTo(user);
        long totalTasks = userTasks.size();
        long completedTasks = userTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
            .count();
        long pendingTasks = userTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.PENDING)
            .count();
        long inProgressTasks = userTasks.stream()
            .filter(task -> task.getStatus() == TaskStatus.IN_PROGRESS)
            .count();
        
        double completionRate = totalTasks > 0 ? (double) completedTasks / totalTasks * 100 : 0;
        
        return new UserTaskStats(user.getId(), user.getName(), totalTasks, completedTasks, 
                                pendingTasks, inProgressTasks, completionRate);
    }
}