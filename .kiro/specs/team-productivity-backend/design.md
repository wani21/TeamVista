# Design Document

## Overview

The Team Productivity Dashboard backend is a RESTful API built with Spring Boot, Maven, and Java 11+. It provides task management and productivity analytics for teams with role-based access control. The system uses JWT for authentication, Spring Security for authorization, and Spring Data JPA for persistence. The default configuration uses H2 in-memory database for easy setup and testing, with optional MySQL support.

## Architecture

The application follows a layered architecture pattern:

- **Controller Layer**: REST endpoints handling HTTP requests/responses
- **Service Layer**: Business logic and orchestration
- **Repository Layer**: Data access using Spring Data JPA
- **Model Layer**: JPA entities representing domain objects
- **DTO Layer**: Data transfer objects for API contracts
- **Config Layer**: Spring Security, JWT, and CORS configuration

The architecture ensures separation of concerns, testability, and maintainability.

## Components and Interfaces

### 1. Authentication & Authorization

**Components:**
- `JwtTokenProvider`: Generates and validates JWT tokens
- `JwtAuthenticationFilter`: Intercepts requests to validate JWT tokens
- `SecurityConfig`: Configures Spring Security with JWT and CORS
- `AuthService`: Handles user registration and login logic
- `AuthController`: Exposes authentication endpoints

**Flow:**
1. User registers via POST /api/auth/register
2. User logs in via POST /api/auth/login, receives JWT token
3. Subsequent requests include JWT in Authorization header
4. JwtAuthenticationFilter validates token and sets authentication context
5. Spring Security enforces role-based access control

### 2. User Management

**Components:**
- `User` entity: Stores user data with role (MANAGER/EMPLOYEE)
- `UserRepository`: Spring Data JPA interface for user persistence
- `UserService`: Business logic for user operations
- `UserController`: (Optional) User-related endpoints

**Key Operations:**
- Create user with BCrypt password hashing
- Find user by email (for authentication)
- Retrieve user details

### 3. Task Management

**Components:**
- `Task` entity: Stores task data with status, priority, dates, and assignment
- `TaskRepository`: Spring Data JPA interface with custom queries
- `TaskService`: Business logic for task CRUD and status updates
- `TaskController`: REST endpoints for task operations

**Key Operations:**
- Create task (MANAGER only)
- List tasks with optional filters (assignedTo, status)
- Get task by ID
- Update task (MANAGER or assigned employee for status)
- Mark task as completed with completedDate

### 4. Dashboard Analytics

**Components:**
- `DashboardService`: Calculates productivity metrics
- `DashboardController`: Exposes analytics endpoint

**Metrics Calculated:**
- Total tasks count
- Completed tasks count
- Pending tasks count (PENDING + IN_PROGRESS)
- On-time completion percentage
- Productivity scores per user (completed/assigned ratio)

### 5. Error Handling

**Components:**
- `GlobalExceptionHandler`: @ControllerAdvice for centralized error handling
- `ErrorResponse`: DTO for consistent error responses

**Handled Exceptions:**
- Validation errors (400)
- Authentication errors (401)
- Authorization errors (403)
- Resource not found (404)
- General server errors (500)

## Data Models

### User Entity
```
User
- id: Long (PK, auto-generated)
- name: String (not null)
- email: String (unique, not null)
- password: String (BCrypt hashed, not null)
- role: Role enum (MANAGER, EMPLOYEE)
- createdAt: LocalDateTime (auto-set)
```

### Task Entity
```
Task
- id: Long (PK, auto-generated)
- title: String (not null)
- description: String
- status: TaskStatus enum (PENDING, IN_PROGRESS, COMPLETED)
- priority: Priority enum (LOW, MEDIUM, HIGH)
- dueDate: LocalDate
- completedDate: LocalDate (nullable)
- assignedTo: User (ManyToOne relationship)
- createdAt: LocalDateTime (auto-set)
```

### Enums
- `Role`: MANAGER, EMPLOYEE
- `TaskStatus`: PENDING, IN_PROGRESS, COMPLETED
- `Priority`: LOW, MEDIUM, HIGH

### Relationships
- Task.assignedTo → User (ManyToOne)
- User can have multiple assigned tasks (OneToMany, not explicitly mapped)

## API Endpoints

### Authentication
- `POST /api/auth/register`: Register new user
  - Request: `{name, email, password, role}`
  - Response: `{message, userId}`
  
- `POST /api/auth/login`: Login and get JWT
  - Request: `{email, password}`
  - Response: `{token, userId, name, role}`

### Task Management
- `POST /api/tasks`: Create task (MANAGER only)
  - Request: `{title, description, status, priority, dueDate, assignedToId}`
  - Response: `{id, title, description, status, priority, dueDate, assignedTo, createdAt}`
  
- `GET /api/tasks`: List tasks with optional filters
  - Query params: `assignedTo` (userId), `status`
  - Response: `[{task objects}]`
  
- `GET /api/tasks/{id}`: Get task details
  - Response: `{task object with user details}`
  
- `PUT /api/tasks/{id}`: Update task
  - Request: `{title, description, status, priority, dueDate, assignedToId}`
  - Response: `{updated task object}`
  
- `PUT /api/tasks/{id}/complete`: Mark task completed
  - Response: `{updated task object with completedDate}`

### Dashboard
- `GET /api/dashboard/summary`: Get analytics
  - Response: `{totalTasks, completedTasks, pendingTasks, onTimeCompletionPercent, productivityScores: [{userId, userName, score}]}`

## Security Configuration

### JWT Implementation
- Token generation on successful login
- Token contains: userId, email, role
- Token expiration: 24 hours (configurable)
- Token validation on each protected request
- Secret key stored in application.properties

### Spring Security
- Stateless session management
- JWT filter added before UsernamePasswordAuthenticationFilter
- Public endpoints: /api/auth/**
- Protected endpoints: /api/tasks/**, /api/dashboard/**
- Role-based authorization using @PreAuthorize annotations

### CORS Configuration
- Allow origin: http://localhost:3000
- Allow methods: GET, POST, PUT, DELETE
- Allow headers: Authorization, Content-Type
- Allow credentials: true

### Password Security
- BCryptPasswordEncoder with strength 12
- Passwords never returned in API responses
- Password validation on registration (minimum length)

## Error Handling

### Global Exception Handler
Catches and transforms exceptions into consistent JSON responses:

```
{
  "timestamp": "2025-10-25T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tasks"
}
```

### Exception Types
- `BadRequestException`: Invalid input (400)
- `UnauthorizedException`: Authentication failed (401)
- `ForbiddenException`: Insufficient permissions (403)
- `NotFoundException`: Resource not found (404)
- `MethodArgumentNotValidException`: Validation errors (400)

## Database Configuration

### H2 In-Memory (Default)
```properties
spring.datasource.url=jdbc:h2:mem:productivitydb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
```

### MySQL (Optional)
```properties
# spring.datasource.url=jdbc:mysql://localhost:3306/productivitydb
# spring.datasource.username=root
# spring.datasource.password=password
# spring.jpa.hibernate.ddl-auto=update
```

### Data Initialization
`data.sql` script runs on startup with:
- 1 MANAGER user
- 2 EMPLOYEE users
- 6 tasks with varied statuses, priorities, and due dates
- Mix of on-time and overdue tasks for analytics testing

## Testing Strategy

### Unit Tests
- Service layer: Test business logic with mocked repositories
- Repository layer: Test custom queries with @DataJpaTest
- JWT utilities: Test token generation and validation
- Exception handling: Test error response formatting

### Integration Tests
- Controller layer: Test endpoints with @SpringBootTest and MockMvc
- Security: Test authentication and authorization flows
- End-to-end: Test complete user journeys (register → login → create task → update → analytics)

### Test Data
- Use in-memory H2 for all tests
- Create test fixtures for users and tasks
- Test edge cases: expired tokens, unauthorized access, invalid data

### Coverage Goals
- Service layer: 80%+ coverage
- Controller layer: 70%+ coverage
- Critical paths (auth, task creation): 90%+ coverage

## Build and Deployment

### Maven Configuration
- Artifact: team-productivity-backend
- Group: com.productivity
- Java version: 11+
- Spring Boot version: 2.7.x or 3.x (compatible with Java 11+)
- Packaging: JAR

### Dependencies
- spring-boot-starter-web
- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-validation
- h2database
- mysql-connector-java (optional)
- jjwt (JWT library)
- spring-boot-starter-test

### Running the Application
```bash
mvn clean install
mvn spring-boot:run
```

Application runs on port 8080 by default.

### Environment Configuration
- Development: H2 in-memory with data.sql initialization
- Production: MySQL with environment-specific properties
- JWT secret: Externalized via environment variable or properties file 