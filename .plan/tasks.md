# Implementation Plan

- [] 1. Set up project structure and Maven configuration
  - Updated Maven project with pom.xml including all required dependencies (JWT, Spring Boot, etc.)
  - Set up package structure: com.productivity.dashboard with model, repository, dto, service, controller, config
  - Created main Application boot class TeamProductivityApplication with @SpringBootApplication
  - Updated artifact name to team-productivity-backend as required
  - _Requirements: 7.4_

- [] 2. Implement domain models and enums
  - [x] 2.1 Create enum classes for Role, TaskStatus, and Priority
    - Created Role enum with MANAGER and EMPLOYEE values
    - Created TaskStatus enum with PENDING, IN_PROGRESS, COMPLETED values  
    - Created Priority enum with LOW, MEDIUM, HIGH values
    - _Requirements: 7.1, 7.3_

  - [] 2.2 Implement User entity with JPA annotations
    - Created User.java with all fields (id, name, email, password, role, createdAt)
    - Added JPA annotations (@Entity, @Id, @GeneratedValue, @Column with unique constraint on email)
    - Implemented explicit getters and setters (no Lombok as required)
    - Added @PrePersist for createdAt timestamp
    - _Requirements: 1.1, 7.3_

  - [] 2.3 Implement Task entity with JPA annotations and relationships
    - Created Task.java with all fields (id, title, description, status, priority, dueDate, completedDate, assignedTo, createdAt)
    - Added JPA annotations including @ManyToOne for assignedTo relationship
    - Implemented explicit getters and setters (no Lombok as required)
    - Added @PrePersist for createdAt timestamp
    - _Requirements: 2.1, 7.3_

- [] 3. Create repository interfaces
  - [] 3.1 Implement UserRepository interface
    - Created UserRepository extending JpaRepository
    - Added custom query method findByEmail
    - Added custom query method existsByEmail
    - _Requirements: 1.1, 1.3_


  - [] 3.2 Implement TaskRepository interface
    - Created TaskRepository extending JpaRepository
    - Added custom query methods: findByAssignedToId, findByStatus, findByAssignedToIdAndStatus
    - Added query methods for analytics: countTotalTasks, countCompletedTasks, countPendingTasks
    - Added query method to find on-time completed tasks for analytics
    - Added user-specific task counting methods
    - _Requirements: 2.5, 4.1, 4.2, 4.3, 4.4_

- [ ] 4. Create DTO classes for API contracts

  - [ ] 4.1 Create authentication DTOs
    - Write RegisterRequest DTO with validation annotations
    - Write LoginRequest DTO with validation annotations


    - Write AuthResponse DTO for login response with token


    - _Requirements: 1.1, 1.3, 6.2_

  - [ ] 4.2 Create task DTOs
    - Write TaskRequest DTO for create/update operations
    - Write TaskResponse DTO for API responses


    - Write TaskSummary DTO for list views
    - _Requirements: 2.1, 2.3, 2.5_

  - [ ] 4.3 Create dashboard DTOs
    - Write DashboardSummary DTO with all metrics fields


    - Write ProductivityScore DTO for per-user scores
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

  - [x] 4.4 Create error response DTO


    - Write ErrorResponse DTO with timestamp, status, error, message, path fields


    - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_



- [ ] 5. Implement JWT utilities and security configuration

  - [ ] 5.1 Create JWT token provider utility
    - Write JwtTokenProvider class with methods to generate and validate tokens
    - Implement token generation with userId, email, and role claims


    - Implement token validation and claims extraction
    - Add configurable secret key and expiration from properties
    - _Requirements: 1.3, 1.5_

  - [ ] 5.2 Implement JWT authentication filter
    - Create JwtAuthenticationFilter extending OncePerRequestFilter


    - Extract JWT from Authorization header
    - Validate token and set SecurityContext authentication
    - Handle token validation exceptions
    - _Requirements: 1.5_

  - [ ] 5.3 Configure Spring Security
    - Create SecurityConfig class with @EnableWebSecurity
    - Configure BCryptPasswordEncoder bean

    - Set up security filter chain with JWT filter
    - Configure public endpoints (/api/auth/**) and protected endpoints
    - Disable CSRF for stateless API
    - Enable method-level security with @EnableGlobalMethodSecurity
    - _Requirements: 1.1, 1.5, 1.6_





  - [ ] 5.4 Configure CORS
    - Add CORS configuration in SecurityConfig or separate CorsConfig
    - Allow origin http://localhost:3000
    - Allow methods GET, POST, PUT, DELETE
    - Allow headers Authorization and Content-Type
    - _Requirements: 1.6_



- [ ] 6. Implement service layer with business logic

  - [ ] 6.1 Create AuthService interface and implementation
    - Define AuthService interface with register and login methods
    - Implement AuthServiceImpl with UserRepository and PasswordEncoder
    - Implement register method with email uniqueness check and password hashing


    - Implement login method with authentication and JWT token generation
    - Add exception handling for duplicate email and invalid credentials
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [ ] 6.2 Create UserService interface and implementation
    - Define UserService interface with user retrieval methods
    - Implement UserServiceImpl with UserRepository
    - Implement findById and findByEmail methods
    - Add exception handling for user not found
    - _Requirements: 2.4_



  - [ ] 6.3 Create TaskService interface and implementation
    - Define TaskService interface with CRUD and status update methods
    - Implement TaskServiceImpl with TaskRepository and UserRepository
    - Implement createTask method with user assignment validation
    - Implement updateTask method with authorization logic (manager or assigned employee)
    - Implement completeTask method to set status and completedDate


    - Implement getTasks method with optional filtering by assignedTo and status


    - Implement getTaskById method
    - Add exception handling for task not found and authorization errors
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 3.2, 3.3, 3.4_

  - [ ] 6.4 Create DashboardService interface and implementation
    - Define DashboardService interface with getSummary method


    - Implement DashboardServiceImpl with TaskRepository
    - Calculate totalTasks count
    - Calculate completedTasks count
    - Calculate pendingTasks count (PENDING + IN_PROGRESS)
    - Calculate onTimeCompletionPercent (completed by or before dueDate)
    - Calculate productivityScores per user (completed/assigned ratio)
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_



- [ ] 7. Implement controller layer with REST endpoints

  - [ ] 7.1 Create AuthController
    - Implement POST /api/auth/register endpoint
    - Implement POST /api/auth/login endpoint
    - Add request validation using @Valid
    - Return appropriate DTOs and HTTP status codes
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

  - [x] 7.2 Create TaskController


    - Implement POST /api/tasks endpoint with @PreAuthorize("hasRole('MANAGER')")
    - Implement GET /api/tasks endpoint with optional query params
    - Implement GET /api/tasks/{id} endpoint
    - Implement PUT /api/tasks/{id} endpoint with authorization logic
    - Implement PUT /api/tasks/{id}/complete endpoint
    - Add request validation and proper response DTOs
    - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5, 3.1, 3.2, 3.3, 3.4_

  - [x] 7.3 Create DashboardController


    - Implement GET /api/dashboard/summary endpoint


    - Return DashboardSummary DTO with all calculated metrics
    - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_

- [ ] 8. Implement global exception handling
  - Create GlobalExceptionHandler with @ControllerAdvice
  - Handle BadRequestException and return 400 with ErrorResponse
  - Handle UnauthorizedException and return 401 with ErrorResponse
  - Handle ForbiddenException and return 403 with ErrorResponse


  - Handle NotFoundException and return 404 with ErrorResponse
  - Handle MethodArgumentNotValidException for validation errors
  - Handle generic exceptions and return 500 with ErrorResponse


  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_



- [ ] 9. Create custom exception classes
  - Write BadRequestException extending RuntimeException
  - Write UnauthorizedException extending RuntimeException
  - Write ForbiddenException extending RuntimeException
  - Write NotFoundException extending RuntimeException
  - _Requirements: 6.1, 6.2, 6.3, 6.4, 6.5_

- [ ] 10. Configure application properties and database

  - [ ] 10.1 Create application.properties file
    - Configure H2 datasource with in-memory database
    - Enable H2 console
    - Configure JPA properties (ddl-auto, show-sql)
    - Add JWT secret and expiration configuration
    - Add commented MySQL configuration as alternative
    - Configure server port (8080)
    - _Requirements: 5.1, 5.4_

  - [ ] 10.2 Create data.sql initialization script
    - Insert 1 MANAGER user with BCrypt ha4 with Erword
    - Insert 2 EMPLOYEE users with BCrypt hashed passwords
    - Insert 6 tasks with varied statuses (PENDING, IN_PROGRESS, COMPLETED)
    - Include tasks with different priorities (LOW, MEDIUM, HIGH)
    - Include tasks with varied due dates (past, present, future)
    - Assign tasks to different users
    - Set completedDate for completed tasks (some on-time, some late)
    - _Requirements: 5.2, 5.3_

- [ ] 11. Verify and test the complete application
  - Run mvn clean install to build the project
  - Run mvn spring-boot:run to start the application
  - Test POST /api/auth/register with sample user data
  - Test POST /api/auth/login and verify JWT token is returned
  - Test POST /api/tasks with JWT token as MANAGER
  - Test GET /api/tasks with filters
  - Test PUT /api/tasks/{id}/complete
  - Test GET /api/dashboard/summary
  - Verify error responses for unauthorized access
  - Verify CORS headers for requests from http://localhost:3000
  - _Requirements: All_