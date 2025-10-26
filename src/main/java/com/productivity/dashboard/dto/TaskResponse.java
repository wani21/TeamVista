package com.productivity.dashboard.dto;

import com.productivity.dashboard.model.Priority;
import com.productivity.dashboard.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for task responses with full details
 */
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private LocalDate completedDate;
    private UserSummary assignedTo;
    private LocalDateTime createdAt;

    // Default constructor
    public TaskResponse() {}

    // Constructor
    public TaskResponse(Long id, String title, String description, TaskStatus status, Priority priority, 
                       LocalDate dueDate, LocalDate completedDate, UserSummary assignedTo, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completedDate = completedDate;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public UserSummary getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(UserSummary assignedTo) {
        this.assignedTo = assignedTo;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Nested class for user summary in task responses
     */
    public static class UserSummary {
        private Long id;
        private String name;
        private String email;

        public UserSummary() {}

        public UserSummary(Long id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}