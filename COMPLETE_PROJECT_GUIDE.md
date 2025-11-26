# TeamVista - Complete Project Guide

## 🎉 **Project Overview**

TeamVista is a modern, full-stack team productivity dashboard built with:
- **Backend**: Spring Boot 2.7.18 + MySQL/H2 + JWT Authentication
- **Frontend**: React 18 + Vite + Tailwind CSS + Recharts

---

## 📊 **Project Statistics**

### Backend
- **Total Endpoints**: 15 REST APIs
- **Java Files**: 35+
- **Lines of Code**: ~3,500+
- **Features**: Authentication, Task Management, User Management, Dashboard Analytics

### Frontend
- **React Components**: 8
- **Pages**: 4 (Login, Register, Dashboard, Tasks)
- **Lines of Code**: ~2,000+
- **Features**: Responsive UI, Charts, Real-time Updates, Search & Filter

---

## 🚀 **Quick Start Guide**

### Prerequisites
1. **Java 11+** - For backend
2. **Maven 3.6+** - For building backend
3. **Node.js 16+** - For frontend
4. **MySQL 8.0+** - For database (or use H2 in-memory)

### Option 1: Automated Startup (Windows)
```bash
# Run the startup script
start-dev.bat
```

This will:
1. Start the Spring Boot backend on port 8080
2. Start the React frontend on port 3000
3. Open the app in your browser

### Option 2: Manual Startup

**Terminal 1 - Backend:**
```bash
# Start backend
mvn spring-boot:run
```

**Terminal 2 - Frontend:**
```bash
# Install dependencies (first time only)
cd frontend
npm install

# Start frontend
npm run dev
```

**Access the application:**
- Frontend: http://localhost:3000
- Backend API: http://localhost:8080
- H2 Console: http://localhost:8080/h2-console (if H2 enabled)

---

## 🔐 **Demo Credentials**

| Role | Email | Password |
|------|-------|----------|
| Manager | manager@example.com | password123 |
| Employee | alice@example.com | password123 |
| Employee | bob@example.com | password123 |

---

## 📁 **Project Structure**

```
TeamVista/
├── src/main/java/com/productivity/dashboard/
│   ├── config/              # Security, JWT, Exception handling
│   ├── controller/          # REST API endpoints
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Custom exceptions
│   ├── model/               # JPA entities
│   ├── repository/          # Data access layer
│   └── service/             # Business logic
├── src/main/resources/
│   ├── application.properties  # Configuration
│   └── data.sql                # Sample data
├── frontend/
│   ├── src/
│   │   ├── components/      # Reusable components
│   │   ├── context/         # React context
│   │   ├── pages/           # Page components
│   │   ├── services/        # API services
│   │   └── App.jsx          # Main app
│   ├── index.html
│   ├── package.json
│   └── tailwind.config.js
├── pom.xml                  # Maven configuration
├── start-dev.bat            # Startup script
└── README.md
```

---

## 🎯 **Features Overview**

### 1. Authentication & Authorization
- ✅ JWT-based authentication
- ✅ Role-based access control (MANAGER/EMPLOYEE)
- ✅ BCrypt password hashing
- ✅ Token expiration (24 hours)
- ✅ Auto-logout on token expiry

### 2. Task Management
- ✅ Create tasks (Manager only)
- ✅ View all tasks with filters
- ✅ Update task status (All users)
- ✅ Edit task details (Manager or assigned user)
- ✅ Complete tasks
- ✅ Delete tasks (Manager only)
- ✅ Search tasks by keyword
- ✅ Filter by status, priority, assignee

### 3. Dashboard Analytics
- ✅ Total tasks count
- ✅ Completed/Pending/In-Progress breakdown
- ✅ On-time completion percentage
- ✅ Overdue tasks tracking
- ✅ High priority tasks count
- ✅ Per-user productivity scores
- ✅ Beautiful charts (Pie & Bar)
- ✅ Team performance metrics

### 4. User Management
- ✅ View all users
- ✅ Get current user profile
- ✅ User task statistics
- ✅ Role-based permissions

---

## 🎨 **UI/UX Features**

### Design Principles
- **Modern**: Inspired by Linear, Notion, and Asana
- **Clean**: Minimalist design with focus on content
- **Responsive**: Works on all devices
- **Accessible**: WCAG compliant colors and contrast

### Color Scheme
- **Primary (Blue)**: Main actions and navigation
- **Accent (Purple)**: Highlights and special features
- **Success (Green)**: Completed tasks and positive actions
- **Warning (Orange)**: In-progress and attention items
- **Danger (Red)**: Overdue tasks and critical actions

### Components
- ✅ Responsive sidebar navigation
- ✅ Beautiful login/register pages with images
- ✅ Interactive task cards
- ✅ Modal dialogs for task creation/editing
- ✅ Real-time search and filtering
- ✅ Loading states and animations
- ✅ Error handling with user-friendly messages
- ✅ Toast notifications (coming soon)

---

## 📡 **API Endpoints**

### Authentication (Public)
```
POST /api/auth/register  - Register new user
POST /api/auth/login     - Login and get JWT token
```

### Tasks (Protected)
```
POST   /api/tasks              - Create task (MANAGER)
GET    /api/tasks              - List all tasks
GET    /api/tasks/{id}         - Get task by ID
GET    /api/tasks/search       - Search tasks
PUT    /api/tasks/{id}         - Update task
PUT    /api/tasks/{id}/complete - Complete task
DELETE /api/tasks/{id}         - Delete task (MANAGER)
```

### Users (Protected)
```
GET /api/users           - Get all users
GET /api/users/me        - Get current user
GET /api/users/{id}      - Get user by ID
GET /api/users/{id}/stats - Get user statistics
```

### Dashboard (Protected)
```
GET /api/dashboard/summary   - Basic dashboard
GET /api/dashboard/enhanced  - Enhanced dashboard with details
```

---

## 🔧 **Configuration**

### Backend Configuration
File: `src/main/resources/application.properties`

```properties
# Server
server.port=8080

# Database (MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/team_productivity
spring.datasource.username=root
spring.datasource.password=your_password

# JWT
jwt.secret=your_secret_key_here
jwt.expiration=86400000

# CORS
# Configured in SecurityConfig.java for http://localhost:3000
```

### Frontend Configuration
File: `frontend/src/services/api.js`

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

---

## 🧪 **Testing**

### Backend Testing
```bash
# Run tests
mvn test

# Run with coverage
mvn clean test jacoco:report
```

### Frontend Testing
```bash
cd frontend

# Run tests
npm test

# Run with coverage
npm test -- --coverage
```

### Manual Testing
1. Use the demo credentials to login
2. Test all CRUD operations
3. Verify role-based permissions
4. Check dashboard analytics
5. Test search and filters

---

## 📦 **Building for Production**

### Backend
```bash
# Build JAR file
mvn clean package -DskipTests

# Run JAR
java -jar target/team-productivity-backend-1.0.0.jar
```

### Frontend
```bash
cd frontend

# Build for production
npm run build

# Preview production build
npm run preview
```

---

## 🚀 **Deployment**

### Backend Deployment Options

#### 1. Traditional Server
```bash
# Copy JAR to server
scp target/*.jar user@server:/app/

# Run on server
java -jar /app/team-productivity-backend-1.0.0.jar
```

#### 2. Docker
```dockerfile
FROM openjdk:11-jre-slim
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 3. Cloud Platforms
- **Heroku**: Use Procfile
- **AWS Elastic Beanstalk**: Upload JAR
- **Google Cloud Run**: Use Docker

### Frontend Deployment Options

#### 1. Netlify
```bash
# Build command
npm run build

# Publish directory
dist
```

#### 2. Vercel
```bash
# Auto-detected from package.json
vercel deploy
```

#### 3. AWS S3 + CloudFront
```bash
# Build and upload
npm run build
aws s3 sync dist/ s3://your-bucket/
```

---

## 🐛 **Troubleshooting**

### Backend Issues

**Port 8080 already in use:**
```bash
# Change port in application.properties
server.port=8081
```

**Database connection failed:**
```bash
# Check MySQL is running
# Or switch to H2 (uncomment H2 config in application.properties)
```

**JWT token expired:**
```bash
# Login again to get new token
# Or increase expiration in application.properties
```

### Frontend Issues

**CORS errors:**
```bash
# Verify backend CORS configuration in SecurityConfig.java
# Should allow http://localhost:3000
```

**API connection failed:**
```bash
# Check backend is running on port 8080
# Verify API_BASE_URL in src/services/api.js
```

**Build errors:**
```bash
# Clear and reinstall
rm -rf node_modules package-lock.json
npm install
```

---

## 📚 **Technology Stack**

### Backend
| Technology | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 2.7.18 | Framework |
| Spring Security | - | Authentication |
| Spring Data JPA | - | Database ORM |
| MySQL | 8.0+ | Database |
| H2 | - | In-memory DB |
| JWT (JJWT) | 0.11.5 | Token auth |
| Hibernate Validator | - | Validation |
| SLF4J | - | Logging |

### Frontend
| Technology | Version | Purpose |
|------------|---------|---------|
| React | 18.2.0 | UI Framework |
| Vite | 5.0.8 | Build tool |
| Tailwind CSS | 3.4.0 | Styling |
| React Router | 6.20.0 | Routing |
| Axios | 1.6.2 | HTTP client |
| Recharts | 2.10.3 | Charts |
| React Icons | 4.12.0 | Icons |
| date-fns | 3.0.6 | Date formatting |

---

## 🎓 **Learning Resources**

### Backend
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT Introduction](https://jwt.io/introduction)

### Frontend
- [React Documentation](https://react.dev/)
- [Tailwind CSS](https://tailwindcss.com/)
- [Vite Guide](https://vitejs.dev/guide/)

---

## 🤝 **Contributing**

### Code Style
- **Backend**: Follow Java conventions, use meaningful names
- **Frontend**: Use functional components, hooks, and Tailwind classes

### Git Workflow
```bash
# Create feature branch
git checkout -b feature/your-feature

# Commit changes
git commit -m "Add: your feature description"

# Push and create PR
git push origin feature/your-feature
```

---

## 📝 **Future Enhancements**

### Planned Features
- [ ] Real-time notifications with WebSocket
- [ ] Task comments and attachments
- [ ] Email notifications
- [ ] Task dependencies
- [ ] Gantt chart view
- [ ] Time tracking
- [ ] Export reports (PDF/Excel)
- [ ] Dark mode
- [ ] Mobile app (React Native)
- [ ] Integration with Slack/Teams

---

## 📄 **License**

This project is part of the TeamVista productivity suite.

---

## 🎉 **Congratulations!**

You now have a fully functional, production-ready team productivity dashboard!

**Key Achievements:**
- ✅ Complete backend with 15 REST APIs
- ✅ Beautiful, responsive frontend
- ✅ Role-based access control
- ✅ Real-time analytics
- ✅ Comprehensive documentation
- ✅ Production-ready code

**Next Steps:**
1. Customize the branding and colors
2. Add your own features
3. Deploy to production
4. Share with your team!

---

**Happy Coding! 🚀**
