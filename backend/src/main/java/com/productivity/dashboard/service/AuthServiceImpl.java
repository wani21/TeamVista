package com.productivity.dashboard.service;

import com.productivity.dashboard.dto.LoginRequest;
import com.productivity.dashboard.dto.RegisterRequest;
import com.productivity.dashboard.exception.BadRequestException;
import com.productivity.dashboard.exception.NotFoundException;
import com.productivity.dashboard.exception.UnauthorizedException;
import com.productivity.dashboard.model.User;
import com.productivity.dashboard.repository.UserRepository;
import com.productivity.dashboard.config.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ActivityService activityService;

    @Override
    public User register(RegisterRequest request) {
        logger.info("Attempting to register new user with email: {}", request.getEmail());

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists - {}", request.getEmail());
            throw new BadRequestException("Email already exists");
        }

        // Create new user
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {} with role: {}", savedUser.getEmail(), savedUser.getRole());

        // Log activity
        activityService.logActivity(savedUser, "USER_REGISTERED", "User", savedUser.getId(),
                "New user registered: " + savedUser.getName());

        return savedUser;
    }

    @Override
    public String login(LoginRequest request) {
        logger.info("Login attempt for user: {}", request.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
            logger.debug("Authentication successful for user: {}", request.getEmail());
        } catch (BadCredentialsException e) {
            logger.warn("Login failed: Invalid credentials for user: {}", request.getEmail());
            throw new UnauthorizedException("Invalid email or password");
        }

        // Generate JWT token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        logger.info("JWT token generated successfully for user: {}", user.getEmail());
        return token;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        logger.debug("Fetching current user: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Current user not found: {}", email);
                    return new NotFoundException("User not found");
                });
    }
}