# Docker Deployment Guide - Nauczycielka

This guide explains how to run Nauczycielka using Docker and Docker Compose.

## Table of Contents

1. [Requirements](#requirements)
2. [Quick Start with Docker Compose](#quick-start-with-docker-compose)
3. [Manual Docker Build & Run](#manual-docker-build--run)
4. [Environment Variables](#environment-variables)
5. [Troubleshooting](#troubleshooting)
6. [Production Deployment](#production-deployment)

---

## Requirements

- **Docker**: 20.10+ ([Download](https://docker.com))
- **Docker Compose**: 2.0+ (usually included with Docker Desktop)
- **Available Ports**: 
  - 8080 (Nauczycielka App)
  - 11434 (Ollama)
  - 5432 (PostgreSQL)

Verify installation:
```bash
docker --version
docker-compose --version
```

---

## Quick Start with Docker Compose

The fastest way to get everything running locally!

### 1. Build Application JAR
```bash
cd /Users/zuahmed/Project/Nauczycielka

# Build the application
mvn clean package

# JAR will be in target/nauczycielka-1.0.0-SNAPSHOT.jar
```

### 2. Start All Services
```bash
# Start Ollama, PostgreSQL, and Nauczycielka app
docker-compose up -d

# View logs in real-time
docker-compose logs -f nauczycielka-app

# Check service status
docker-compose ps
```

### 3. Verify Services Are Running

```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check Ollama
curl http://localhost:11434/api/tags

# Check PostgreSQL
psql -h localhost -U nauczycielka_user -d nauczycielka -c "SELECT NOW();"
```

### 4. Access the Application
- **API**: `http://localhost:8080/api/v1`
- **Ollama**: `http://localhost:11434`
- **PostgreSQL**: `localhost:5432` (user: nauczycielka_user)

### 5. Download Ollama Model (First Time Only)
```bash
# Pull Qwen2 model (inside container)
docker exec nauczycielka-ollama ollama pull qwen2

# List available models
docker exec nauczycielka-ollama ollama list
```

### 6. Test API
```bash
# Get all courses
curl http://localhost:8080/api/v1/courses | jq

# View application logs
docker-compose logs nauczycielka-app --follow
```

### 7. Stop All Services
```bash
# Stop and remove containers
docker-compose down

# Stop but keep data (volumes persist)
docker-compose down

# Remove all volumes (WARNING: deletes data)
docker-compose down -v
```

---

## Manual Docker Build & Run

For more control, build and run containers manually.

### 1. Build Docker Image
```bash
cd /Users/zuahmed/Project/Nauczycielka

# Build JAR first
mvn clean package

# Build Docker image
docker build -t nauczycielka:latest .

# Verify image
docker images | grep nauczycielka
```

### 2. Run PostgreSQL Container
```bash
docker run -d \
  --name nauczycielka-postgres \
  -e POSTGRES_DB=nauczycielka \
  -e POSTGRES_USER=nauczycielka_user \
  -e POSTGRES_PASSWORD=nauczycielka_password \
  -p 5432:5432 \
  -v postgres_data:/var/lib/postgresql/data \
  postgres:16-alpine

# Verify it's running
docker ps | grep postgres
```

### 3. Run Ollama Container
```bash
docker run -d \
  --name nauczycielka-ollama \
  -e OLLAMA_HOST=0.0.0.0:11434 \
  -p 11434:11434 \
  -v ollama_data:/root/.ollama \
  ollama/ollama:latest

# Wait for startup
sleep 10

# Pull model
docker exec nauczycielka-ollama ollama pull qwen2

# Verify
curl http://localhost:11434/api/tags
```

### 4. Run Application Container
```bash
docker run -d \
  --name nauczycielka-app \
  -e SPRING_APPLICATION_NAME=nauczycielka \
  -e OLLAMA_BASE_URL=http://nauczycielka-ollama:11434 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://nauczycielka-postgres:5432/nauczycielka \
  -e SPRING_DATASOURCE_USERNAME=nauczycielka_user \
  -e SPRING_DATASOURCE_PASSWORD=nauczycielka_password \
  -p 8080:8080 \
  --link nauczycielka-postgres \
  --link nauczycielka-ollama \
  nauczycielka:latest

# View logs
docker logs -f nauczycielka-app
```

### 5. Test Application
```bash
# Wait for startup (20-30 seconds)
sleep 30

# Test health
curl http://localhost:8080/actuator/health

# Test API
curl http://localhost:8080/api/v1/courses
```

---

## Environment Variables

### Nauczycielka Application

```yaml
# Ollama Configuration
OLLAMA_BASE_URL=http://ollama:11434              # Ollama server
OLLAMA_MODEL=qwen2                               # Model name
OLLAMA_TIMEOUT=300000                            # Timeout (ms)

# Database Configuration
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/nauczycielka
SPRING_DATASOURCE_USERNAME=nauczycielka_user
SPRING_DATASOURCE_PASSWORD=nauczycielka_password
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

# Hibernate/JPA
SPRING_JPA_DATABASE=postgresql
SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop        # create-drop, validate, update
SPRING_JPA_SHOW_SQL=false

# Logging
LOGGING_LEVEL_ROOT=INFO
LOGGING_LEVEL_COM_NAUCZYCIELKA=DEBUG

# Server
SERVER_PORT=8080
SERVER_SERVLET_CONTEXT_PATH=/

# Actuator
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
```

### PostgreSQL

```yaml
POSTGRES_DB=nauczycielka
POSTGRES_USER=nauczycielka_user
POSTGRES_PASSWORD=nauczycielka_password
POSTGRES_INITDB_ARGS=--encoding=UTF8
```

### Ollama

```yaml
OLLAMA_HOST=0.0.0.0:11434
OLLAMA_MODELS=/root/.ollama/models
```

---

## Troubleshooting

### Container Won't Start

```bash
# Check logs
docker logs nauczycielka-app

# Check if port is in use
lsof -i :8080
lsof -i :11434
lsof -i :5432

# Kill process using port
kill -9 <PID>
```

### Connection Refused: Ollama

```bash
# Verify Ollama is running
docker ps | grep ollama

# Check health
curl http://localhost:11434/api/tags

# Verify model is pulled
docker exec nauczycielka-ollama ollama list
```

### PostgreSQL Connection Error

```bash
# Verify PostgreSQL is running
docker ps | grep postgres

# Test connection
docker exec nauczycielka-postgres psql -U nauczycielka_user -d nauczycielka -c "SELECT NOW();"

# Check logs
docker logs nauczycielka-postgres
```

### Out of Memory

```bash
# Increase Docker memory in Docker Desktop Settings
# Or limit container memory:
docker run -m 2GB ...

# For Ollama specifically:
# Qwen2 requires ~8GB, Mistral ~5GB
# Use smaller model or increase Docker memory
```

### Rebuild After Changes

```bash
# Stop containers
docker-compose down

# Rebuild image
mvn clean package
docker build -t nauczycielka:latest .

# Start again
docker-compose up -d
```

---

## Production Deployment

### Multi-Stage Build (Optimized Image)

Create `.dockerignore`:
```
target/
.git
.gitignore
.idea
.mvn
README.md
src/test
pom.xml.bak
```

Update Dockerfile for multi-stage build:
```dockerfile
# Build stage
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/nauczycielka-1.0.0-SNAPSHOT.jar .
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "nauczycielka.jar"]
```

### Production Configuration

Update `application.yaml` for production:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://postgres-prod:5432/nauczycielka
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
  jpa:
    hibernate:
      ddl-auto: validate          # Never use create-drop in production
    properties:
      hibernate:
        format_sql: false
        show_sql: false

logging:
  level:
    root: WARN
    com.nauczycielka: INFO
  file: /var/log/nauczycielka/app.log

management:
  endpoints:
    web:
      exposure:
        include: health,metrics    # Limit exposed endpoints
```

### Kubernetes Deployment

Example deployment file (`k8s-deployment.yaml`):
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nauczycielka-app
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nauczycielka
  template:
    metadata:
      labels:
        app: nauczycielka
    spec:
      containers:
      - name: nauczycielka
        image: nauczycielka:latest
        ports:
        - containerPort: 8080
        env:
        - name: OLLAMA_BASE_URL
          value: http://ollama-service:11434
        - name: SPRING_DATASOURCE_URL
          value: jdbc:postgresql://postgres-service:5432/nauczycielka
        resources:
          requests:
            memory: "512Mi"
            cpu: "500m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

Deploy to Kubernetes:
```bash
kubectl apply -f k8s-deployment.yaml
kubectl get pods
kubectl logs deployment/nauczycielka-app
```

### Push to Docker Registry

```bash
# Tag image
docker tag nauczycielka:latest myregistry/nauczycielka:1.0.0

# Login to registry
docker login myregistry

# Push image
docker push myregistry/nauczycielka:1.0.0

# Pull and run
docker run -d \
  -p 8080:8080 \
  myregistry/nauczycielka:1.0.0
```

---

## Monitoring

### Docker Container Monitoring

```bash
# View resource usage
docker stats nauczycielka-app

# View container logs
docker logs -f nauczycielka-app --tail 100

# Inspect container
docker inspect nauczycielka-app
```

### Application Metrics

```bash
# Get health status
curl http://localhost:8080/actuator/health | jq

# View metrics
curl http://localhost:8080/actuator/metrics | jq

# Specific metric
curl http://localhost:8080/actuator/metrics/jvm.memory.used | jq
```

---

## Cleanup

```bash
# Stop all containers
docker-compose down

# Remove images
docker rmi nauczycielka:latest
docker rmi postgres:16-alpine
docker rmi ollama/ollama:latest

# Remove volumes (WARNING: deletes data)
docker volume rm nauczycielka_postgres_data
docker volume rm nauczycielka_ollama_data

# Clean up all unused Docker resources
docker system prune -a --volumes
```

---

## Summary

**Docker Compose** (Recommended for development):
```bash
mvn clean package && docker-compose up -d
```

**Manual Docker** (For custom setups):
- Build JAR with Maven
- Build image with Dockerfile
- Run containers with proper linking
- Configure environment variables

**Production**:
- Use multi-stage builds
- Use external PostgreSQL service
- Configure proper logging
- Set up monitoring
- Use Kubernetes for orchestration

---

**Happy Deploying! 🚀**
