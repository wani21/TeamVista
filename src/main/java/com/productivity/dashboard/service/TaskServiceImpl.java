package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.TaskRequest;
import com.productivity.dashboard.dto.TaskResponse;
import com.productivity.dashboard.exception.ForbiddenException;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.model.Role;
import com.productivity.dashboard.model.Task;
import com.productivity.dashboard.model.TaskStatus;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.TaskRepository;
import com.productivity.dashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TaskService for task operations
 */
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public TaskResponse createTask(TaskRequest taskRequest, Long currentUserId) {
        // Verify current user is a manager
        User currentUser = getUserById(currentUserId);
        if (currentUser.getRole() != Role.MANAGER) {
            throw new ForbiddenException("Only managers can create tasks");
        }

        // Verify assigned user exists
        User assignedUser = getUserById(taskRequest.getAssignedToId());

        // Create task
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setPriority(taskRequest.getPriority());
        task.setDueDate(taskRequest.getDueDate());
        task.setAssignedTo(assignedUser);

        Task savedTask = taskRepository.save(task);
        return convertToTaskResponse(savedTask);
    }

    @Override
    public TaskResponse updateTask(Long taskId, TaskRequest taskRequest, Long currentUserId) {
        Task task = getTaskEntityById(taskId);
        User currentUser = getUserById(currentUserId);

        // Check authorization: manager can update any task, employee can only update their own task's status
        boolean isManager = currentUser.getRole() == Role.MANAGER;
        boolean isAssignedEmployee = task.getAssignedTo().getId().equals(currentUserId);

        if (!isManager && !isAssignedEmployee) {
            throw new ForbiddenException("You can only update tasks assigned to you");
        }

        // If employee, only allow status updates
        if (!isManager && isAssignedEmployee) {
            task.setStatus(taskRequest.getStatus());
        } else {
            // Manager can update all fields
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setStatus(taskRequest.getStatus());
            task.setPriority(taskRequest.getPriority());
            task.setDueDate(taskRequest.getDueDate());
            
            // Update assigned user if changed
            if (!task.getAssignedTo().getId().equals(taskRequest.getAssignedToId())) {
                User newAssignedUser = getUserById(taskRequest.getAssignedToId());
                task.setAssignedTo(newAssignedUser);
            }
        }

        Task updatedTask = taskRepository.save(task);
        return convertToTaskResponse(updatedTask);
    }

    @Override
    public TaskResponse completeTask(Long taskId, Long currentUserId) {
        Task task = getTaskEntityById(taskId);
        User currentUser = getUserById(currentUserId);

        // Check authorization: manager or assigned employee can complete task
        boolean isManager = currentUser.getRole() == Role.MANAGER;
        boolean isAssignedEmployee = task.getAssignedTo().getId().equals(currentUserId);

        if (!isManager && !isAssignedEmployee) {
            throw new ForbiddenException("You can only complete tasks assigned to you");
        }

        // Mark as completed
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedDate(LocalDate.now());

        Task updatedTask = taskRepository.save(task);
        return convertToTaskResponse(updatedTask);
    }

    @Override
    public TaskResponse getTaskById(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
        return convertToTaskResponse(task);
    }

    @Override
    public List<TaskResponse> getTasks(Long assignedToId, TaskStatus status) {
        List<Task> tasks;

        if (assignedToId != null && status != null) {
            tasks = taskRepository.findByAssignedToIdAndStatus(assignedToId, status);
        } else if (assignedToId != null) {
            tasks = taskRepository.findByAssignedToId(assignedToId);
        } else if (status != null) {
            tasks = taskRepository.findByStatus(status);
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream()
                .map(this::convertToTaskResponse)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to get task entity by ID
     */
    private Task getTaskEntityById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new NotFoundException("Task not found with id: " + taskId));
    }

    /**
     * Helper method to get user by ID
     */
    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
    }

    /**
     * Convert Task entity to TaskResponse DTO
     */
    private TaskResponse convertToTaskResponse(Task task) {
        TaskResponse.UserSummary userSummary = new TaskResponse.UserSummary(
                task.getAssignedTo().getId(),
                task.getAssignedTo().getName(),
                task.getAssignedTo().getEmail()
        );

        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getCompletedDate(),
                userSummary,
                task.getCreatedAt()
        );
    }
}