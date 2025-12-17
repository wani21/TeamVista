# Quick Reference Card

## 🚀 Start Application

```bash
mvn spring-boot:run
```

## 🔑 Sample Credentials

```
Manager:  manager@example.com / password123
Employee: alice@example.com / password123
Employee: bob@example.com / password123
```

## 📡 API Endpoints

### Auth (Public)
```bash
POST /api/auth/register
POST /api/auth/login
```

### Tasks (Protected)
```bash
POST   /api/tasks              # Create (MANAGER only)
GET    /api/tasks              # List all
GET    /api/tasks/{id}         # Get by ID
PUT    /api/tasks/{id}         # Update
PUT    /api/tasks/{id}/complete # Complete
```

### Dashboard (Protected)
```bash
GET    /api/dashboard/summary  # Analytics
```

## 🧪 Quick Test

### 1. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manager@example.com","password":"password123"}'
```

### 2. Get Tasks (use token from step 1)
```bash
curl http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

## 🔧 Configuration

**Database:** `src/main/resources/application.properties`
```properties
# MySQL (default)
spring.datasource.url=jdbc:mysql://localhost:3306/team_productivity
spring.datasource.username=root
spring.datasource.password=Engineering!123

# H2 (uncomment for in-memory)
#spring.datasource.url=jdbc:h2:mem:testdb
```

**JWT:** `src/main/resources/application.properties`
```properties
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidation12345678901234567890
jwt.expiration=86400000
```

## 📦 Build

```bash
# Clean and compile
mvn clean compile

# Package JAR
mvn clean package -DskipTests

# Run JAR
java -jar target/team-productivity-backend-1.0.0.jar
```

## 🐛 Troubleshooting

**Port already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Database connection failed:**
```bash
# Check MySQL is running
# Or switch to H2 (uncomment H2 config)
```

**JWT token expired:**
```bash
# Login again to get new token
```

## 📊 Roles & Permissions

| Action | MANAGER | EMPLOYEE |
|--------|---------|----------|
| Create Task | ✅ | ❌ |
| View Tasks | ✅ | ✅ |
| Update Own Task Status | ✅ | ✅ |
| Update Any Task | ✅ | ❌ |
| Complete Own Task | ✅ | ✅ |
| Complete Any Task | ✅ | ❌ |
| View Dashboard | ✅ | ✅ |

## 🎯 Response Format

**Success:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

**Error:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/tasks/999"
}
```

## 🔐 Security Headers

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
Content-Type: application/json
```

## 📝 Request Examples

**Register:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "role": "EMPLOYEE"
}
```

**Create Task:**
```json
{
  "title": "Task Title",
  "description": "Task Description",
  "priority": "HIGH",
  "dueDate": "2024-12-31",
  "assignedToId": 2
}
```

**Update Task:**
```json
{
  "status": "IN_PROGRESS",
  "priority": "MEDIUM"
}
```

## 🌐 CORS

Allowed origin: `http://localhost:3000`

## 📚 Documentation Files

- `README.md` - Overview & quick start
- `API_TESTS.md` - Detailed test cases
- `IMPLEMENTATION_SUMMARY.md` - Architecture
- `DEPLOYMENT_CHECKLIST.md` - Deployment guide
- `PROJECT_STATUS.md` - Project status
- `QUICK_REFERENCE.md` - This file

## 🆘 Common Issues

**401 Unauthorized:** Missing or invalid token
**403 Forbidden:** Insufficient permissions
**404 Not Found:** Resource doesn't exist
**400 Bad Request:** Validation error or duplicate email

## 💡 Tips

- Token expires in 24 hours
- All passwords are BCrypt hashed
- Use H2 for quick testing (no MySQL needed)
- Check logs for detailed error messages
- Sample data loads automatically on startup
