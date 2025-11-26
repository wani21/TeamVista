# Team Productivity Backend - Features & API Documentation

## 🎯 Features by Role

### 👤 **MANAGER Features**

#### User Management
- ✅ Register new users (MANAGER or EMPLOYEE)
- ✅ Login to get JWT authentication token
- ✅ View own profile information

#### Task Management
- ✅ **Create new tasks** and assign to any employee
- ✅ **View all tasks** in the system
- ✅ **Filter tasks** by:
  - Assigned user
  - Task status (PENDING, IN_PROGRESS, COMPLETED)
  - Combination of both
- ✅ **View task details** by ID
- ✅ **Update any task** (full permissions):
  - Change title
  - Change description
  - Change status
  - Change priority
  - Change due date
  - Reassign to different employee
- ✅ **Complete any task** (mark as completed with automatic date)
- ✅ **Delete tasks** (if implemented)

#### Dashboard & Analytics
- ✅ View **dashboard summary** with:
  - Total tasks count
  - Completed tasks count
  - Pending tasks count (PENDING + IN_PROGRESS)
  - On-time completion percentage
  - **Productivity scores per employee**
- ✅ Monitor team performance
- ✅ Track task completion rates

#### Permissions Summary
| Action | Manager Access |
|--------|---------------|
| Create Task | ✅ Full Access |
| View All Tasks | ✅ Full Access |
| Update Any Task | ✅ Full Access |
| Complete Any Task | ✅ Full Access |
| View Dashboard | ✅ Full Access |
| Assign Tasks | ✅ Full Access |
| Reassign Tasks | ✅ Full Access |

---

### 👥 **EMPLOYEE Features**

#### User Management
- ✅ Register as new employee
- ✅ Login to get JWT authentication token
- ✅ View own profile information

#### Task Management
- ✅ **View all tasks** in the system
- ✅ **Filter tasks** by:
  - Assigned user (typically filter to see own tasks)
  - Task status
  - Combination of both
- ✅ **View task details** by ID
- ✅ **Update own assigned tasks** (limited permissions):
  - ✅ Change task **status only** (PENDING → IN_PROGRESS → COMPLETED)
  - ❌ Cannot change title
  - ❌ Cannot change description
  - ❌ Cannot change priority
  - ❌ Cannot change due date
  - ❌ Cannot reassign to others
- ✅ **Complete own assigned tasks** (mark as completed with automatic date)
- ❌ Cannot update tasks assigned to others
- ❌ Cannot create new tasks

#### Dashboard & Analytics
- ✅ View **dashboard summary** with:
  - Total tasks count
  - Completed tasks count
  - Pending tasks count
  - On-time completion percentage
  - Productivity scores (can see own score)
- ✅ Track personal performance

#### Permissions Summary
| Action | Employee Access |
|--------|----------------|
| Create Task | ❌ No Access |
| View All Tasks | ✅ Read Only |
| Update Own Task Status | ✅ Status Only |
| Update Other Tasks | ❌ No Access |
| Complete Own Task | ✅ Allowed |
| Complete Other Tasks | ❌ No Access |
| View Dashboard | ✅ Read Only |
| Assign Tasks | ❌ No Access |

---

## 📡 Complete API Reference

### Base URL
```
http://localhost:8080
```

---

## 🔓 **PUBLIC ENDPOINTS** (No Authentication Required)

### 1. Register User
**Endpoint:** `POST /api/auth/register`

**Description:** Register a new user (MANAGER or EMPLOYEE)

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "EMPLOYEE"
}
```

**Validation Rules:**
- `name`: Required, not blank
- `email`: Required, valid email format, must be unique
- `password`: Required, minimum length
- `role`: Required, must be "MANAGER" or "EMPLOYEE"

**Success Response (200 OK):**
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

**Error Response (400 Bad Request):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email already exists",
  "path": "/api/auth/register"
}
```

**cURL Example:**
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

---

### 2. Login
**Endpoint:** `POST /api/auth/login`

**Description:** Authenticate user and receive JWT token

**Request Body:**
```json
{
  "email": "manager@example.com",
  "password": "password123"
}
```

**Validation Rules:**
- `email`: Required, valid email format
- `password`: Required

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYW5hZ2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA1MzE0NjAwLCJleHAiOjE3MDU0MDEwMDB9.abc123..."
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "manager@example.com",
    "password": "password123"
  }'
```

**Note:** Save the token from the response. Use it in the `Authorization` header for all protected endpoints:
```
Authorization: Bearer YOUR_JWT_TOKEN_HERE
```

---

## 🔒 **PROTECTED ENDPOINTS** (Authentication Required)

All endpoints below require JWT token in the Authorization header:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📋 **TASK ENDPOINTS**

### 3. Create Task
**Endpoint:** `POST /api/tasks`

**Access:** MANAGER only

**Description:** Create a new task and assign it to an employee

**Request Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Implement User Authentication",
  "description": "Develop JWT-based authentication system with login and registration",
  "priority": "HIGH",
  "dueDate": "2024-12-31",
  "assignedToId": 2
}
```

**Validation Rules:**
- `title`: Required, not blank
- `description`: Optional
- `priority`: Required, must be "LOW", "MEDIUM", or "HIGH"
- `dueDate`: Optional, must be valid date format (YYYY-MM-DD)
- `assignedToId`: Required, must be valid user ID

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "id": 7,
    "title": "Implement User Authentication",
    "description": "Develop JWT-based authentication system with login and registration",
    "status": "PENDING",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "completedDate": null,
    "assignedTo": {
      "id": 2,
      "name": "Alice Employee",
      "email": "alice@example.com",
      "role": "EMPLOYEE"
    },
    "createdAt": "2024-01-15T10:30:00"
  }
}
```

**Error Response (403 Forbidden):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/tasks"
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_MANAGER_TOKEN" \
  -d '{
    "title": "Implement User Authentication",
    "description": "Develop JWT-based authentication system",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "assignedToId": 2
  }'
```

---

### 4. Get All Tasks
**Endpoint:** `GET /api/tasks`

**Access:** MANAGER and EMPLOYEE

**Description:** Retrieve all tasks with optional filtering

**Request Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Query Parameters (Optional):**
- `assignedTo` (Long): Filter by assigned user ID
- `status` (String): Filter by status (PENDING, IN_PROGRESS, COMPLETED)

**Examples:**
- Get all tasks: `/api/tasks`
- Get tasks assigned to user 2: `/api/tasks?assignedTo=2`
- Get pending tasks: `/api/tasks?status=PENDING`
- Get pending tasks for user 2: `/api/tasks?assignedTo=2&status=PENDING`

**Success Response (200 OK):**
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
      "assignedTo": {
        "id": 2,
        "name": "Alice Employee",
        "email": "alice@example.com",
        "role": "EMPLOYEE"
      },
      "createdAt": "2024-01-10T10:00:00"
    },
    {
      "id": 3,
      "title": "Implement User Authentication",
      "description": "Develop JWT-based authentication system",
      "status": "IN_PROGRESS",
      "priority": "HIGH",
      "dueDate": "2024-01-25",
      "completedDate": null,
      "assignedTo": {
        "id": 2,
        "name": "Alice Employee",
        "email": "alice@example.com",
        "role": "EMPLOYEE"
      },
      "createdAt": "2024-01-10T10:00:00"
    }
  ]
}
```

**cURL Examples:**
```bash
# Get all tasks
curl http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN"

# Get tasks for specific user
curl "http://localhost:8080/api/tasks?assignedTo=2" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Get pending tasks
curl "http://localhost:8080/api/tasks?status=PENDING" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Get pending tasks for specific user
curl "http://localhost:8080/api/tasks?assignedTo=2&status=PENDING" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 5. Get Task by ID
**Endpoint:** `GET /api/tasks/{id}`

**Access:** MANAGER and EMPLOYEE

**Description:** Retrieve a specific task by its ID

**Request Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Path Parameters:**
- `id` (Long): Task ID

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Task retrieved successfully",
  "data": {
    "id": 1,
    "title": "Setup Development Environment",
    "description": "Configure local development environment with all necessary tools",
    "status": "COMPLETED",
    "priority": "HIGH",
    "dueDate": "2024-01-15",
    "completedDate": "2024-01-14",
    "assignedTo": {
      "id": 2,
      "name": "Alice Employee",
      "email": "alice@example.com",
      "role": "EMPLOYEE"
    },
    "createdAt": "2024-01-10T10:00:00"
  }
}
```

**Error Response (404 Not Found):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

**cURL Example:**
```bash
curl http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 6. Update Task
**Endpoint:** `PUT /api/tasks/{id}`

**Access:** 
- MANAGER: Can update all fields of any task
- EMPLOYEE: Can only update status of their own assigned tasks

**Description:** Update task details with role-based permissions

**Request Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
Content-Type: application/json
```

**Path Parameters:**
- `id` (Long): Task ID

**Request Body (All fields optional):**
```json
{
  "title": "Updated Task Title",
  "description": "Updated description",
  "status": "IN_PROGRESS",
  "priority": "MEDIUM",
  "dueDate": "2024-12-31",
  "assignedToId": 3
}
```

**MANAGER can update:**
- ✅ title
- ✅ description
- ✅ status
- ✅ priority
- ✅ dueDate
- ✅ assignedToId (reassign task)

**EMPLOYEE can update:**
- ✅ status (only for their own tasks)
- ❌ All other fields forbidden

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Task updated successfully",
  "data": {
    "id": 3,
    "title": "Updated Task Title",
    "description": "Updated description",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM",
    "dueDate": "2024-12-31",
    "completedDate": null,
    "assignedTo": {
      "id": 3,
      "name": "Bob Employee",
      "email": "bob@example.com",
      "role": "EMPLOYEE"
    },
    "createdAt": "2024-01-10T10:00:00"
  }
}
```

**Error Response (403 Forbidden - Employee trying to update other's task):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You can only update your own tasks",
  "path": "/api/tasks/3"
}
```

**Error Response (403 Forbidden - Employee trying to update non-status fields):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Employees can only update task status",
  "path": "/api/tasks/3"
}
```

**cURL Examples:**
```bash
# Manager updating task (all fields)
curl -X PUT http://localhost:8080/api/tasks/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_MANAGER_TOKEN" \
  -d '{
    "title": "Updated Title",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM"
  }'

# Employee updating own task status
curl -X PUT http://localhost:8080/api/tasks/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_EMPLOYEE_TOKEN" \
  -d '{
    "status": "IN_PROGRESS"
  }'
```

---

### 7. Complete Task
**Endpoint:** `PUT /api/tasks/{id}/complete`

**Access:** 
- MANAGER: Can complete any task
- EMPLOYEE: Can only complete their own assigned tasks

**Description:** Mark a task as completed and set completion date automatically

**Request Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Path Parameters:**
- `id` (Long): Task ID

**Request Body:** None required

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Task completed successfully",
  "data": {
    "id": 4,
    "title": "Create Task Management API",
    "description": "Build REST API endpoints for task management",
    "status": "COMPLETED",
    "priority": "MEDIUM",
    "dueDate": "2024-01-30",
    "completedDate": "2024-01-15",
    "assignedTo": {
      "id": 3,
      "name": "Bob Employee",
      "email": "bob@example.com",
      "role": "EMPLOYEE"
    },
    "createdAt": "2024-01-10T10:00:00"
  }
}
```

**Error Response (403 Forbidden - Employee trying to complete other's task):**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You can only complete your own tasks",
  "path": "/api/tasks/4/complete"
}
```

**cURL Examples:**
```bash
# Manager completing any task
curl -X PUT http://localhost:8080/api/tasks/4/complete \
  -H "Authorization: Bearer YOUR_MANAGER_TOKEN"

# Employee completing own task
curl -X PUT http://localhost:8080/api/tasks/3/complete \
  -H "Authorization: Bearer YOUR_EMPLOYEE_TOKEN"
```

---

## 📊 **DASHBOARD ENDPOINTS**

### 8. Get Dashboard Summary
**Endpoint:** `GET /api/dashboard/summary`

**Access:** MANAGER and EMPLOYEE

**Description:** Retrieve dashboard analytics including task counts and productivity scores

**Request Headers:**
```
Authorization: Bearer YOUR_JWT_TOKEN
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Dashboard summary retrieved successfully",
  "data": {
    "totalTasks": 6,
    "completedTasks": 2,
    "pendingTasks": 1,
    "onTimeCompletionPercent": 33.33,
    "productivityScores": {
      "Alice Employee": 66.67,
      "Bob Employee": 0.0,
      "John Manager": 100.0
    }
  }
}
```

**Response Fields:**
- `totalTasks`: Total number of tasks in the system
- `completedTasks`: Number of tasks with status COMPLETED
- `pendingTasks`: Number of tasks with status PENDING (not IN_PROGRESS or COMPLETED)
- `onTimeCompletionPercent`: Percentage of completed tasks (simplified calculation)
- `productivityScores`: Object with user names as keys and their productivity percentage as values
  - Calculated as: (completed tasks / total assigned tasks) × 100

**cURL Example:**
```bash
curl http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## 🔐 **AUTHENTICATION & AUTHORIZATION**

### JWT Token Usage

**Token Format:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwiaWF0IjoxNzA1MzE0NjAwLCJleHAiOjE3MDU0MDEwMDB9.signature
```

**Token Properties:**
- **Expiration:** 24 hours (86400000 ms)
- **Algorithm:** HS256
- **Claims:** email (subject), issued at, expiration

**How to Use:**
1. Login via `/api/auth/login`
2. Extract token from response
3. Include in all subsequent requests:
   ```
   Authorization: Bearer YOUR_TOKEN_HERE
   ```

---

## ❌ **ERROR RESPONSES**

### Standard Error Format
All errors follow this format:
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Detailed error message",
  "path": "/api/endpoint"
}
```

### HTTP Status Codes

| Code | Error | Description |
|------|-------|-------------|
| 400 | Bad Request | Validation error, duplicate email, invalid input |
| 401 | Unauthorized | Missing or invalid JWT token, wrong credentials |
| 403 | Forbidden | Insufficient permissions for the operation |
| 404 | Not Found | Resource (task/user) not found |
| 500 | Internal Server Error | Unexpected server error |

### Common Error Scenarios

**401 Unauthorized:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid email or password",
  "path": "/api/auth/login"
}
```

**403 Forbidden:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You can only update your own tasks",
  "path": "/api/tasks/3"
}
```

**404 Not Found:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

**400 Validation Error:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "email: must be a valid email, password: must not be blank",
  "path": "/api/auth/register"
}
```

---

## 📝 **SAMPLE DATA**

### Pre-loaded Users

| ID | Name | Email | Password | Role |
|----|------|-------|----------|------|
| 1 | John Manager | manager@example.com | password123 | MANAGER |
| 2 | Alice Employee | alice@example.com | password123 | EMPLOYEE |
| 3 | Bob Employee | bob@example.com | password123 | EMPLOYEE |

### Pre-loaded Tasks

| ID | Title | Status | Priority | Assigned To |
|----|-------|--------|----------|-------------|
| 1 | Setup Development Environment | COMPLETED | HIGH | Alice |
| 2 | Design Database Schema | COMPLETED | HIGH | Alice |
| 3 | Implement User Authentication | IN_PROGRESS | HIGH | Alice |
| 4 | Create Task Management API | PENDING | MEDIUM | Bob |
| 5 | Write Unit Tests | PENDING | MEDIUM | Bob |
| 6 | Setup CI/CD Pipeline | PENDING | LOW | Bob |

---

## 🌐 **CORS Configuration**

**Allowed Origin:** `http://localhost:3000`

**Allowed Methods:** GET, POST, PUT, DELETE, OPTIONS

**Allowed Headers:** All headers (*)

**Credentials:** Allowed

---

## 📊 **API Summary Table**

| # | Method | Endpoint | Access | Description |
|---|--------|----------|--------|-------------|
| 1 | POST | `/api/auth/register` | Public | Register new user |
| 2 | POST | `/api/auth/login` | Public | Login and get JWT token |
| 3 | POST | `/api/tasks` | MANAGER | Create new task |
| 4 | GET | `/api/tasks` | All | Get all tasks (with filters) |
| 5 | GET | `/api/tasks/{id}` | All | Get task by ID |
| 6 | PUT | `/api/tasks/{id}` | All* | Update task (role-based) |
| 7 | PUT | `/api/tasks/{id}/complete` | All* | Complete task (role-based) |
| 8 | GET | `/api/dashboard/summary` | All | Get dashboard analytics |

*All authenticated users, but with different permissions based on role

---

## 🎯 **Quick Test Sequence**

```bash
# 1. Login as Manager
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manager@example.com","password":"password123"}' \
  | jq -r '.data.token')

# 2. Create Task
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "title": "New Task",
    "description": "Test task",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "assignedToId": 2
  }'

# 3. Get All Tasks
curl http://localhost:8080/api/tasks \
  -H "Authorization: Bearer $TOKEN"

# 4. Get Dashboard
curl http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📚 **Additional Resources**

- **Full Testing Guide:** See `API_TESTS.md`
- **Deployment Guide:** See `DEPLOYMENT_CHECKLIST.md`
- **Quick Reference:** See `QUICK_REFERENCE.md`
- **Project Status:** See `PROJECT_STATUS.md`
