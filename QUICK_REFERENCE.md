# Nauczycielka - Quick Reference Guide

A handy one-page reference for getting started and common tasks.

## 🚀 Quick Start (5 minutes)

```bash
# 1. Ensure Ollama is running in another terminal
ollama serve

# 2. Navigate to project
cd /Users/zuahmed/Project/Nauczycielka

# 3. Build the project
mvn clean install

# 4. Run the application
mvn spring-boot:run

# 5. Test it works
curl http://localhost:8080/api/v1/courses
```

**Application is ready at**: `http://localhost:8080`

---

## 📚 Documentation Guide

| Need | File | Quick Link |
|------|------|-----------|
| **Setup Instructions** | SETUP.md | Start here! |
| **API Documentation** | README.md | All endpoints |
| **Docker Deployment** | DOCKER_GUIDE.md | Container setup |
| **Project Overview** | IMPLEMENTATION_SUMMARY.md | Architecture |
| **File Listing** | PROJECT_FILES.md | All files |
| **API Testing** | Nauczycielka.postman_collection.json | Import to Postman |

---

## 🔑 Key URLs

| Service | URL | Purpose |
|---------|-----|---------|
| Application | `http://localhost:8080` | Main app |
| API Base | `http://localhost:8080/api/v1` | REST API |
| Health | `http://localhost:8080/actuator/health` | Status check |
| Metrics | `http://localhost:8080/actuator/metrics` | Performance |
| H2 Console | `http://localhost:8080/h2-console` | Database UI |
| Ollama | `http://localhost:11434` | AI model service |

---

## 💾 Database Access

**H2 Console** (Development)
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:nauczycielka
Username: sa
Password: (blank)
```

**PostgreSQL** (Production)
```
Host: localhost
Port: 5432
Database: nauczycielka
Username: nauczycielka_user
Password: nauczycielka_password
```

**View Sample Data:**
```sql
SELECT * FROM courses;
SELECT * FROM users;
SELECT * FROM vocabulary_items;
SELECT * FROM course_summaries;
```

---

## 🌐 Common API Requests

### Get All Courses
```bash
curl http://localhost:8080/api/v1/courses | jq
```

### Create a Course
```bash
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Python 101",
    "description": "Learn Python",
    "instructor_id": 1
  }' | jq
```

### Start a Session
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Lesson 1",
    "description": "Learn basics"
  }' | jq
```

### Add Transcript
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions/1/transcripts \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Today we learn variables...",
    "speaker": "Dr. Smith"
  }' | jq
```

### End Session (Triggers AI)
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions/1/end
```

### Get Generated Vocabulary
```bash
curl http://localhost:8080/api/v1/courses/1/vocabulary | jq
```

### Get Generated Summary
```bash
curl http://localhost:8080/api/v1/courses/1/summary | jq
```

---

## 🐳 Docker Commands

### Quick Start with Compose
```bash
mvn clean package && docker-compose up -d
```

### View Logs
```bash
docker-compose logs -f nauczycielka-app
```

### Stop All Services
```bash
docker-compose down
```

### Manual Build
```bash
mvn clean package
docker build -t nauczycielka:latest .
docker run -p 8080:8080 nauczycielka:latest
```

---

## 🛠️ Common Tasks

### Rebuild After Code Changes
```bash
mvn clean compile
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

### Check Maven Dependencies
```bash
mvn dependency:tree
```

### Build Production JAR
```bash
mvn clean package -DskipTests
java -jar target/nauczycielka-1.0.0-SNAPSHOT.jar
```

### View Application Logs
```bash
# In application console (last 50 lines)
tail -50 target/application.log

# Filter for errors
grep ERROR target/application.log
```

### Change Logging Level
Edit `application.yaml`:
```yaml
logging:
  level:
    com.nauczycielka: DEBUG  # Change from INFO to DEBUG
```

---

## 🔍 Troubleshooting

### ❌ Ollama Connection Error
```
Solution: Verify Ollama is running
ollama serve
curl http://localhost:11434/api/tags
```

### ❌ Port Already in Use
```bash
# Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

### ❌ Model Not Found
```bash
# Pull the model
ollama pull qwen2

# List available models
ollama list
```

### ❌ Application Won't Start
```bash
# Check logs
mvn spring-boot:run 2>&1 | head -50

# Increase memory
export MAVEN_OPTS="-Xmx1024m"
mvn spring-boot:run
```

### ❌ Database Locked (H2)
```bash
# Delete H2 database and restart
rm *.db
mvn spring-boot:run
```

---

## 📊 Project Statistics

| Metric | Value |
|--------|-------|
| **Java Classes** | 43 |
| **REST Endpoints** | 20+ |
| **Database Tables** | 7 |
| **Configuration Files** | 4 |
| **Documentation Files** | 7 |
| **Total Lines of Code** | 6,000+ |
| **Build Time** | ~30 seconds |
| **Startup Time** | ~15 seconds |

---

## 🔐 Default Sample Data

### Users
```
Instructor:
  Email: instructor@example.com
  Name: Dr. John Smith
  Role: INSTRUCTOR

Students:
  Email: student1@example.com
  Email: student2@example.com
  Email: student3@example.com
  Role: STUDENT
```

### Course
```
Title: Python Fundamentals
Description: Learn Python basics
Instructor: Dr. John Smith
```

---

## 📋 Checklist for First Run

- [ ] Java 21+ installed
- [ ] Maven 3.9+ installed
- [ ] Ollama installed and running
- [ ] Qwen2 model pulled
- [ ] Project cloned
- [ ] `mvn clean install` completed
- [ ] Application started with `mvn spring-boot:run`
- [ ] `curl http://localhost:8080/api/v1/courses` returns data
- [ ] Postman collection imported
- [ ] First API test successful

---

## 📞 Support Resources

- **Spring Boot Docs**: https://spring.io/projects/spring-boot
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
- **Ollama**: https://ollama.ai
- **Jakarta**: https://jakarta.ee/
- **Maven**: https://maven.apache.org

---

## 🎯 Next Steps

1. ✅ **Run the application** (see Quick Start)
2. 📖 **Read README.md** for full API documentation
3. 🧪 **Import Postman collection** for API testing
4. 🐳 **Try Docker deployment** (see DOCKER_GUIDE.md)
5. 🔐 **Add authentication** (JWT - Future enhancement)
6. 📈 **Deploy to production** (See DOCKER_GUIDE.md)

---

## 📝 File Reference

```
/Users/zuahmed/Project/Nauczycielka/
├── README.md                    ← Full API docs
├── SETUP.md                     ← Installation guide
├── DOCKER_GUIDE.md              ← Docker deployment
├── IMPLEMENTATION_SUMMARY.md    ← Architecture overview
├── PROJECT_FILES.md             ← File listing
├── pom.xml                      ← Build configuration
├── Dockerfile                   ← Docker image
├── docker-compose.yml           ← Docker Compose
└── src/main/
    ├── java/com/nauczycielka/   ← Source code (43 classes)
    └── resources/               ← Configuration
```

---

## 💡 Tips & Tricks

### Pretty Print JSON
```bash
curl http://localhost:8080/api/v1/courses | jq
```

### Save Response to File
```bash
curl http://localhost:8080/api/v1/courses > courses.json
```

### With Authentication (Future)
```bash
curl -H "Authorization: Bearer TOKEN" http://localhost:8080/api/v1/courses
```

### Measure Response Time
```bash
curl -w "Response time: %{time_total}s\n" http://localhost:8080/api/v1/courses
```

### Watch Logs in Real-Time
```bash
tail -f application.log | grep ERROR
```

---

## 🎓 Learning Path

1. **Beginner**: SETUP.md → README.md → Run sample requests
2. **Intermediate**: Explore code → Modify endpoints → Docker deployment
3. **Advanced**: CI/CD setup → Kubernetes → Production hardening

---

**Last Updated**: April 23, 2026
**Version**: 1.0.0
**Status**: ✅ Ready to Use

🚀 **Happy Coding!**
