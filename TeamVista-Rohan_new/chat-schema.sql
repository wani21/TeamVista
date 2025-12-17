-- ========================================
-- TeamVista - Group Chat & Revenue Feature
-- Database Schema Update
-- ========================================

-- Note: If you're using spring.jpa.hibernate.ddl-auto=update, 
-- Hibernate will create these tables automatically.
-- This file is for manual setup or reference.

-- ========================================
-- Projects Table (with Revenue fields)
-- ========================================
CREATE TABLE IF NOT EXISTS projects (
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
-- Chat Groups Table
-- ========================================
CREATE TABLE IF NOT EXISTS chat_groups (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    type VARCHAR(20),
    project_id BIGINT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    INDEX idx_project (project_id)
);

-- ========================================
-- Project Members Table
-- ========================================
CREATE TABLE IF NOT EXISTS project_members (
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
-- Messages Table
-- ========================================
CREATE TABLE IF NOT EXISTS messages (
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
-- Sample Data (Optional)
-- ========================================

-- Create a sample project with financials
-- INSERT INTO projects (name, description, status, manager_id, budget, revenue, expenses) VALUES 
-- ('Website Redesign', 'Complete redesign of the company website', 'IN_PROGRESS', 1, 50000.00, 35000.00, 20000.00);

-- Create a group for the project (automatically done by the application)
-- INSERT INTO chat_groups (name, type, project_id) VALUES 
-- ('Website Redesign Group', 'PROJECT_TEAM', 1);

-- Add members to the project
-- INSERT INTO project_members (project_id, user_id, member_role) VALUES 
-- (1, 1, 'OWNER'),
-- (1, 2, 'MEMBER'),
-- (1, 3, 'MEMBER');

-- ========================================
-- Alter existing tables (if upgrading)
-- ========================================

-- Add revenue columns to existing projects table
-- ALTER TABLE projects ADD COLUMN IF NOT EXISTS budget DECIMAL(15,2) DEFAULT 0.00;
-- ALTER TABLE projects ADD COLUMN IF NOT EXISTS revenue DECIMAL(15,2) DEFAULT 0.00;
-- ALTER TABLE projects ADD COLUMN IF NOT EXISTS expenses DECIMAL(15,2) DEFAULT 0.00;

-- ========================================
-- Useful Queries
-- ========================================

-- Get all messages in a group
-- SELECT m.*, u.name as sender_name 
-- FROM messages m 
-- JOIN users u ON m.sender_id = u.id 
-- WHERE m.group_id = ? 
-- ORDER BY m.created_at ASC;

-- Get all members of a project
-- SELECT pm.*, u.name, u.email 
-- FROM project_members pm 
-- JOIN users u ON pm.user_id = u.id 
-- WHERE pm.project_id = ?;

-- Get group for a project
-- SELECT * FROM chat_groups WHERE project_id = ?;

-- Calculate project profit/loss
-- SELECT 
--     name,
--     budget,
--     revenue,
--     expenses,
--     (revenue - expenses) as profit,
--     (budget - expenses) as budget_remaining
-- FROM projects;

-- Get total financials summary
-- SELECT 
--     COUNT(*) as total_projects,
--     SUM(budget) as total_budget,
--     SUM(revenue) as total_revenue,
--     SUM(expenses) as total_expenses,
--     SUM(revenue - expenses) as total_profit
-- FROM projects;
