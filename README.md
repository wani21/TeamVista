# Team Productivity Dashboard Backend

A Spring Boot REST API for managing team productivity with task management and user authentication.

## Features

- JWT-based authentication
- Role-based access control (MANAGER/EMPLOYEE)
- Task management with status tracking
- Dashboard analytics
- MySQL database support
- CORS enabled for frontend integration

## Quick Start

1. **Prerequisites:**
   - Java 11+
   - Maven 3.6+
   - MySQL 8.0+ (or use H2 for development)

2. **Database Setup:**
   - Create MySQL database: `team_productivity`
   - Update credentials in `application.properties`
   - Or uncomment H2 configuration for in-memory database

3. **Run Application:**
   ```bash
   mvn spring-boot:run
   ```

4. **Access:**
   - API: http://localhost:8080
   - H2 Console (if enabled): http://localhost:8080/h2-console

## API Endpoints

### Authentication (Public)
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login and get JWT token

### Tasks (Protected)
- `POST /api/tasks` - Create task (MANAGER only)
- `GET /api/tasks` - List tasks (with optional filters)
- `GET /api/tasks/{id}` - Get task details
- `GET /api/tasks/search?keyword=` - Search tasks by keyword
- `PUT /api/tasks/{id}` - Update task
- `PUT /api/tasks/{id}/complete` - Mark task as completed
- `DELETE /api/tasks/{id}` - Delete task (MANAGER only)

### Users (Protected)
- `GET /api/users` - Get all users
- `GET /api/users/me` - Get current user profile
- `GET /api/users/{id}` - Get user by ID
- `GET /api/users/{id}/stats` - Get user task statistics

### Dashboard (Protected)
- `GET /api/dashboard/summary` - Get basic analytics summary
- `GET /api/dashboard/enhanced` - Get enhanced dashboard with detailed stats

## Sample Data

The application includes sample data:
- **Users:** 1 manager, 2 employees (password: `password123`)
- **Tasks:** 6 sample tasks with various statuses

## Example API Calls

### 1. Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "password123",
    "role": "EMPLOYEE"
  }'
```

### 2. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "manager@example.com",
    "password": "password123"
  }'
```

### 3. Create Task (with JWT token)
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE" \
  -d '{
    "title": "New Task",
    "description": "Task description",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "assignedToId": 2
  }'
```

## Project Structure

```
src/main/java/com/productivity/dashboard/
├── config/          # Security, JWT, Exception handling
├── controller/      # REST endpoints
├── dto/            # Data Transfer Objects
├── model/          # JPA entities
├── repository/     # Data access layer
└── service/        # Business logic
```

## Technology Stack

- **Framework:** Spring Boot 2.7.18
- **Security:** Spring Security + JWT
- **Database:** MySQL (H2 for development)
- **Build:** Maven
- **Java:** 11+