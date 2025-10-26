package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.TaskRequest;
import com.productivity.dashboard.dto.TaskResponse;
import com.productivity.dashboard.model.TaskStatus;

import java.util.List;

/**
 * Service interface for task operations
 */
public interface TaskService {

    /**
     * Create a new task
     * @param taskRequest task creation data
     * @param currentUserId ID of the user creating the task
     * @return created task response
     */
    TaskResponse createTask(TaskRequest taskRequest, Long currentUserId);

    /**
     * Update an existing task
     * @param taskId task ID to update
     * @param taskRequest updated task data
     * @param currentUserId ID of the user updating the task
     * @return updated task response
     */
    TaskResponse updateTask(Long taskId, TaskRequest taskRequest, Long currentUserId);

    /**
     * Mark task as completed
     * @param taskId task ID to complete
     * @param currentUserId ID of the user completing the task
     * @return updated task response
     */
    TaskResponse completeTask(Long taskId, Long currentUserId);

    /**
     * Get task by ID
     * @param taskId task ID
     * @return task response
     */
    TaskResponse getTaskById(Long taskId);

    /**
     * Get tasks with optional filtering
     * @param assignedToId optional filter by assigned user ID
     * @param status optional filter by task status
     * @return list of task responses
     */
    List<TaskResponse> getTasks(Long assignedToId, TaskStatus status);
}