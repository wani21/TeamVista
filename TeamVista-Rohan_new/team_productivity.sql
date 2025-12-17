-- Create the database
CREATE DATABASE IF NOT EXISTS team_productivity;

-- Use the database
USE team_productivity;
drop database team_productivity;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('MANAGER', 'EMPLOYEE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
DROP table users;
-- Create tasks table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    status ENUM('PENDING', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'PENDING',
    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
    due_date DATE,
    created_by_id BIGINT NOT NULL,
    assigned_to_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (assigned_to_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Create indexes for better performance
CREATE INDEX idx_tasks_status ON tasks(status);
CREATE INDEX idx_tasks_priority ON tasks(priority);
CREATE INDEX idx_tasks_assigned_to ON tasks(assigned_to_id);
CREATE INDEX idx_tasks_created_by ON tasks(created_by_id);
CREATE INDEX idx_tasks_due_date ON tasks(due_date);
CREATE INDEX idx_users_email ON users(email);

-- Insert sample users (password is 'password123' - should be bcrypt hashed in actual application)
-- Note: You'll need to hash these passwords using BCrypt in your application
INSERT INTO users (name, email, password, role) VALUES
('Manager User', 'manager@example.com', '$2a$10$YourBCryptHashedPasswordHere', 'MANAGER'),
('John Doe', 'john@example.com', '$2a$10$YourBCryptHashedPasswordHere', 'EMPLOYEE'),
('Jane Smith', 'jane@example.com', '$2a$10$YourBCryptHashedPasswordHere', 'EMPLOYEE');

-- Insert sample tasks
INSERT INTO tasks (title, description, status, priority, due_date, created_by_id, assigned_to_id) VALUES
('Complete Project Documentation', 'Write comprehensive documentation for the new feature', 'IN_PROGRESS', 'HIGH', '2024-12-20', 1, 2),
('Code Review', 'Review pull requests from team members', 'PENDING', 'MEDIUM', '2024-12-18', 1, 2),
('Fix Login Bug', 'Resolve authentication issue on mobile app', 'COMPLETED', 'HIGH', '2024-12-10', 1, 3),
('Update Database Schema', 'Add new fields for user preferences', 'IN_PROGRESS', 'MEDIUM', '2024-12-22', 1, 2),
('Team Meeting Preparation', 'Prepare slides for weekly team sync', 'PENDING', 'LOW', '2024-12-17', 1, 3),
('Performance Optimization', 'Optimize API response times', 'PENDING', 'HIGH', '2024-12-25', 1, 2);
