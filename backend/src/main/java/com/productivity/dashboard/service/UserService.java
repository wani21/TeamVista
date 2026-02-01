package com.productivity.dashboard.service;

import com.productivity.dashboard.model.User;

import java.util.List;

public interface UserService {
    
    List<User> getAllUsers();
    
    User getUserById(Long id);
    
    User getUserByEmail(String email);
}