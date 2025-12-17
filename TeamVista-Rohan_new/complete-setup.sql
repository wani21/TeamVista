-- ========================================
-- TeamVista - Complete Database Setup
-- Run this in MySQL to set up the database
-- ========================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS team_productivity;
USE team_productivity;

-- ========================================
-- Drop existing tables (clean setup)
-- ========================================
DROP TABLE IF EXISTS messages;
DROP TABLE IF EXISTS project_members;
DROP TABLE IF EXISTS chat_groups;
DROP TABLE IF EXISTS time_entries;
DROP TABLE IF EXISTS activities;
DROP TABLE IF EXISTS tasks;
DROP TABLE IF EXISTS leave_requests;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

-- ========================================
-- 1. Users Table
-- ========================================
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_role (role)
);

-- ========================================
-- 2. Projects Table
-- ========================================
CREATE TABLE projects (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) DEFAULT 'PLANNING',
    start_date DATE,
    end_date DATE,
    manager_id BIGINT,
    budget DECIMAL(15,2) DEFAULT 0.00,
    revenue DECIMAL(15,2) DEFAULT 0.00,
    expenses DECIMAL(15,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (manager_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_status (status),
    INDEX idx_manager (manager_id)
);

-- ========================================
-- 3. Chat Groups Table
-- ========================================
CREATE TABLE chat_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(20),
    project_id BIGINT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    INDEX idx_project (project_id)
);

-- ========================================
-- 4. Project Members Table
-- ========================================
CREATE TABLE project_members (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    member_role VARCHAR(20) DEFAULT 'MEMBER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_project_user (project_id, user_id),
    INDEX idx_project (project_id),
    INDEX idx_user (user_id)
);

-- ========================================
-- 5. Messages Table
-- ========================================
CREATE TABLE messages (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    sender_id BIGINT NOT NULL,
    group_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (group_id) REFERENCES chat_groups(id) ON DELETE CASCADE,
    INDEX idx_group (group_id),
    INDEX idx_sender (sender_id),
    INDEX idx_created (created_at)
);

-- ========================================
-- 6. Tasks Table
-- ========================================
CREATE TABLE tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',
    due_date DATE,
    completed_date DATE,
    assigned_to BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_to) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_assigned_to (assigned_to),
    INDEX idx_status (status)
);

-- ========================================
-- 7. Activities Table
-- ========================================
CREATE TABLE activities (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_user_created (user_id, created_at DESC)
);

-- ========================================
-- 8. Time Entries Table
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE SET NULL
);

-- ========================================
-- 9. Attendance Table
-- ========================================
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    work_hours DOUBLE,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_date (user_id, date)
);

-- ========================================
-- 10. Leave Requests Table
-- ========================================
CREATE TABLE leave_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    leave_type VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    days_count INT NOT NULL,
    reason TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (approved_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ========================================
-- INSERT SAMPLE DATA
-- ========================================

-- Password for all users is: password
-- BCrypt hash of 'password'

-- Insert Users (1 Manager + 4 Employees)
INSERT INTO users (name, email, password, role) VALUES
('John Manager', 'manager@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'MANAGER'),
('Alice Employee', 'alice@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYEE'),
('Bob Developer', 'bob@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYEE'),
('Carol Designer', 'carol@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYEE'),
('David Analyst', 'david@example.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'EMPLOYEE');

-- Insert Projects
INSERT INTO projects (name, description, status, manager_id, budget, revenue, expenses, start_date, end_date) VALUES
('Website Redesign', 'Complete redesign of company website with modern UI/UX', 'IN_PROGRESS', 1, 50000.00, 35000.00, 22000.00, '2024-01-15', '2024-06-30'),
('Mobile App Development', 'Build iOS and Android mobile application', 'PLANNING', 1, 80000.00, 0.00, 5000.00, '2024-03-01', '2024-12-31'),
('Data Analytics Platform', 'Internal analytics dashboard for business insights', 'IN_PROGRESS', 1, 35000.00, 20000.00, 15000.00, '2024-02-01', '2024-08-31');

-- Insert Chat Groups for Projects
INSERT INTO chat_groups (name, type, project_id) VALUES
('Website Redesign Group', 'PROJECT_TEAM', 1),
('Mobile App Development Group', 'PROJECT_TEAM', 2),
('Data Analytics Platform Group', 'PROJECT_TEAM', 3);

-- Insert Project Members
-- Project 1: Website Redesign - Manager (owner), Alice, Bob
INSERT INTO project_members (project_id, user_id, member_role) VALUES
(1, 1, 'OWNER'),
(1, 2, 'MEMBER'),
(1, 3, 'MEMBER');

-- Project 2: Mobile App - Manager (owner), Bob, Carol
INSERT INTO project_members (project_id, user_id, member_role) VALUES
(2, 1, 'OWNER'),
(2, 3, 'MEMBER'),
(2, 4, 'MEMBER');

-- Project 3: Analytics Platform - Manager (owner), Alice, David
INSERT INTO project_members (project_id, user_id, member_role) VALUES
(3, 1, 'OWNER'),
(3, 2, 'MEMBER'),
(3, 5, 'MEMBER');

-- Insert Sample Messages
INSERT INTO messages (content, sender_id, group_id) VALUES
('Welcome to the Website Redesign project! Lets discuss our goals.', 1, 1),
('Hi everyone! Excited to work on this project.', 2, 1),
('I have some design mockups ready to share.', 3, 1),
('Great! Lets schedule a meeting to review them.', 1, 1),
('Mobile app kickoff meeting tomorrow at 10 AM', 1, 2),
('Ill prepare the technical requirements doc', 3, 2),
('Analytics dashboard sprint planning this week', 1, 3),
('I have the data models ready for review', 5, 3);

-- Insert Sample Tasks
INSERT INTO tasks (title, description, status, priority, due_date, assigned_to) VALUES
('Design Homepage Mockup', 'Create wireframes and mockups for new homepage', 'IN_PROGRESS', 'HIGH', '2024-12-20', 2),
('Implement User Authentication', 'Set up JWT authentication system', 'COMPLETED', 'HIGH', '2024-12-15', 3),
('Create Database Schema', 'Design and implement database structure', 'COMPLETED', 'HIGH', '2024-12-10', 3),
('Build REST APIs', 'Develop backend API endpoints', 'IN_PROGRESS', 'MEDIUM', '2024-12-25', 3),
('Mobile UI Design', 'Design mobile app screens', 'PENDING', 'MEDIUM', '2024-12-30', 4),
('Setup Analytics Pipeline', 'Configure data collection pipeline', 'IN_PROGRESS', 'HIGH', '2024-12-22', 5),
('Write Unit Tests', 'Add comprehensive test coverage', 'PENDING', 'LOW', '2025-01-05', 2),
('Performance Optimization', 'Optimize database queries and API responses', 'PENDING', 'MEDIUM', '2025-01-10', 3);

-- Insert Sample Activities
INSERT INTO activities (user_id, action, entity_type, entity_id, details) VALUES
(1, 'PROJECT_CREATED', 'Project', 1, 'Created project: Website Redesign'),
(1, 'PROJECT_CREATED', 'Project', 2, 'Created project: Mobile App Development'),
(1, 'PROJECT_CREATED', 'Project', 3, 'Created project: Data Analytics Platform'),
(2, 'TASK_UPDATED', 'Task', 1, 'Started working on Homepage Mockup'),
(3, 'TASK_COMPLETED', 'Task', 2, 'Completed User Authentication'),
(3, 'TASK_COMPLETED', 'Task', 3, 'Completed Database Schema');

-- ========================================
-- VERIFICATION QUERIES
-- ========================================

-- Check all tables
SELECT 'Users' as TableName, COUNT(*) as Count FROM users
UNION ALL SELECT 'Projects', COUNT(*) FROM projects
UNION ALL SELECT 'Chat Groups', COUNT(*) FROM chat_groups
UNION ALL SELECT 'Project Members', COUNT(*) FROM project_members
UNION ALL SELECT 'Messages', COUNT(*) FROM messages
UNION ALL SELECT 'Tasks', COUNT(*) FROM tasks
UNION ALL SELECT 'Activities', COUNT(*) FROM activities;

-- Show users
SELECT id, name, email, role FROM users;

-- Show projects with managers
SELECT p.id, p.name, p.status, p.budget, p.revenue, u.name as manager_name 
FROM projects p 
LEFT JOIN users u ON p.manager_id = u.id;

-- Show project members
SELECT p.name as project_name, u.name as member_name, pm.member_role 
FROM project_members pm 
JOIN projects p ON pm.project_id = p.id 
JOIN users u ON pm.user_id = u.id
ORDER BY p.id, pm.member_role;

-- ========================================
-- LOGIN CREDENTIALS
-- ========================================
-- 
-- MANAGER (can create projects, add members, see all projects):
--   Email: manager@example.com
--   Password: password
--
-- EMPLOYEES (can only see assigned projects):
--   Email: alice@example.com   Password: password
--   Email: bob@example.com     Password: password
--   Email: carol@example.com   Password: password
--   Email: david@example.com   Password: password
--
-- ========================================

