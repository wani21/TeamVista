package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.AuthResponse;
import com.productivity.dashboard.dto.LoginRequest;
import com.productivity.dashboard.dto.RegisterRequest;

/**
 * Service interface for authentication operations
 */
public interface AuthService {

    /**
     * Register a new user
     * @param registerRequest user registration data
     * @return authentication response with user ID
     */
    AuthResponse register(RegisterRequest registerRequest);

    /**
     * Authenticate user and generate JWT token
     * @param loginRequest user login credentials
     * @return authentication response with JWT token
     */
    AuthResponse login(LoginRequest loginRequest);
}