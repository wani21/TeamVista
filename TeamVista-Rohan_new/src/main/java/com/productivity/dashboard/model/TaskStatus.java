package com.productivity.dashboard.model;

/**
 * Enum representing the status of a task in the system.
 * PENDING: Task has been created but work has not started
 * IN_PROGRESS: Task is currently being worked on
 * COMPLETED: Task has been finished
 */
public enum TaskStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}