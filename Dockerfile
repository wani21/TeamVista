# Stage 1: Build Frontend
FROM node:18-alpine AS frontend-build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm install
COPY frontend/ ./
RUN npm run build

# Stage 2: Build Backend
FROM maven:3.9.6-eclipse-temurin-17 AS backend-build
WORKDIR /app
COPY backend/pom.xml ./
RUN mvn dependency:go-offline -B
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Stage 3: Runtime
FROM ubuntu:22.04
ENV DEBIAN_FRONTEND=noninteractive

# Install Java, MySQL, Nginx
RUN apt-get update && \
    apt-get install -y openjdk-17-jre mysql-server nginx && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Copy built files
COPY --from=backend-build /app/target/*.jar /app/app.jar
COPY --from=frontend-build /app/dist /usr/share/nginx/html

# Nginx config
RUN echo 'server {\n\
    listen 3000;\n\
    location / {\n\
        root /usr/share/nginx/html;\n\
        index index.html;\n\
        try_files $uri /index.html;\n\
    }\n\
    location /api {\n\
        proxy_pass http://localhost:8080;\n\
    }\n\
}' > /etc/nginx/sites-enabled/default

# Startup script
RUN echo '#!/bin/bash\n\
service mysql start\n\
mysql -e "CREATE DATABASE IF NOT EXISTS ${MYSQL_DATABASE:-team_productivity};"\n\
mysql -e "ALTER USER root@localhost IDENTIFIED BY \"${MYSQL_ROOT_PASSWORD:-root123}\";"\n\
nginx\n\
java -Dspring.datasource.url=jdbc:mysql://localhost:3306/${MYSQL_DATABASE:-team_productivity} \
     -Dspring.datasource.username=root \
     -Dspring.datasource.password=${MYSQL_ROOT_PASSWORD:-root123} \
     -jar /app/app.jar' > /start.sh && chmod +x /start.sh

EXPOSE 3000 8080 3306

CMD ["/start.sh"]