-- ========================================
-- TeamVista - Team Productivity Dashboard
-- Complete Database Schema with Enhanced Features
-- ========================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS leave_requests;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS time_entries;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS users;

-- ========================================
-- 1. Users Table
-- ========================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('MANAGER', 'EMPLOYEE')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Indexes for performance
    INDEX idx_email (email),
    INDEX idx_role (role),
    INDEX idx_created_at (created_at)
);

-- ========================================
-- 2. Tasks Table
-- ========================================
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED')),
    priority VARCHAR(10) NOT NULL CHECK (priority IN ('LOW', 'MEDIUM', 'HIGH')),
    due_date DATE,
    completed_date DATE,
    assigned_to BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_status (status),
    INDEX idx_priority (priority),
    INDEX idx_due_date (due_date),
    INDEX idx_created_at (created_at),
    INDEX idx_status_assigned (status, assigned_to)
);

-- ========================================
-- 3. Activities Table (Activity Tracking & Audit Trail)
-- ========================================
CREATE TABLE activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Indexes for performance
    INDEX idx_user_created (user_id, created_at DESC),
    INDEX idx_entity (entity_type, entity_id),
    INDEX idx_action (action),
    INDEX idx_created_at (created_at DESC)
);

-- ========================================
-- 4. Time Entries Table (Time Tracking)
-- ========================================
CREATE TABLE time_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    task_id BIGINT,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    duration_minutes BIGINT,
    description TEXT,
    is_manual BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_user_start (user_id, start_time DESC),
    INDEX idx_task (task_id),
    INDEX idx_running_timer (user_id, end_time),
    INDEX idx_date_range (start_time, end_time)
);

-- ========================================
-- 5. Attendance Table (Daily Attendance Tracking)
-- ========================================
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR(20) NOT NULL CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'HALF_DAY', 'WORK_FROM_HOME', 'ON_LEAVE')),
    work_hours DOUBLE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraint
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    
    -- Unique constraint - one attendance record per user per day
    UNIQUE KEY unique_user_date (user_id, date),
    
    -- Indexes for performance
    INDEX idx_date (date),
    INDEX idx_user_date (user_id, date DESC),
    INDEX idx_status (status)
);

-- ========================================
-- 6. Leave Requests Table (Leave Management)
-- ========================================
CREATE TABLE leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    leave_type VARCHAR(50) NOT NULL CHECK (leave_type IN ('SICK_LEAVE', 'CASUAL_LEAVE', 'ANNUAL_LEAVE', 'MATERNITY_LEAVE', 'PATERNITY_LEAVE', 'UNPAID_LEAVE', 'COMPENSATORY_LEAVE', 'EMERGENCY_LEAVE')),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_count INT NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Indexes for performance
    INDEX idx_user_status (user_id, status),
    INDEX idx_status (status),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_leave_type (leave_type)
);

-- ========================================
-- Sample Data
-- ========================================

-- Insert sample users
INSERT INTO users (name, email, password, role) VALUES
('John Manager', 'manager@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MANAGER'),
('Alice Employee', 'alice@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYEE'),
('Bob Employee', 'bob@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYEE'),
('Carol Employee', 'carol@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYEE');

-- Insert sample tasks
INSERT INTO tasks (title, description, status, priority, due_date, assigned_to, created_at) VALUES
('Setup Development Environment', 'Install and configure all necessary development tools', 'COMPLETED', 'HIGH', '2024-12-01', 2, '2024-11-25 09:00:00'),
('Design Database Schema', 'Create the database schema for the productivity dashboard', 'COMPLETED', 'HIGH', '2024-12-02', 2, '2024-11-26 10:00:00'),
('Implement User Authentication', 'Build JWT-based authentication system', 'IN_PROGRESS', 'HIGH', '2024-12-10', 3, '2024-11-27 11:00:00'),
('Create Task Management API', 'Develop REST APIs for task CRUD operations', 'PENDING', 'MEDIUM', '2024-12-15', 3, '2024-11-28 14:00:00'),
('Build Dashboard Frontend', 'Create React components for the dashboard', 'PENDING', 'MEDIUM', '2024-12-20', 4, '2024-11-29 09:30:00'),
('Write Unit Tests', 'Add comprehensive unit tests for all services', 'PENDING', 'LOW', '2024-12-25', 2, '2024-11-30 16:00:00'),
('Deploy to Production', 'Set up production environment and deploy application', 'PENDING', 'HIGH', '2024-12-30', 1, '2024-12-01 08:00:00'),
('User Interface Polish', 'Improve UI/UX and add responsive design', 'PENDING', 'MEDIUM', '2024-12-18', 4, '2024-12-02 13:00:00'),
('Performance Optimization', 'Optimize database queries and API performance', 'PENDING', 'LOW', '2024-12-22', 3, '2024-12-03 10:30:00'),
('Documentation', 'Write comprehensive API and user documentation', 'PENDING', 'MEDIUM', '2024-12-28', 2, '2024-12-04 15:00:00');

-- ========================================
-- Verification Queries
-- ========================================

-- Check if tables were created successfully
SELECT 'Tables created successfully' as status;

-- Count records
SELECT 
    'users' as table_name, 
    COUNT(*) as record_count 
FROM users
UNION ALL
SELECT 
    'tasks' as table_name, 
    COUNT(*) as record_count 
FROM tasks;

-- Insert sample activities
INSERT INTO activities (user_id, action, entity_type, entity_id, details, created_at) VALUES
(1, 'USER_LOGIN', 'User', 1, 'Manager logged in', NOW() - INTERVAL 2 HOUR),
(2, 'USER_LOGIN', 'User', 2, 'Employee logged in', NOW() - INTERVAL 1 HOUR),
(1, 'TASK_CREATED', 'Task', 1, 'Created task: Setup Development Environment', NOW() - INTERVAL 1 HOUR),
(2, 'TASK_UPDATED', 'Task', 1, 'Status changed to COMPLETED', NOW() - INTERVAL 30 MINUTE),
(3, 'TASK_UPDATED', 'Task', 3, 'Status changed to IN_PROGRESS', NOW() - INTERVAL 15 MINUTE);

-- Insert sample time entries
INSERT INTO time_entries (user_id, task_id, start_time, end_time, duration_minutes, description, is_manual, created_at) VALUES
(2, 1, NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 2 HOUR, 120, 'Setting up development environment', FALSE, NOW() - INTERVAL 4 HOUR),
(3, 3, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 1 HOUR, 120, 'Working on authentication system', FALSE, NOW() - INTERVAL 3 HOUR),
(4, 5, NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 1 HOUR, 60, 'Building React components', FALSE, NOW() - INTERVAL 2 HOUR);

-- Insert sample attendance records
INSERT INTO attendance (user_id, date, check_in_time, check_out_time, status, work_hours, notes, created_at) VALUES
(1, CURDATE(), CONCAT(CURDATE(), ' 08:30:00'), CONCAT(CURDATE(), ' 17:30:00'), 'PRESENT', 9.0, 'Full day work', NOW()),
(2, CURDATE(), CONCAT(CURDATE(), ' 09:15:00'), CONCAT(CURDATE(), ' 17:00:00'), 'LATE', 7.75, 'Traffic delay', NOW()),
(3, CURDATE(), CONCAT(CURDATE(), ' 09:00:00'), NULL, 'PRESENT', NULL, 'Currently working', NOW()),
(4, CURDATE(), NULL, NULL, 'WORK_FROM_HOME', 8.0, 'Working remotely today', NOW()),
(1, CURDATE() - INTERVAL 1 DAY, CONCAT(CURDATE() - INTERVAL 1 DAY, ' 08:45:00'), CONCAT(CURDATE() - INTERVAL 1 DAY, ' 18:00:00'), 'PRESENT', 9.25, 'Productive day', NOW() - INTERVAL 1 DAY),
(2, CURDATE() - INTERVAL 1 DAY, CONCAT(CURDATE() - INTERVAL 1 DAY, ' 09:00:00'), CONCAT(CURDATE() - INTERVAL 1 DAY, ' 17:30:00'), 'PRESENT', 8.5, 'Regular day', NOW() - INTERVAL 1 DAY);

-- Insert sample leave requests
INSERT INTO leave_requests (user_id, leave_type, start_date, end_date, days_count, reason, status, approved_by, approved_at, created_at) VALUES
(2, 'ANNUAL_LEAVE', CURDATE() + INTERVAL 7 DAY, CURDATE() + INTERVAL 11 DAY, 5, 'Family vacation planned', 'PENDING', NULL, NULL, NOW()),
(3, 'SICK_LEAVE', CURDATE() + INTERVAL 2 DAY, CURDATE() + INTERVAL 3 DAY, 2, 'Medical appointment and recovery', 'APPROVED', 1, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 2 HOUR),
(4, 'CASUAL_LEAVE', CURDATE() - INTERVAL 5 DAY, CURDATE() - INTERVAL 5 DAY, 1, 'Personal work', 'APPROVED', 1, NOW() - INTERVAL 6 DAY, NOW() - INTERVAL 7 DAY);

-- Show sample data
SELECT 
    u.name as user_name,
    u.role,
    COUNT(t.id) as task_count,
    SUM(CASE WHEN t.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_tasks,
    SUM(CASE WHEN t.status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as in_progress_tasks,
    SUM(CASE WHEN t.status = 'PENDING' THEN 1 ELSE 0 END) as pending_tasks
FROM users u
LEFT JOIN tasks t ON u.id = t.assigned_to
GROUP BY u.id, u.name, u.role
ORDER BY u.role DESC, u.name;

-- ========================================
-- Additional Useful Queries
-- ========================================

-- Tasks by status
-- SELECT status, COUNT(*) as count FROM tasks GROUP BY status;

-- Tasks by priority
-- SELECT priority, COUNT(*) as count FROM tasks GROUP BY priority;

-- Overdue tasks
-- SELECT t.title, t.due_date, u.name as assigned_to 
-- FROM tasks t 
-- JOIN users u ON t.assigned_to = u.id 
-- WHERE t.due_date < CURDATE() AND t.status != 'COMPLETED';

-- Tasks due this week
-- SELECT t.title, t.due_date, t.priority, u.name as assigned_to 
-- FROM tasks t 
-- JOIN users u ON t.assigned_to = u.id 
-- WHERE t.due_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY)
-- ORDER BY t.due_date, t.priority DESC;

-- ========================================
-- Notes
-- ========================================
-- 1. Passwords are BCrypt hashed (password is 'password' for all sample users)
-- 2. Foreign key constraints ensure data integrity
-- 3. Indexes are added for optimal query performance
-- 4. CHECK constraints ensure valid enum values
-- 5. Default values are set for status and timestamps
-- 6. ON DELETE SET NULL for tasks when user is deleted (preserves task history)

-- ========================================
-- For H2 Database (In-Memory)
-- ========================================
-- If using H2, you can also use this schema.
-- H2 supports most MySQL syntax.
-- Just change AUTO_INCREMENT to IDENTITY if needed.

-- ========================================
-- For PostgreSQL
-- ========================================
-- For PostgreSQL, make these changes:
-- 1. Change BIGINT AUTO_INCREMENT to BIGSERIAL
-- 2. Change TEXT to TEXT (same)
-- 3. Change TIMESTAMP to TIMESTAMP
-- 4. Change CHECK constraints syntax if needed

-- ========================================
-- Connection Examples
-- ========================================
-- MySQL: jdbc:mysql://localhost:3306/team_productivity
-- H2: jdbc:h2:mem:testdb (for in-memory)
-- H2: jdbc:h2:file:./data/team_productivity (for file-based)