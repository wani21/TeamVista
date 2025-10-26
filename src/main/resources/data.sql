-- Insert sample users (1 manager, 2 employees)
-- Password for all users is 'password123' (BCrypt encoded)
INSERT INTO users (name, email, password, role, created_at) VALUES ('Alice Manager', 'alice@company.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBgIdm5vWEhIiC', 'MANAGER', '2025-10-20 09:00:00');
INSERT INTO users (name, email, password, role, created_at) VALUES ('Bob Employee', 'bob@company.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBgIdm5vWEhIiC', 'EMPLOYEE', '2025-10-20 09:15:00');
INSERT INTO users (name, email, password, role, created_at) VALUES ('Carol Employee', 'carol@company.com', '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewfBgIdm5vWEhIiC', 'EMPLOYEE', '2025-10-20 09:30:00');

-- Insert sample tasks with varied statuses, priorities, and due dates
-- Completed tasks (some on-time, some late)
INSERT INTO tasks (title, description, status, priority, due_date, completed_date, assigned_to_id, created_at) VALUES ('Setup Development Environment', 'Install and configure development tools', 'COMPLETED', 'HIGH', '2025-10-22', '2025-10-21', 2, '2025-10-20 10:00:00');
INSERT INTO tasks (title, description, status, priority, due_date, completed_date, assigned_to_id, created_at) VALUES ('Create Database Schema', 'Design and implement database tables', 'COMPLETED', 'HIGH', '2025-10-24', '2025-10-25', 3, '2025-10-20 10:15:00');
INSERT INTO tasks (title, description, status, priority, due_date, completed_date, assigned_to_id, created_at) VALUES ('Write API Documentation', 'Document all REST endpoints', 'COMPLETED', 'MEDIUM', '2025-10-26', '2025-10-25', 2, '2025-10-20 10:30:00');

-- In progress tasks
INSERT INTO tasks (title, description, status, priority, due_date, completed_date, assigned_to_id, created_at) VALUES ('Implement User Authentication', 'Add JWT-based authentication system', 'IN_PROGRESS', 'HIGH', '2025-10-28', NULL, 3, '2025-10-20 10:45:00');
INSERT INTO tasks (title, description, status, priority, due_date, completed_date, assigned_to_id, created_at) VALUES ('Design UI Components', 'Create reusable React components', 'IN_PROGRESS', 'MEDIUM', '2025-10-30', NULL, 2, '2025-10-20 11:00:00');

-- Pending tasks
INSERT INTO tasks (title, description, status, priority, due_date, completed_date, assigned_to_id, created_at) VALUES ('Write Unit Tests', 'Add comprehensive test coverage', 'PENDING', 'MEDIUM', '2025-11-05', NULL, 3, '2025-10-20 11:15:00');