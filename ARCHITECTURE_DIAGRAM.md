# Architecture Diagram: Before & After

## Problem: Circular Dependency

### BEFORE FIX ❌

```
┌─────────────────────────────────────────────────────────────┐
│                     Spring Boot Context                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌──────────────────┐         ┌──────────────────────┐      │
│  │ LiveSessionCtrl  │         │ LiveSessionService   │      │
│  └────────┬─────────┘         └──────────┬───────────┘      │
│           │ depends on                   │                   │
│           └──────────────────────────────┤                   │
│                                          ▼                   │
│                    ┌─────────────────────────────────┐      │
│                    │ CourseSummaryService            │      │
│                    └──────────────┬──────────────────┘      │
│                                   │ depends on              │
│                                   ▼                         │
│                    ┌──────────────────────────────────┐     │
│                    │ LiveSessionService ⚠️            │     │
│                    │ (Already being created!)         │     │
│                    └──────────────────────────────────┘     │
│                                                               │
│  🔴 CIRCULAR REFERENCE DETECTED!                             │
│  Spring cannot resolve: Who creates what, in what order?    │
│                                                               │
└─────────────────────────────────────────────────────────────┘
```

**ERROR**: `UnsatisfiedDependencyException: circular reference`

---

## Solution: Repository Pattern

### AFTER FIX ✅

```
┌──────────────────────────────────────────────────────────────┐
│                     Spring Boot Context                       │
├──────────────────────────────────────────────────────────────┤
│                                                                │
│  ┌──────────────────┐         ┌──────────────────────┐       │
│  │ LiveSessionCtrl  │         │ LiveSessionService   │       │
│  └────────┬─────────┘         └──────────┬───────────┘       │
│           │ depends on                   │                    │
│           └──────────────────────────────┤                    │
│                                          ▼                    │
│                    ┌─────────────────────────────────┐       │
│                    │ CourseSummaryService            │       │
│                    └──────────────┬──────────────────┘       │
│                                   │ depends on               │
│                                   ▼                          │
│                    ┌──────────────────────────────────┐      │
│                    │ LiveSessionRepository            │      │
│                    │ (No circular dependency!)        │      │
│                    └──────────────────────────────────┘      │
│                                   │                           │
│                                   ▼                           │
│                    ┌──────────────────────────────────┐      │
│                    │ LiveSession Entity               │      │
│                    │ (Database data)                  │      │
│                    └──────────────────────────────────┘      │
│                                                                │
│  ✅ CLEAN DEPENDENCY FLOW!                                    │
│  Spring can resolve and initialize all beans in order        │
│                                                                │
└──────────────────────────────────────────────────────────────┘
```

**SUCCESS**: Application starts and runs properly

---

## Dependency Injection Flow

### The Key Change

```javascript
// ❌ WRONG: Service depending on another Service
CourseSummaryService {
    LiveSessionService liveSessionService;  // ← Circular!
}

// ✅ CORRECT: Service depending on Repository
CourseSummaryService {
    LiveSessionRepository liveSessionRepository;  // ← Clean!
}
```

---

## Data Flow: Creating a Course Summary

### Process:

```
User Request to End Session
        │
        ▼
   LiveSessionService.endSessionAndGenerateContent()
        │
        ├─→ Extract transcripts from session
        │
        ├─→ Call AI to generate vocabulary
        │
        ├─→ Call AI to generate summary
        │
        └─→ Call CourseSummaryService.createCourseSummary()
                │
                ├─→ courseService.getCourseById()  [✓ OK]
                │
                ├─→ liveSessionRepository.findById()  [✓ OK - No circular ref!]
                │
                └─→ summaryRepository.save()
                        │
                        ▼
                    Database (H2/PostgreSQL)
```

---

## Service Dependencies Graph

### Before (Problematic)

```
LiveSessionService
    ├── LiveSessionRepository
    ├── TranscriptRepository
    ├── CourseService
    ├── VocabularyService
    ├── CourseSummaryService ⚠️
    │       └── LiveSessionService ⚠️ ← CIRCULAR!
    ├── VocabularyExtractor
    └── SummaryGenerator
```

### After (Clean)

```
LiveSessionService
    ├── LiveSessionRepository
    ├── TranscriptRepository
    ├── CourseService
    ├── VocabularyService
    ├── CourseSummaryService ✓
    │       ├── LiveSessionRepository ✓
    │       └── CourseService ✓
    ├── VocabularyExtractor
    └── SummaryGenerator
```

---

## Repository Pattern Benefits

```
┌─────────────────────────────────────────────┐
│ Services (Business Logic Layer)             │
├─────────────────────────────────────────────┤
│  • Implement business rules                 │
│  • Coordinate workflows                     │
│  • Call other services (carefully!)         │
│  • Depend on Repositories for data access   │
└────────────────┬────────────────────────────┘
                 │
                 │ Uses (dependency)
                 ▼
┌─────────────────────────────────────────────┐
│ Repositories (Data Access Layer)            │
├─────────────────────────────────────────────┤
│  • Query entities from database             │
│  • Save/update entities                     │
│  • Encapsulate database access              │
│  • No dependencies on other services        │
└────────────────┬────────────────────────────┘
                 │
                 │ Uses (JPA)
                 ▼
┌─────────────────────────────────────────────┐
│ Database (H2 / PostgreSQL)                  │
├─────────────────────────────────────────────┤
│  • Persistent data storage                  │
│  • Tables, queries, transactions            │
└─────────────────────────────────────────────┘
```

---

## Spring Boot Initialization Order

### Sequential Bean Creation (After Fix)

```
1. RepositoryBeans
   ├── LiveSessionRepository ✓
   ├── CourseSummaryRepository ✓
   ├── CourseRepository ✓
   ├── VocabularyRepository ✓
   ├── StudentEnrollmentRepository ✓
   ├── TranscriptRepository ✓
   └── UserRepository ✓

2. ServiceBeans
   ├── CourseService ✓
   ├── UserService ✓
   ├── VocabularyService ✓
   ├── EnrollmentService ✓
   ├── CourseSummaryService ✓ (depends only on repos)
   └── LiveSessionService ✓ (depends on CourseSummaryService & repos)

3. ControllerBeans
   ├── LiveSessionController ✓
   ├── CourseController ✓
   ├── VocabularyController ✓
   ├── SummaryController ✓
   └── EnrollmentController ✓

4. WebServer
   ├── Tomcat initialized ✓
   └── Listening on port 8080 ✓

✅ APPLICATION READY
```

---

## Configuration Files

### application.yaml Structure

```yaml
spring.application.name: nauczycielka

# Server
server.port: 8080                              ← App runs on localhost:8080

# Database (H2 for development)
spring.datasource.url: jdbc:h2:mem:nauczycielka  ← FIXED! ✅
spring.jpa.hibernate.ddl-auto: create-drop   ← Auto-create schema

# Database Console
spring.h2.console.enabled: true               ← Access at /h2-console
spring.h2.console.path: /h2-console

# Logging
logging.level.com.nauczycielka: DEBUG         ← See app logs
logging.level.org.springframework.web: DEBUG  ← See Spring logs

# Actuator (Monitoring)
management.endpoints.web.exposure.include: health,info,metrics
                                          ← Access at /actuator/*

# Ollama (AI Integration)
ollama.base-url: http://localhost:11434      ← Local Ollama
ollama.model: qwen2                           ← Model to use
ollama.timeout: 300000                        ← 5 min timeout
```

---

## Testing the Fix

### Verification Checklist

```
[ ] Java 17 installed
    → java -version

[ ] Maven installed
    → mvn -version

[ ] Project builds successfully
    → mvn clean compile

[ ] No compilation errors
    → Only minor "unused method" warnings OK

[ ] Application starts without errors
    → mvn spring-boot:run

[ ] Look for "Started NauczycielkaApplication"
    → Indicates successful initialization

[ ] Health endpoint responds
    → curl http://localhost:8080/actuator/health
    → Should return {"status":"UP"}

[ ] H2 Console accessible
    → http://localhost:8080/h2-console
    → Login with sa / (blank password)

[ ] API endpoints work
    → Try creating a course, getting courses, etc.
```

---

## Summary

| Item | Before | After |
|------|--------|-------|
| **Circular Dependency** | ❌ Yes | ✅ No |
| **Spring Initialization** | ❌ Failed | ✅ Success |
| **Bean Wiring** | ❌ Impossible | ✅ Automatic |
| **Application Startup** | ❌ Error | ✅ Runs |
| **Architecture Quality** | ⚠️ Poor | ✅ Good |
| **Maintainability** | ⚠️ Low | ✅ High |

---

## Key Takeaway

**The Repository Pattern** is the standard solution for breaking circular dependencies:
- Services access data through repositories
- Repositories don't depend on services
- Clean separation of concerns
- Easy to test and maintain
- Scalable architecture

