package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.LoginRequest;
import com.productivity.dashboard.dto.RegisterRequest;
import com.productivity.dashboard.model.User;

public interface AuthService {
    
    User register(RegisterRequest request);
    
    String login(LoginRequest request);
    
    User getCurrentUser();
}
