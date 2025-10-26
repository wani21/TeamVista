package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.AuthResponse;
import com.productivity.dashboard.dto.LoginRequest;
import com.productivity.dashboard.dto.RegisterRequest;
import com.productivity.dashboard.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication endpoints
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Register a new user
     * POST /api/auth/register
     * 
     * Request: {"name": "John Doe", "email": "john@example.com", "password": "password123", "role": "EMPLOYEE"}
     * Response: {"message": "User registered successfully", "userId": 1}
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = authService.register(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Login user and get JWT token
     * POST /api/auth/login
     * 
     * Request: {"email": "john@example.com", "password": "password123"}
     * Response: {"token": "jwt-token-here", "userId": 1, "name": "John Doe", "role": "EMPLOYEE"}
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(response);
    }
}