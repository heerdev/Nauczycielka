# Quick Start Guide - Nauczycielka Project Setup

This guide will help you get the Nauczycielka application up and running locally.

## 1. Prerequisites Checklist

- ✅ Java 21+ installed (`java -version`)
- ✅ Maven 3.9+ installed (`mvn -version`)
- ✅ Git installed
- ✅ Ollama installed (for AI features)

## 2. Install & Configure Ollama

### Option A: Install from Official Source
```bash
# Download and install from https://ollama.ai
# For macOS:
brew install ollama

# For Linux/Windows, download installer from ollama.ai
```

### Option B: Docker (if Ollama not available locally)
```bash
docker run -d -p 11434:11434 ollama/ollama:latest
```

### Download AI Model
```bash
# Start Ollama (if not already running)
ollama serve

# In another terminal, pull the model
ollama pull qwen2
# or alternative efficient models:
# ollama pull mistral
# ollama pull neural-chat
# ollama pull llama2
```

**Verify Ollama is running:**
```bash
curl http://localhost:11434/api/tags
# Should return list of models
```

## 3. Build & Run the Application

### Step 1: Navigate to Project
```bash
cd /Users/zuahmed/Project/Nauczycielka
```

### Step 2: Build with Maven
```bash
mvn clean install
```

**Note**: First build downloads dependencies (~500MB), may take 3-5 minutes.

### Step 3: Run Application
```bash
mvn spring-boot:run
```

or

```bash
java -jar target/nauczycielka-1.0.0-SNAPSHOT.jar
```

**Expected Output:**
```
2026-04-23 10:15:30 - Starting NauczycielkaApplication v1.0.0-SNAPSHOT
...
2026-04-23 10:15:45 - Tomcat started on port(s): 8080
2026-04-23 10:15:45 - Started NauczycielkaApplication in 12.345 seconds
```

### Step 4: Access the Application
- **API Base URL**: `http://localhost:8080/api/v1`
- **H2 Console**: `http://localhost:8080/h2-console`
- **Health Check**: `http://localhost:8080/actuator/health`

## 4. Verify Setup with Sample Request

### Check Application Health
```bash
curl http://localhost:8080/actuator/health | json_pp
```

**Expected Response:**
```json
{
  "status": "UP"
}
```

### Get All Courses (should have sample data)
```bash
curl http://localhost:8080/api/v1/courses | json_pp
```

## 5. First API Workflow

### 1. List Existing Users/Courses
```bash
# Get all courses (includes sample data)
curl http://localhost:8080/api/v1/courses

# Get instructor's courses
curl "http://localhost:8080/api/v1/courses/instructor/1"
```

### 2. Create a New Course
```bash
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Advanced Python",
    "description": "Deep dive into Python",
    "instructor_id": 1
  }'
```

### 3. Enroll a Student
```bash
curl -X POST http://localhost:8080/api/v1/courses/2/enrollments \
  -H "Content-Type: application/json" \
  -d '{"student_id": 2}'
```

### 4. Start a Live Session
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Lesson 1: Functions",
    "description": "Learn how to write functions"
  }'
```

### 5. Add Transcripts (During Lecture)
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions/1/transcripts \
  -H "Content-Type: application/json" \
  -d '{
    "content": "A function is a reusable block of code. Functions help organize code and reduce repetition. In Python, we define functions using the def keyword. Functions can take parameters and return values.",
    "speaker": "Dr. John Smith"
  }'
```

### 6. End Session & Generate AI Content
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions/1/end
```

**Note**: This triggers async AI processing. Check logs:
```
Generated X vocabulary items for session 1
Generated summary for session 1
```

### 7. View Generated Content
```bash
# Get vocabulary (AI-extracted terms)
curl http://localhost:8080/api/v1/courses/1/vocabulary

# Get lecture summary (AI-generated)
curl http://localhost:8080/api/v1/courses/1/summary
```

## 6. Database Access (H2 Console)

**URL**: `http://localhost:8080/h2-console`

**Connection Details:**
- **JDBC URL**: `jdbc:h2:mem:nauczycielka`
- **Username**: `sa`
- **Password**: (leave blank)

**Example Queries:**
```sql
-- View all courses
SELECT * FROM courses;

-- View all students
SELECT * FROM users WHERE role = 'STUDENT';

-- View vocabulary items
SELECT * FROM vocabulary_items;

-- View course summaries
SELECT * FROM course_summaries;
```

## 7. Monitoring & Logs

### View Real-time Logs
```bash
# While application is running, view logs:
tail -f target/nauczycielka.log

# or grep for specific errors:
grep ERROR target/nauczycielka.log
```

### Actuator Endpoints
```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

## 8. Troubleshooting

### ❌ Ollama Connection Error
```
Error: Unable to connect to http://localhost:11434
```

**Solutions:**
1. Verify Ollama is running: `ollama serve`
2. Check port is accessible: `curl http://localhost:11434/api/tags`
3. Verify model is available: `ollama list`

### ❌ Port 8080 Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>

# Or use different port:
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

### ❌ Out of Memory
```bash
# Increase JVM memory:
export MAVEN_OPTS="-Xmx1024m -Xms512m"
mvn spring-boot:run

# or for JAR:
java -Xmx1024m -Xms512m -jar target/nauczycielka-1.0.0-SNAPSHOT.jar
```

### ❌ Model Timeout
If Qwen2 is too slow on your machine:
```bash
# Use a faster model
ollama pull mistral
# Then update application.yaml: ollama.model: mistral
```

## 9. Development Tips

### Reload Changes Without Rebuild
Add Spring Boot DevTools (optional):
```xml
<!-- Add to pom.xml -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-devtools</artifactId>
  <scope>runtime</scope>
</dependency>
```

### Run in Debug Mode
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"
```

### Run Tests
```bash
mvn test

# Run specific test
mvn test -Dtest=CourseServiceTest

# Skip tests during build
mvn clean install -DskipTests
```

## 10. Next Steps

- 📖 Read the [README.md](README.md) for full API documentation
- 🔐 Implement JWT authentication (see Future Enhancements)
- 🚀 Deploy to production (Docker/Kubernetes ready)
- 📊 Add custom metrics and monitoring
- 🧪 Write integration tests

## 11. Support & Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Ollama Docs**: https://ollama.ai
- **Jakarta Persistence**: https://jakarta.ee/
- **Project Issues**: Check GitHub repository

---

**Happy Learning! 🎓**
