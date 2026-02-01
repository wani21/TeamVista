-- Insert sample users (passwords are BCrypt encoded for 'password123')
INSERT IGNORE INTO users (id, name, email, password, role, created_at) VALUES
(1, 'John Manager', 'manager@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'MANAGER', NOW()),
(2, 'Alice Employee', 'alice@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'EMPLOYEE', NOW()),
(3, 'Bob Employee', 'bob@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'EMPLOYEE', NOW());

-- Insert sample tasks
INSERT IGNORE INTO tasks (id, title, description, status, priority, due_date, completed_date, assigned_to, created_at) VALUES
(1, 'Setup Development Environment', 'Configure local development environment with all necessary tools', 'COMPLETED', 'HIGH', '2024-01-15', '2024-01-14', 2, NOW()),
(2, 'Design Database Schema', 'Create comprehensive database schema for the application', 'COMPLETED', 'HIGH', '2024-01-20', '2024-01-19', 2, NOW()),
(3, 'Implement User Authentication', 'Develop JWT-based authentication system', 'IN_PROGRESS', 'HIGH', '2024-01-25', NULL, 2, NOW()),
(4, 'Create Task Management API', 'Build REST API endpoints for task management', 'PENDING', 'MEDIUM', '2024-01-30', NULL, 3, NOW()),
(5, 'Write Unit Tests', 'Implement comprehensive unit tests for all services', 'PENDING', 'MEDIUM', '2024-02-05', NULL, 3, NOW()),
(6, 'Setup CI/CD Pipeline', 'Configure automated build and deployment pipeline', 'PENDING', 'LOW', '2024-02-10', NULL, 3, NOW());