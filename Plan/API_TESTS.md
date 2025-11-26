# API Testing Guide

This document provides step-by-step instructions to test all API endpoints.

## Prerequisites
- Application running on http://localhost:8080
- curl or Postman installed
- Sample data loaded from data.sql

## Test Sequence

### 1. Test User Registration

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "role": "EMPLOYEE"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "User registered successfully",
  "data": {
    "id": 4,
    "name": "Test User",
    "email": "test@example.com",
    "role": "EMPLOYEE"
  }
}
```

### 2. Test Login (Manager)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "manager@example.com",
    "password": "password123"
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9..."
  }
}
```

**Save the token for subsequent requests!**

### 3. Test Login (Employee)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "alice@example.com",
    "password": "password123"
  }'
```

### 4. Test Create Task (Manager Only)

Replace `YOUR_MANAGER_TOKEN` with the token from step 2.

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_MANAGER_TOKEN" \
  -d '{
    "title": "New Feature Development",
    "description": "Implement new dashboard feature",
    "priority": "HIGH",
    "dueDate": "2024-12-31",
    "assignedToId": 2
  }'
```

**Expected Response:**
```json
{
  "success": true,
  "message": "Task created successfully",
  "data": {
    "id": 7,
    "title": "New Feature Development",
    "status": "PENDING",
    "priority": "HIGH"
  }
}
```

### 5. Test Get All Tasks

```bash
curl -X GET http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 6. Test Get Tasks with Filters

**Filter by assigned user:**
```bash
curl -X GET "http://localhost:8080/api/tasks?assignedTo=2" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Filter by status:**
```bash
curl -X GET "http://localhost:8080/api/tasks?status=PENDING" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Filter by both:**
```bash
curl -X GET "http://localhost:8080/api/tasks?assignedTo=2&status=IN_PROGRESS" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 7. Test Get Task by ID

```bash
curl -X GET http://localhost:8080/api/tasks/1 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 8. Test Update Task (Manager)

```bash
curl -X PUT http://localhost:8080/api/tasks/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_MANAGER_TOKEN" \
  -d '{
    "title": "Updated Title",
    "status": "IN_PROGRESS",
    "priority": "MEDIUM"
  }'
```

### 9. Test Update Task Status (Employee)

Employee can only update status of their own tasks:

```bash
curl -X PUT http://localhost:8080/api/tasks/3 \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_EMPLOYEE_TOKEN" \
  -d '{
    "status": "COMPLETED"
  }'
```

### 10. Test Complete Task

```bash
curl -X PUT http://localhost:8080/api/tasks/4/complete \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### 11. Test Dashboard Summary

```bash
curl -X GET http://localhost:8080/api/dashboard/summary \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected Response:**
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
      "Bob Employee": 0.0
    }
  }
}
```

## Error Testing

### Test Unauthorized Access (No Token)

```bash
curl -X GET http://localhost:8080/api/tasks
```

**Expected:** 401 Unauthorized

### Test Forbidden Access (Employee Creating Task)

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_EMPLOYEE_TOKEN" \
  -d '{
    "title": "Test",
    "priority": "HIGH",
    "assignedToId": 2
  }'
```

**Expected:** 403 Forbidden

### Test Not Found

```bash
curl -X GET http://localhost:8080/api/tasks/999 \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected:** 404 Not Found

### Test Validation Error

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "",
    "email": "invalid-email",
    "password": "123"
  }'
```

**Expected:** 400 Bad Request with validation errors

### Test Duplicate Email

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Duplicate User",
    "email": "manager@example.com",
    "password": "password123",
    "role": "EMPLOYEE"
  }'
```

**Expected:** 400 Bad Request - "Email already exists"

## CORS Testing

Test from a frontend application running on http://localhost:3000:

```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    email: 'manager@example.com',
    password: 'password123'
  })
})
.then(response => response.json())
.then(data => console.log(data));
```

## Sample Users (from data.sql)

| Email | Password | Role |
|-------|----------|------|
| manager@example.com | password123 | MANAGER |
| alice@example.com | password123 | EMPLOYEE |
| bob@example.com | password123 | EMPLOYEE |

## Notes

- All passwords in sample data are BCrypt hashed
- JWT tokens expire after 24 hours (configurable in application.properties)
- CORS is configured to allow requests from http://localhost:3000
- H2 console available at http://localhost:8080/h2-console (if enabled)
