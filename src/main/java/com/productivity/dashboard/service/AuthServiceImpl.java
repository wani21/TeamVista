package com.productivity.dashboard.service;

import com.productivity.dashboard.config.JwtTokenProvider;
import com.productivity.dashboard.dto.AuthResponse;
import com.productivity.dashboard.dto.LoginRequest;
import com.productivity.dashboard.dto.RegisterRequest;
import com.productivity.dashboard.exception.BadRequestException;
import com.productivity.dashboard.exception.UnauthorizedException;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of AuthService for user authentication
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    public AuthResponse register(RegisterRequest registerRequest) {
        // Check if email already exists
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());

        // Save user
        User savedUser = userRepository.save(user);

        return new AuthResponse("User registered successfully", savedUser.getId());
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Find user by email
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userOptional.get();

        // Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getId(), user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getId(), user.getName(), user.getRole());
    }
}