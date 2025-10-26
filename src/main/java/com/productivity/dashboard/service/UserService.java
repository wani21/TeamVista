package com.productivity.dashboard.service;

import com.productivity.dashboard.model.User;

import java.util.List;

/**
 * Service interface for user operations
 */
public interface UserService {

    /**
     * Find user by ID
     * @param id user ID
     * @return user entity
     */
    User findById(Long id);

    /**
     * Find user by email
     * @param email user email
     * @return user entity
     */
    User findByEmail(String email);

    /**
     * Get all users
     * @return list of all users
     */
    List<User> findAll();
}