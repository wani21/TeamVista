# Complete API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication
All protected endpoints require a JWT token in the Authorization header:
```
Authorization: Bearer <your_jwt_token>
```

---

## 📡 API Endpoints (Total: 15)

### 1. Authentication APIs (Public - 2 endpoints)

#### 1.1 Register User
**POST** `/api/auth/register`

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "EMPLOYEE"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 4,
    "name": "John Doe",
    "email": "john@example.com",
    "role": "EMPLOYEE",
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### 1.2 Login
**POST** `/api/auth/login`

**Request Body:**
```json
{
  "email": "manager@example.com",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

---

### 2. Task Management APIs (Protected - 7 endpoints)

#### 2.1 Create Task (MANAGER only)
**POST** `/api/tasks`

**Headers:**
```
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Implement Feature X",
  "description": "Build the new dashboard feature",
  "priority": "HIGH",
  "dueDate": "2024-12-31",
  "assignedToId": 2
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "id": 7,
    "title": "Implement Feature X",
    "description": "Build the new dashboard feature",
    "status": "PENDING",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "completedDate": null,
    "assignedTo": {
      "id": 2,
      "name": "Alice Employee",
      "email": "alice@example.com"
    },
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

#### 2.2 Get All Tasks (with filters)
**GET** `/api/tasks`

**Query Parameters:**
- `assignedTo` (optional) - Filter by user ID
- `status` (optional) - Filter by status (PENDING, IN_PROGRESS, COMPLETED)

**Examples:**
```
GET /api/tasks
GET /api/tasks?assignedTo=2
GET /api/tasks?status=PENDING
GET /api/tasks?assignedTo=2&status=IN_PROGRESS
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tasks retrieved successfully",
  "data": [
    {
      "id": 1,
      "title": "Setup Development Environment",
      "description": "Configure local development environment",
      "status": "COMPLETED",
      "priority": "HIGH",
      "dueDate": "2024-01-15",
      "completedDate": "2024-01-14",
      "assignedTo": {...},
      "createdAt": "2024-01-10T10:00:00"
    }
  ]
}
```

#### 2.3 Get Task by ID
**GET** `/api/tasks/{id}`

**Example:**
```
GET /api/tasks/1
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task retrieved successfully",
  "data": {
    "id": 1,
    "title": "Setup Development Environment",
    ...
  }
}
```

#### 2.4 Search Tasks
**GET** `/api/tasks/search?keyword={keyword}`

**Example:**
```
GET /api/tasks/search?keyword=feature
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Tasks found",
  "data": [...]
}
```

#### 2.5 Update Task
**PUT** `/api/tasks/{id}`

**Permissions:**
- MANAGER: Can update any field of any task
- EMPLOYEE: Can only update status of their own tasks

**Request Body (Manager):**
```json
{
  "title": "Updated Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2024-12-31",
  "assignedToId": 3
}
```

**Request Body (Employee - status only):**
```json
{
  "status": "IN_PROGRESS"
}
```

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task updated successfully",
  "data": {...}
}
```

#### 2.6 Complete Task
**PUT** `/api/tasks/{id}/complete`

**Permissions:**
- MANAGER: Can complete any task
- EMPLOYEE: Can only complete their own tasks

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task completed successfully",
  "data": {
    "id": 3,
    "status": "COMPLETED",
    "completedDate": "2024-01-15",
    ...
  }
}
```

#### 2.7 Delete Task (MANAGER only)
**DELETE** `/api/tasks/{id}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Task deleted successfully",
  "data": null
}
```

---

### 3. User Management APIs (Protected - 4 endpoints)

#### 3.1 Get All Users
**GET** `/api/users`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Users retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "John Manager",
      "email": "manager@example.com",
      "role": "MANAGER",
      "createdAt": "2024-01-01T00:00:00"
    },
    {
      "id": 2,
      "name": "Alice Employee",
      "email": "alice@example.com",
      "role": "EMPLOYEE",
      "createdAt": "2024-01-01T00:00:00"
    }
  ]
}
```

#### 3.2 Get Current User Profile
**GET** `/api/users/me`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User profile retrieved",
  "data": {
    "id": 1,
    "name": "John Manager",
    "email": "manager@example.com",
    "role": "MANAGER",
    "createdAt": "2024-01-01T00:00:00"
  }
}
```

#### 3.3 Get User by ID
**GET** `/api/users/{id}`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User retrieved successfully",
  "data": {...}
}
```

#### 3.4 Get User Task Statistics
**GET** `/api/users/{id}/stats`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "User statistics retrieved",
  "data": {
    "userId": 2,
    "userName": "Alice Employee",
    "totalTasks": 3,
    "completedTasks": 2,
    "pendingTasks": 0,
    "inProgressTasks": 1,
    "completionRate": 66.67
  }
}
```

---

### 4. Dashboard APIs (Protected - 2 endpoints)

#### 4.1 Get Basic Dashboard Summary
**GET** `/api/dashboard/summary`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Dashboard summary retrieved successfully",
  "data": {
    "totalTasks": 6,
    "completedTasks": 2,
    "pendingTasks": 4,
    "onTimeCompletionPercent": 50.0,
    "productivityScores": {
      "John Manager": 100.0,
      "Alice Employee": 66.67,
      "Bob Employee": 0.0
    }
  }
}
```

#### 4.2 Get Enhanced Dashboard
**GET** `/api/dashboard/enhanced`

**Response (200 OK):**
```json
{
  "success": true,
  "message": "Enhanced dashboard retrieved successfully",
  "data": {
    "totalTasks": 6,
    "completedTasks": 2,
    "pendingTasks": 1,
    "inProgressTasks": 3,
    "onTimeCompletionPercent": 50.0,
    "productivityScores": {
      "John Manager": 100.0,
      "Alice Employee": 66.67,
      "Bob Employee": 0.0
    },
    "userStats": [
      {
        "userId": 1,
        "userName": "John Manager",
        "totalTasks": 0,
        "completedTasks": 0,
        "pendingTasks": 0,
        "inProgressTasks": 0,
        "completionRate": 0.0
      },
      {
        "userId": 2,
        "userName": "Alice Employee",
        "totalTasks": 3,
        "completedTasks": 2,
        "pendingTasks": 0,
        "inProgressTasks": 1,
        "completionRate": 66.67
      }
    ],
    "overdueTasks": 1,
    "highPriorityTasks": 2
  }
}
```

---

## Error Responses

### 400 Bad Request
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/api/auth/register"
}
```

### 401 Unauthorized
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

### 403 Forbidden
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You can only update your own tasks",
  "path": "/api/tasks/1"
}
```

### 404 Not Found
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

### 500 Internal Server Error
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred",
  "path": "/api/tasks"
}
```

---

## Data Models

### User
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "role": "MANAGER",
  "createdAt": "2024-01-01T00:00:00"
}
```

### Task
```json
{
  "id": 1,
  "title": "Task Title",
  "description": "Task Description",
  "status": "PENDING",
  "priority": "HIGH",
  "dueDate": "2024-12-31",
  "completedDate": null,
  "assignedTo": {...},
  "createdAt": "2024-01-15T10:30:00"
}
```

### Enums

**Role:**
- `MANAGER`
- `EMPLOYEE`

**TaskStatus:**
- `PENDING`
- `IN_PROGRESS`
- `COMPLETED`

**Priority:**
- `LOW`
- `MEDIUM`
- `HIGH`

---

## Sample Test Credentials

| Email | Password | Role |
|-------|----------|------|
| manager@example.com | password123 | MANAGER |
| alice@example.com | password123 | EMPLOYEE |
| bob@example.com | password123 | EMPLOYEE |

---

## Quick Test Flow

```bash
# 1. Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manager@example.com","password":"password123"}'

# Save the token from response
TOKEN="your_token_here"

# 2. Get all tasks
curl http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN"

# 3. Create a task (MANAGER only)
curl -X POST http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "New Task",
    "description": "Task description",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "assignedToId": 2
  }'

# 4. Search tasks
curl "http://localhost:8080/api/tasks/search?keyword=feature" \
  -H "Authorization: Bearer $TOKEN"

# 5. Get enhanced dashboard
curl http://localhost:8080/api/dashboard/enhanced \
  -H "Authorization: Bearer $TOKEN"

# 6. Get user statistics
curl http://localhost:8080/api/users/2/stats \
  -H "Authorization: Bearer $TOKEN"

# 7. Delete task (MANAGER only)
curl -X DELETE http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## API Summary

**Total Endpoints: 15**

- **Public:** 2 (Authentication)
- **Protected:** 13
  - Tasks: 7
  - Users: 4
  - Dashboard: 2

**HTTP Methods Used:**
- GET: 10
- POST: 3
- PUT: 2
- DELETE: 1
