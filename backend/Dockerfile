# =========================
# Stage 1: Build React (Vite)
# =========================
FROM node:18-alpine AS frontend-build
WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm install --legacy-peer-deps || npm install

COPY frontend/ ./

ENV GENERATE_SOURCEMAP=false
ENV CI=false

RUN npm run build

# =========================
# Stage 2: Build Spring Boot
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS backend-build
WORKDIR /app

COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B

COPY backend/src ./src
RUN mvn clean package -DskipTests

# =========================
# Stage 3: Runtime
# =========================
FROM eclipse-temurin:17-jre
WORKDIR /app

# Install nginx AND netcat for MySQL wait
RUN apt-get update && \
    apt-get install -y nginx netcat-openbsd && \
    rm -rf /var/lib/apt/lists/*

# Copy backend JAR
COPY --from=backend-build /app/target/*.jar /app/backend.jar

# Copy Vite build output (dist folder)
COPY --from=frontend-build /app/frontend/dist /usr/share/nginx/html

# Nginx configuration
RUN printf 'server {\n\
    listen 80;\n\
    server_name _;\n\
\n\
    location / {\n\
        root /usr/share/nginx/html;\n\
        index index.html;\n\
        try_files $uri $uri/ /index.html;\n\
    }\n\
\n\
    location /api {\n\
        proxy_pass http://127.0.0.1:8080;\n\
        proxy_http_version 1.1;\n\
        proxy_set_header Host $host;\n\
        proxy_set_header X-Real-IP $remote_addr;\n\
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;\n\
    }\n\
}\n' > /etc/nginx/sites-enabled/default

# Startup script WITH MySQL wait
RUN printf '#!/bin/bash\n\
set -e\n\
echo "Waiting for MySQL..."\n\
MYSQL_HOST=$(echo $SPRING_DATASOURCE_URL | sed "s/.*\\/\\/\\([^:\\/]*\\).*/\\1/")\n\
for i in {1..30}; do\n\
    if nc -z $MYSQL_HOST 3306 2>/dev/null; then\n\
        echo "MySQL is ready!"\n\
        break\n\
    fi\n\
    echo "Attempt $i/30..."\n\
    sleep 2\n\
done\n\
echo "Starting nginx..."\n\
nginx\n\
echo "Starting Spring Boot..."\n\
exec java -jar /app/backend.jar\n' > /app/start.sh && chmod +x /app/start.sh

# Expose both ports
EXPOSE 80 8080

CMD ["/app/start.sh"]