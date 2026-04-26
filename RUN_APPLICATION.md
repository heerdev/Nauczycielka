# Spring Boot Application Run Instructions

## Prerequisites

1. **Java 17 or higher** installed
2. **Maven 3.6+** installed
3. **Ollama** running locally (optional, for AI features)

## Building the Application

```bash
cd /Users/zuahmed/Project/Nauczycielka

# Clean and build
mvn clean install

# Or just compile without tests
mvn clean compile
```

## Running the Application

### Option 1: Using Maven
```bash
mvn spring-boot:run
```

### Option 2: Using Java directly
```bash
# First build the JAR
mvn clean package -DskipTests

# Then run it
java -jar target/nauczycielka-1.0.0-SNAPSHOT.jar
```

## Verifying the Application Started Successfully

Look for log messages like:
```
2026-04-24T22:53:45.629+02:00  INFO ... Initialized JPA EntityManagerFactory for persistence unit 'default'
2026-04-24T22:53:45.844+02:00  INFO ... Tomcat started on port(s): 8080
2026-04-24T22:53:45.850+02:00  INFO ... Started NauczycielkaApplication in X.XXX seconds
```

## Testing the Application

Once running, test the API:

```bash
# Health check
curl http://localhost:8080/actuator/health

# Access H2 Console (if enabled)
# http://localhost:8080/h2-console
```

## Common Issues & Solutions

### Issue: "Cannot find symbol" errors
**Solution**: Make sure you're using Java 17 and Maven is installed correctly
```bash
java -version  # Should show 17.x.x
mvn -version   # Should show 3.6+
```

### Issue: Port 8080 already in use
**Solution**: Change the port in application.yaml
```yaml
server.port: 8081  # Change to a different port
```

### Issue: Database connection errors
**Solution**: H2 should start automatically. If issues persist, verify:
- H2 dependencies are in pom.xml ✓
- spring.jpa.hibernate.ddl-auto is set to "create-drop" ✓

### Issue: Circular dependency errors (NOW FIXED!)
**Previous Error**: 
```
The dependencies of some of the beans in the application context form a cycle:
liveSessionController → liveSessionService → CourseSummaryService → liveSessionService
```

**Status**: ✅ RESOLVED
- Changed `CourseSummaryService` to use `LiveSessionRepository` directly
- Removed circular dependency between `LiveSessionService` and `CourseSummaryService`
- See `CIRCULAR_DEPENDENCY_FIX.md` for details

## Configuration Files

- **Application Config**: `src/main/resources/application.yaml`
  - Server port: 8080
  - Database: H2 in-memory
  - Ollama URL: http://localhost:11434
  - Ollama Model: qwen2

## Project Structure

```
src/main/
├── java/com/nauczycielka/
│   ├── NauczycielkaApplication.java      (Entry point)
│   ├── config/                           (Configuration classes)
│   ├── controller/                       (REST endpoints)
│   ├── service/                          (Business logic)
│   │   └── ai/                          (AI integration services)
│   ├── model/                            (JPA entities)
│   ├── repository/                       (Data access)
│   └── dto/                              (Data transfer objects)
└── resources/
    └── application.yaml                  (Configuration)
```

## API Endpoints

### Live Sessions
- `POST /api/v1/courses/{courseId}/sessions` - Start a session
- `GET /api/v1/courses/{courseId}/sessions` - Get all sessions
- `GET /api/v1/courses/{courseId}/sessions/{sessionId}` - Get session details
- `POST /api/v1/courses/{courseId}/sessions/{sessionId}/transcripts` - Add transcript
- `POST /api/v1/courses/{courseId}/sessions/{sessionId}/end` - End session & generate content

### Vocabulary
- `GET /api/v1/vocabulary` - Get all vocabulary items
- `GET /api/v1/vocabulary/course/{courseId}` - Get vocabulary for course
- `POST /api/v1/vocabulary` - Add vocabulary item

### Summaries
- `GET /api/v1/summaries/course/{courseId}` - Get course summary
- `GET /api/v1/summaries/session/{sessionId}` - Get session summary

## Troubleshooting Build Issues

```bash
# Clear Maven cache if needed
rm -rf ~/.m2/repository

# Rebuild
mvn clean install -U
```

## Notes

- Default database is **H2 in-memory** (resets on restart)
- For production, modify database configuration in `application.yaml`
- Async processing is enabled for AI tasks
- WebSocket support is available for real-time transcript capture
