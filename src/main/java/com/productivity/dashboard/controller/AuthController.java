package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.ApiResponse;
import com.productivity.dashboard.dto.LoginRequest;
import com.productivity.dashboard.dto.RegisterRequest;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    /**
     * Register a new user
     * POST /api/auth/register
     * Request: {"name": "John Doe", "email": "john@example.com", "password": "password123", "role": "EMPLOYEE"}
     * Response: {"success": true, "message": "User registered successfully", "data": {...}}
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<User>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("POST /api/auth/register - Attempting to register user with email: {}", request.getEmail());
        
        try {
            User user = authService.register(request);
            logger.info("User registration successful - ID: {}, Email: {}, Role: {}", 
                user.getId(), user.getEmail(), user.getRole());
            return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
        } catch (Exception e) {
            logger.error("User registration failed for email: {} - Error: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }
    
    /**
     * Login user and return JWT token
     * POST /api/auth/login
     * Request: {"email": "john@example.com", "password": "password123"}
     * Response: {"success": true, "message": "Login successful", "data": {"token": "jwt_token_here"}}
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("POST /api/auth/login - Login attempt for email: {}", request.getEmail());
        
        try {
            String token = authService.login(request);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            logger.info("Login successful for user: {}", request.getEmail());
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            logger.warn("Login failed for email: {} - Error: {}", request.getEmail(), e.getMessage());
            throw e;
        }
    }
}