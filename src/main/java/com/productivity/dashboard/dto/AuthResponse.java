package com.productivity.dashboard.dto;

import com.productivity.dashboard.model.Role;

/**
 * DTO for authentication response containing JWT token and user info
 */
public class AuthResponse {

    private String token;
    private Long userId;
    private String name;
    private Role role;
    private String message;

    // Default constructor
    public AuthResponse() {}

    // Constructor for login response
    public AuthResponse(String token, Long userId, String name, Role role) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.role = role;
    }

    // Constructor for registration response
    public AuthResponse(String message, Long userId) {
        this.message = message;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}