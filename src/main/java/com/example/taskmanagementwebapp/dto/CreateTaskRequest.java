package com.example.taskmanagementwebapp.dto;

public class CreateTaskRequest {
    private String title;
    private String description;
    private String priorityLevel;
    // assignedUserId for manual assignment
    private Long assignedUserId;

    // getters & setters
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
    public String getPriorityLevel() {
        return priorityLevel;
    }
    public void setPriorityLevel(String priorityLevel) {
        this.priorityLevel = priorityLevel;
    }
    public Long getAssignedUserId() {
        return assignedUserId;
    }
    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }
}
