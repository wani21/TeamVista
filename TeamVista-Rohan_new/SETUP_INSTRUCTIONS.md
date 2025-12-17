# TeamVista - Setup Instructions

## 🎯 **Complete Setup Guide**

Follow these steps to get TeamVista up and running on your machine.

---

## ✅ **Prerequisites Checklist**

Before starting, ensure you have:

- [ ] **Java 11 or higher** installed
  ```bash
  java -version
  # Should show: java version "11.0.x" or higher
  ```

- [ ] **Maven 3.6+** installed
  ```bash
  mvn -version
  # Should show: Apache Maven 3.6.x or higher
  ```

- [ ] **Node.js 16+** and npm installed
  ```bash
  node -version
  # Should show: v16.x.x or higher
  
  npm -version
  # Should show: 8.x.x or higher
  ```

- [ ] **MySQL 8.0+** installed and running (or use H2 in-memory database)
  ```bash
  mysql --version
  # Should show: mysql Ver 8.0.x
  ```

---

## 📥 **Step 1: Clone/Download the Project**

```bash
# If using Git
git clone <repository-url>
cd TeamVista

# Or extract the ZIP file and navigate to the folder
```

---

## 🗄️ **Step 2: Database Setup**

### Option A: Using MySQL (Recommended for Production)

1. **Start MySQL Server**
   ```bash
   # Windows: Start MySQL service from Services
   # Mac: brew services start mysql
   # Linux: sudo systemctl start mysql
   ```

2. **Create Database**
   ```bash
   mysql -u root -p
   ```
   
   Then run:
   ```sql
   CREATE DATABASE team_productivity;
   EXIT;
   ```

3. **Update Configuration**
   
   Edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/team_productivity
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### Option B: Using H2 (Quick Start/Development)

1. **Enable H2 Configuration**
   
   Edit `src/main/resources/application.properties`:
   
   Comment out MySQL config and uncomment H2 config:
   ```properties
   # MySQL (comment these)
   #spring.datasource.url=jdbc:mysql://localhost:3306/team_productivity
   #spring.datasource.username=root
   #spring.datasource.password=Engineering!123
   
   # H2 (uncomment these)
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driverClassName=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=password
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
   ```

---

## 🔧 **Step 3: Backend Setup**

1. **Navigate to project root**
   ```bash
   cd TeamVista
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```
   
   This will:
   - Download all dependencies
   - Compile the code
   - Run tests (if any)
   - Create a JAR file

3. **Verify build success**
   
   You should see:
   ```
   [INFO] BUILD SUCCESS
   [INFO] Total time: XX s
   ```

4. **Start the backend**
   ```bash
   mvn spring-boot:run
   ```
   
   Wait for:
   ```
   Started TeamProductivityApplication in X.XXX seconds
   ```

5. **Verify backend is running**
   
   Open browser and go to:
   ```
   http://localhost:8080/api/auth/login
   ```
   
   You should see a JSON response (even if it's an error, it means the server is running)

---

## 🎨 **Step 4: Frontend Setup**

1. **Open a NEW terminal** (keep backend running in the first terminal)

2. **Navigate to frontend folder**
   ```bash
   cd frontend
   ```

3. **Install dependencies**
   ```bash
   npm install
   ```
   
   This will download all required packages (may take 2-3 minutes)

4. **Start the frontend**
   ```bash
   npm run dev
   ```
   
   You should see:
   ```
   VITE v5.0.8  ready in XXX ms
   
   ➜  Local:   http://localhost:3000/
   ➜  Network: use --host to expose
   ```

5. **Open the application**
   
   Open your browser and go to:
   ```
   http://localhost:3000
   ```

---

## 🎉 **Step 5: First Login**

1. **You should see the beautiful login page**

2. **Use demo credentials:**
   
   **Manager Account:**
   - Email: `manager@example.com`
   - Password: `password123`
   
   **Employee Account:**
   - Email: `alice@example.com`
   - Password: `password123`

3. **After login, you'll see:**
   - Dashboard with statistics
   - Sidebar navigation
   - Your user profile

---

## ✨ **Step 6: Explore Features**

### As Manager:
1. **Dashboard**: View team analytics and charts
2. **Tasks**: 
   - Click "Create Task" to add a new task
   - Edit any task
   - Delete tasks
   - Complete tasks
   - Search and filter

### As Employee:
1. **Dashboard**: View team performance
2. **Tasks**:
   - View assigned tasks
   - Update task status
   - Complete your tasks
   - Search and filter

---

## 🚀 **Quick Start Script (Windows)**

For future startups, just run:
```bash
start-dev.bat
```

This will:
1. Start the backend automatically
2. Start the frontend automatically
3. Open the app in your browser

---

## 🔍 **Verification Checklist**

After setup, verify everything works:

- [ ] Backend running on http://localhost:8080
- [ ] Frontend running on http://localhost:3000
- [ ] Can login with demo credentials
- [ ] Dashboard shows statistics
- [ ] Can create a task (as Manager)
- [ ] Can view tasks
- [ ] Can update task status
- [ ] Charts are displaying correctly

---

## 🐛 **Common Issues & Solutions**

### Issue 1: "Port 8080 already in use"

**Solution:**
```bash
# Find and kill the process using port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Mac/Linux:
lsof -ti:8080 | xargs kill -9
```

Or change the port in `application.properties`:
```properties
server.port=8081
```

### Issue 2: "Cannot connect to database"

**Solution:**
- Verify MySQL is running
- Check username/password in `application.properties`
- Or switch to H2 database (see Step 2, Option B)

### Issue 3: "npm install fails"

**Solution:**
```bash
# Clear npm cache
npm cache clean --force

# Delete node_modules and try again
rm -rf node_modules package-lock.json
npm install
```

### Issue 4: "CORS error in browser"

**Solution:**
- Verify backend is running
- Check `SecurityConfig.java` has CORS configured for `http://localhost:3000`
- Clear browser cache and reload

### Issue 5: "JWT token expired"

**Solution:**
- Just login again
- Token expires after 24 hours by default
- Can be changed in `application.properties`:
  ```properties
  jwt.expiration=86400000  # 24 hours in milliseconds
  ```

### Issue 6: "Frontend shows blank page"

**Solution:**
```bash
# Check browser console for errors
# Verify API_BASE_URL in frontend/src/services/api.js
# Make sure backend is running
# Try clearing browser cache
```

---

## 📊 **Verify Installation**

Run these commands to verify everything is set up correctly:

```bash
# Check Java
java -version

# Check Maven
mvn -version

# Check Node.js
node -version

# Check npm
npm -version

# Check MySQL (if using)
mysql --version

# Test backend (in project root)
mvn clean test

# Test frontend (in frontend folder)
cd frontend
npm run build
```

---

## 🎓 **Next Steps**

Now that everything is set up:

1. **Explore the Code**
   - Backend: `src/main/java/com/productivity/dashboard/`
   - Frontend: `frontend/src/`

2. **Read Documentation**
   - `COMPLETE_PROJECT_GUIDE.md` - Full project overview
   - `COMPLETE_API_DOCUMENTATION.md` - API reference
   - `frontend/README.md` - Frontend details

3. **Customize**
   - Change colors in `frontend/tailwind.config.js`
   - Update branding
   - Add your own features

4. **Deploy**
   - Follow deployment guide in `COMPLETE_PROJECT_GUIDE.md`

---

## 📞 **Need Help?**

If you encounter issues:

1. **Check the logs**
   - Backend: Look at the terminal where backend is running
   - Frontend: Check browser console (F12)

2. **Review documentation**
   - All documentation files in the project root
   - Comments in the code

3. **Common solutions**
   - Restart both servers
   - Clear browser cache
   - Check all prerequisites are installed
   - Verify database is running

---

## 🎉 **Success!**

If you can:
- ✅ Login to the application
- ✅ See the dashboard
- ✅ Create and manage tasks
- ✅ View charts and statistics

**Congratulations! Your TeamVista installation is complete!** 🚀

---

## 📝 **Quick Reference**

### Start Development
```bash
# Terminal 1 - Backend
mvn spring-boot:run

# Terminal 2 - Frontend
cd frontend
npm run dev
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console (if H2 enabled)

### Demo Credentials
- **Manager**: manager@example.com / password123
- **Employee**: alice@example.com / password123

---

**Happy Coding! 🎊**
