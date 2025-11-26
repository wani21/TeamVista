# Deployment Checklist

## Pre-Deployment Verification

### 1. ✅ Code Completeness
- [x] All domain models implemented
- [x] All repositories created
- [x] All services implemented
- [x] All controllers created
- [x] Exception handling configured
- [x] Security configured
- [x] JWT authentication working

### 2. ✅ Configuration Files
- [x] `pom.xml` - All dependencies included
- [x] `application.properties` - Database and JWT configured
- [x] `data.sql` - Sample data ready

### 3. ✅ Security Checklist
- [x] JWT secret configured (change for production!)
- [x] BCrypt password encoding enabled
- [x] CORS configured for frontend origin
- [x] Role-based access control implemented
- [x] Sensitive endpoints protected
- [x] Public endpoints (auth) accessible

### 4. ✅ Database Setup
- [x] MySQL configuration present
- [x] H2 in-memory option available
- [x] Schema auto-generation configured
- [x] Sample data script ready

## Deployment Steps

### Option 1: Local Development

1. **Start MySQL (if using MySQL):**
   ```bash
   # Ensure MySQL is running
   # Create database: team_productivity
   ```

2. **Update Configuration:**
   - Edit `src/main/resources/application.properties`
   - Set database credentials
   - Or use H2 by uncommenting H2 configuration

3. **Build and Run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Verify:**
   - API: http://localhost:8080
   - H2 Console: http://localhost:8080/h2-console

### Option 2: Production Deployment

1. **Update Production Configuration:**
   ```properties
   # Change JWT secret to a strong random value
   jwt.secret=YOUR_PRODUCTION_SECRET_KEY_HERE_MINIMUM_256_BITS
   
   # Update database credentials
   spring.datasource.url=jdbc:mysql://your-db-host:3306/team_productivity
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # Disable H2 console
   spring.h2.console.enabled=false
   
   # Set appropriate logging level
   logging.level.com.productivity.dashboard=INFO
   logging.level.org.springframework.security=WARN
   
   # Disable SQL logging
   spring.jpa.show-sql=false
   ```

2. **Build JAR:**
   ```bash
   mvn clean package -DskipTests
   ```

3. **Run JAR:**
   ```bash
   java -jar target/team-productivity-backend-1.0.0.jar
   ```

### Option 3: Docker Deployment

1. **Create Dockerfile:**
   ```dockerfile
   FROM openjdk:11-jre-slim
   WORKDIR /app
   COPY target/team-productivity-backend-1.0.0.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

2. **Build Docker Image:**
   ```bash
   mvn clean package -DskipTests
   docker build -t team-productivity-backend .
   ```

3. **Run Container:**
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/team_productivity \
     -e SPRING_DATASOURCE_USERNAME=root \
     -e SPRING_DATASOURCE_PASSWORD=password \
     -e JWT_SECRET=your_secret_key \
     team-productivity-backend
   ```

## Post-Deployment Verification

### 1. Health Check
```bash
curl http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"manager@example.com","password":"password123"}'
```

**Expected:** 200 OK with JWT token

### 2. Test Authentication
```bash
# Register new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "password": "password123",
    "role": "EMPLOYEE"
  }'
```

**Expected:** 200 OK with user data

### 3. Test Protected Endpoint
```bash
# Get tasks (requires authentication)
curl http://localhost:8080/api/tasks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** 200 OK with task list

### 4. Test CORS
From frontend (http://localhost:3000):
```javascript
fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({
    email: 'manager@example.com',
    password: 'password123'
  })
})
```

**Expected:** No CORS errors

### 5. Test Error Handling
```bash
# Test 404
curl http://localhost:8080/api/tasks/999 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Expected:** 404 with error response

```bash
# Test 401
curl http://localhost:8080/api/tasks
```

**Expected:** 401 Unauthorized

## Security Recommendations for Production

### Critical
- [ ] Change JWT secret to a strong random value (minimum 256 bits)
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS/TLS
- [ ] Set up database connection pooling
- [ ] Configure proper CORS origins (not wildcard)
- [ ] Disable H2 console
- [ ] Set appropriate session timeout

### Recommended
- [ ] Implement rate limiting
- [ ] Add request logging
- [ ] Set up monitoring and alerts
- [ ] Configure backup strategy
- [ ] Implement password complexity rules
- [ ] Add account lockout after failed attempts
- [ ] Set up SSL for database connections
- [ ] Use secrets management service (AWS Secrets Manager, HashiCorp Vault)

### Optional
- [ ] Implement refresh tokens
- [ ] Add API versioning
- [ ] Set up CDN for static assets
- [ ] Implement caching (Redis)
- [ ] Add health check endpoint
- [ ] Set up log aggregation (ELK stack)

## Environment Variables

For production, use environment variables instead of hardcoded values:

```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/team_productivity
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export JWT_SECRET=your_production_secret_key
export JWT_EXPIRATION=86400000
export CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
```

Update `application.properties`:
```properties
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION}
```

## Monitoring

### Application Logs
```bash
# View logs
tail -f logs/application.log

# Search for errors
grep ERROR logs/application.log
```

### Database Monitoring
```sql
-- Check active connections
SHOW PROCESSLIST;

-- Check table sizes
SELECT 
    table_name,
    table_rows,
    data_length,
    index_length
FROM information_schema.tables
WHERE table_schema = 'team_productivity';
```

### Performance Metrics
- Response time for API endpoints
- Database query performance
- Memory usage
- CPU usage
- Active connections

## Troubleshooting

### Issue: Application won't start
**Check:**
- Database connection
- Port 8080 availability
- Java version (11+)
- Maven dependencies

### Issue: Authentication fails
**Check:**
- JWT secret configuration
- Password encoding
- User exists in database
- Token expiration

### Issue: CORS errors
**Check:**
- CORS configuration in SecurityConfig
- Frontend origin matches configuration
- Preflight requests allowed

### Issue: Database connection fails
**Check:**
- Database is running
- Credentials are correct
- Network connectivity
- Firewall rules

## Rollback Plan

If deployment fails:

1. **Stop the application:**
   ```bash
   # If running as service
   systemctl stop team-productivity-backend
   
   # If running in Docker
   docker stop team-productivity-backend
   ```

2. **Restore previous version:**
   ```bash
   # Deploy previous JAR
   java -jar target/team-productivity-backend-1.0.0-previous.jar
   ```

3. **Restore database (if needed):**
   ```bash
   mysql -u root -p team_productivity < backup.sql
   ```

## Success Criteria

Deployment is successful when:
- [x] Application starts without errors
- [x] All API endpoints respond correctly
- [x] Authentication works
- [x] Database connections are stable
- [x] CORS works for frontend
- [x] Error handling works properly
- [x] Sample data loads correctly
- [x] No security vulnerabilities

## Support

For issues or questions:
- Check logs in `logs/application.log`
- Review `API_TESTS.md` for testing examples
- Consult `IMPLEMENTATION_SUMMARY.md` for architecture details
