# Nauczycielka - Live Tutorial Platform with AI-Powered Learning Tools

A modern Java Spring Boot application for managing online live video tutorials with AI-powered vocabulary extraction and lecture summarization using Ollama's Qwen model.

## Features

✨ **Core Features:**
- **Live Tutorial Sessions**: Real-time capture of lecture content through WebSocket/API
- **AI-Powered Vocabulary Extraction**: Automatically extract key terms from lectures using Ollama
- **Lecture Summarization**: Generate concise summaries of lectures at the end of each session
- **Student Enrollment**: Manage student enrollment in courses
- **Shared Learning Resources**: Vocabulary lists and summaries shared across all students in a course
- **Persistent Storage**: All content stored in database for later review

🤖 **AI Integration:**
- Local Ollama integration with Qwen 3.5 model
- Extensible architecture for future Claude API integration (production)
- Real-time and batch processing modes
- Asynchronous AI task processing

## Technology Stack

- **Framework**: Spring Boot 3.4.0
- **Language**: Java 21
- **ORM**: Spring Data JPA with Hibernate
- **Database**: H2 (development), PostgreSQL (production-ready)
- **Build**: Maven 3.9+
- **AI Integration**: Ollama REST API
- **Security**: Spring Security
- **Async Processing**: Spring @Async with ThreadPoolTaskExecutor
- **API Style**: RESTful with RFC 7807 Problem Details
- **Validation**: Jakarta Bean Validation 3.0+

## Prerequisites

### System Requirements
- Java 21 or later
- Maven 3.9 or later
- Ollama (for AI features) - [Download](https://ollama.ai)
- Qwen 2 model pulled in Ollama (or compatible open-source model)

### Setup Ollama

1. **Install Ollama**: Download from [ollama.ai](https://ollama.ai)

2. **Pull Qwen2 Model** (or alternative):
   ```bash
   # Pull Qwen 2 model (recommended - ~4GB)
   ollama pull qwen2

   # Alternative: Other efficient models
   ollama pull mistral      # Mistral 7B (5GB)
   ollama pull neural-chat  # Neural Chat 7B (4GB)
   ollama pull llama2       # Llama 2 7B (4GB)
   ```

3. **Run Ollama Server**:
   ```bash
   ollama serve
   # Default listens on http://localhost:11434
   ```

## Project Structure

```
src/main/java/com/nauczycielka/
├── NauczycielkaApplication.java          # Main Spring Boot Application
├── controller/                            # REST API Endpoints
│   ├── CourseController.java
│   ├── LiveSessionController.java
│   ├── VocabularyController.java
│   ├── SummaryController.java
│   └── EnrollmentController.java
├── service/                               # Business Logic Layer
│   ├── CourseService.java
│   ├── UserService.java
│   ├── LiveSessionService.java
│   ├── VocabularyService.java
│   ├── CourseSummaryService.java
│   ├── EnrollmentService.java
│   └── ai/                                # AI Integration Services
│       ├── OllamaClient.java              # HTTP Client Interface
│       ├── VocabularyExtractor.java       # Extract terms from transcripts
│       └── SummaryGenerator.java          # Generate lecture summaries
├── model/                                 # JPA Entity Models
│   ├── User.java
│   ├── Course.java
│   ├── LiveSession.java
│   ├── Transcript.java
│   ├── VocabularyItem.java
│   ├── CourseSummary.java
│   ├── StudentEnrollment.java
│   ├── UserRole.java
│   └── SessionStatus.java
├── repository/                            # Spring Data JPA Repositories
│   ├── UserRepository.java
│   ├── CourseRepository.java
│   ├── LiveSessionRepository.java
│   ├── TranscriptRepository.java
│   ├── VocabularyRepository.java
│   ├── CourseSummaryRepository.java
│   └── StudentEnrollmentRepository.java
├── dto/                                   # Data Transfer Objects
│   ├── CreateCourseRequest.java
│   ├── UpdateCourseRequest.java
│   ├── CreateLiveSessionRequest.java
│   ├── TranscriptRequest.java
│   ├── EnrollmentRequest.java
│   └── ai/                                # AI DTOs
│       ├── OllamaRequest.java
│       └── OllamaResponse.java
└── config/                                # Spring Configuration
    ├── OllamaConfig.java                  # Ollama RestClient Bean
    ├── SecurityConfig.java                # Spring Security
    ├── AsyncConfig.java                   # Thread Pool for Async Tasks
    └── GlobalExceptionHandler.java        # RFC 7807 Error Handling

src/main/resources/
├── application.yaml                       # Application Properties
└── db/migration/                          # Flyway Migrations (optional)
```

## Installation & Setup

### 1. Clone Repository
```bash
cd /Users/zuahmed/Project/Nauczycielka
```

### 2. Install Dependencies
```bash
mvn clean install
```

### 3. Start Ollama Server
```bash
# In a separate terminal
ollama serve
```

### 4. Run the Application
```bash
mvn spring-boot:run
```

Application will start on `http://localhost:8080`

### 5. Access H2 Database Console (Optional)
```
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:nauczycielka
Username: sa
Password: (leave blank)
```

## API Endpoints

### Course Management

**Create Course**
```bash
POST /api/v1/courses
Content-Type: application/json

{
  "title": "Python Fundamentals",
  "description": "Learn Python basics",
  "instructor_id": 1
}
```

**Get All Courses**
```bash
GET /api/v1/courses
```

**Get Course by ID**
```bash
GET /api/v1/courses/{courseId}
```

**Get Courses by Instructor**
```bash
GET /api/v1/courses/instructor/{instructorId}
```

**Update Course**
```bash
PUT /api/v1/courses/{courseId}
Content-Type: application/json

{
  "title": "Advanced Python",
  "description": "Learn advanced Python concepts"
}
```

**Delete Course**
```bash
DELETE /api/v1/courses/{courseId}
```

### Live Session Management

**Start Live Session**
```bash
POST /api/v1/courses/{courseId}/sessions
Content-Type: application/json

{
  "title": "Session 1: Variables & Data Types",
  "description": "Introduction to variables in Python"
}
```

**Get Sessions for Course**
```bash
GET /api/v1/courses/{courseId}/sessions
```

**Get Specific Session**
```bash
GET /api/v1/courses/{courseId}/sessions/{sessionId}
```

**Add Transcript to Session**
```bash
POST /api/v1/courses/{courseId}/sessions/{sessionId}/transcripts
Content-Type: application/json

{
  "content": "Today we'll learn about variables. A variable is a named storage location...",
  "speaker": "John Doe"
}
```

**End Session & Generate Content** (Triggers AI processing)
```bash
POST /api/v1/courses/{courseId}/sessions/{sessionId}/end
```

### Vocabulary Management

**Get Vocabulary for Course**
```bash
GET /api/v1/courses/{courseId}/vocabulary
```

**Get Vocabulary for Student**
```bash
GET /api/v1/courses/{courseId}/vocabulary/student/{studentId}
```

**Get Specific Vocabulary Item**
```bash
GET /api/v1/courses/{courseId}/vocabulary/{itemId}
```

### Lecture Summary

**Get Course Summary**
```bash
GET /api/v1/courses/{courseId}/summary
```

**Get Session Summary**
```bash
GET /api/v1/courses/{courseId}/summary/session/{sessionId}
```

### Student Enrollment

**Enroll Student**
```bash
POST /api/v1/courses/{courseId}/enrollments
Content-Type: application/json

{
  "student_id": 5
}
```

**Get Course Enrollments**
```bash
GET /api/v1/courses/{courseId}/enrollments
```

**Remove Student from Course**
```bash
DELETE /api/v1/courses/{courseId}/enrollments/student/{studentId}
```

## Workflow Example

### 1. Create Users
```bash
# Create instructor (via database directly or future endpoint)
INSERT INTO users (email, full_name, password, role) 
VALUES ('instructor@example.com', 'Dr. Smith', 'hashed_pwd', 'INSTRUCTOR');

# Create students
INSERT INTO users (email, full_name, password, role) 
VALUES ('student1@example.com', 'John Doe', 'hashed_pwd', 'STUDENT');
```

### 2. Create Course
```bash
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Python 101",
    "description": "Learn Python basics",
    "instructor_id": 1
  }'
```

### 3. Enroll Students
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/enrollments \
  -H "Content-Type: application/json" \
  -d '{"student_id": 2}'
```

### 4. Start Live Session
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Session 1: Variables",
    "description": "Learn about Python variables"
  }'
```

### 5. Add Transcripts (During Session)
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions/1/transcripts \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Variables are containers for storing data values...",
    "speaker": "Dr. Smith"
  }'
```

### 6. End Session (Triggers AI Processing)
```bash
curl -X POST http://localhost:8080/api/v1/courses/1/sessions/1/end
# This asynchronously generates vocabulary and summary
```

### 7. Retrieve Generated Content
```bash
# Get vocabulary
curl http://localhost:8080/api/v1/courses/1/vocabulary

# Get summary
curl http://localhost:8080/api/v1/courses/1/summary
```

## Configuration

### application.yaml Properties

```yaml
# Ollama Configuration
ollama.base-url: http://localhost:11434    # Ollama server address
ollama.model: qwen2                         # Model to use
ollama.timeout: 300000                      # Request timeout (ms)

# Database (H2 for dev, PostgreSQL for prod)
spring.datasource.url: jdbc:h2:mem:nauczycielka
spring.jpa.hibernate.ddl-auto: create-drop # auto, create, update, validate

# Logging
logging.level.com.nauczycielka: DEBUG      # Set to DEBUG for detailed logs
```

### Switch to PostgreSQL (Production)

**Update pom.xml** (PostgreSQL driver included):
```xml
<!-- Already included in pom.xml -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>
```

**Update application.yaml**:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/nauczycielka
    username: postgres
    password: your_password
    driver-class-name: org.postgresql.Driver
  jpa:
    database: postgresql
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## Building & Deployment

### Build JAR
```bash
mvn clean package
java -jar target/nauczycielka-1.0.0-SNAPSHOT.jar
```

### Docker Build (Future Enhancement)
```dockerfile
FROM eclipse-temurin:21-jre
COPY target/nauczycielka-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

## Testing

### Run Tests
```bash
mvn test
```

### Integration Tests with Testcontainers
```bash
mvn verify
# Testcontainers configuration ready in pom.xml
```

## Future Enhancements

- [ ] JWT Authentication & Authorization
- [ ] WebSocket real-time updates for live transcript display
- [ ] Claude API integration for production AI inference
- [ ] Video file processing (transcription + AI analysis)
- [ ] Student progress tracking
- [ ] Quiz/Assessment generation from lectures
- [ ] Multi-language support
- [ ] Docker Compose setup for local dev
- [ ] GraphQL API alongside REST
- [ ] Caching layer (Redis)
- [ ] Advanced search with Elasticsearch

## Performance Considerations

- **Async Processing**: AI vocabulary/summary generation runs asynchronously to avoid blocking requests
- **ThreadPool**: Configured with 2 core threads, 5 max threads for AI tasks
- **Database**: H2 in-memory for dev, PostgreSQL with connection pooling for production
- **Ollama**: Model inference runs locally; optimize model size for deployment environment

## Troubleshooting

### Ollama Connection Error
```
Error: Unable to connect to http://localhost:11434
Solution:
1. Ensure Ollama is running: ollama serve
2. Check port 11434 is accessible
3. Verify model is pulled: ollama pull qwen2
```

### Out of Memory for Qwen2
```
If running on low-memory machine:
ollama pull mistral      # Uses ~5GB
ollama pull neural-chat  # Uses ~4GB
Then update application.yaml: ollama.model: mistral
```

### H2 Console Not Accessible
```
Enable via application.yaml:
spring.h2.console.enabled: true
spring.h2.console.path: /h2-console
Access: http://localhost:8080/h2-console
```

## Contributing

Contributions welcome! Areas for improvement:
- Add authentication/authorization layer
- Expand AI capabilities
- Improve error handling
- Add comprehensive test coverage

## License

MIT License - Free for educational and commercial use

## Support

For issues and questions:
1. Check troubleshooting section above
2. Review logs: `tail -f logs/nauczycielka.log`
3. Open an issue in repository

---

**Happy Teaching! 🎓**
