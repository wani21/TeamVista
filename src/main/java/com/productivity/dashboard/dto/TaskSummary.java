package com.productivity.dashboard.dto;

import com.productivity.dashboard.model.Priority;
import com.productivity.dashboard.model.TaskStatus;

import java.time.LocalDate;

/**
 * DTO for task list views with essential information
 */
public class TaskSummary {

    private Long id;
    private String title;
    private TaskStatus status;
    private Priority priority;
    private LocalDate dueDate;
    private String assignedToName;

    // Default constructor
    public TaskSummary() {}

    // Constructor
    public TaskSummary(Long id, String title, TaskStatus status, Priority priority, LocalDate dueDate, String assignedToName) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.assignedToName = assignedToName;
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

    public String getAssignedToName() {
        return assignedToName;
    }

    public void setAssignedToName(String assignedToName) {
        this.assignedToName = assignedToName;
    }
}