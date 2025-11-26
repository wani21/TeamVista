package com.productivity.dashboard.controller;

import com.productivity.dashboard.dto.ApiResponse;
import com.productivity.dashboard.dto.LoginRequest;
import com.productivity.dashboard.dto.RegisterRequest;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
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
        User user = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", user));
    }
    
    /**
     * Login user and return JWT token
     * POST /api/auth/login
     * Request: {"email": "john@example.com", "password": "password123"}
     * Response: {"success": true, "message": "Login successful", "data": {"token": "jwt_token_here"}}
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }
}