package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.DashboardSummary;
import com.productivity.dashboard.dto.TaskCreateRequest;
import com.productivity.dashboard.dto.TaskUpdateRequest;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TaskStatus;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.TaskRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Task createTask(TaskCreateRequest request) {
        User assignedUser = userRepository.findById(request.getAssignedToId())
            .orElseThrow(() -> new NotFoundException("User not found with id: " + request.getAssignedToId()));
        
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(request.getPriority());
        task.setDueDate(request.getDueDate());
        task.setAssignedTo(assignedUser);
        
        return taskRepository.save(task);
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
    public DashboardSummary getDashboardSummary() {
        long totalTasks = taskRepository.countTotalTasks();
        long completedTasks = taskRepository.countCompletedTasks();
        long pendingTasks = taskRepository.countPendingTasks();
        
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
}