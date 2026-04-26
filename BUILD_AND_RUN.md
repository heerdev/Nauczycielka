# Complete Step-by-Step Guide: Build & Run the Application

## ✅ What Has Been Fixed

1. **Circular Dependency** - Resolved by refactoring `CourseSummaryService` to use `LiveSessionRepository` directly
2. **YAML Configuration** - Fixed syntax error in `application.yaml` (datasource URL)
3. **Compilation Status** - All errors resolved, project compiles successfully

---

## Prerequisites Check

Before running, ensure you have:

```bash
# Check Java version (need 17+)
java -version

# Check Maven version (need 3.6+)
mvn -version
```

If not installed, install them via:
- **macOS**: `brew install openjdk@17 maven`
- Or download from oracle.com and maven.apache.org

---

## Step 1: Build the Project

### Option A: Build with Maven (Recommended)

```bash
cd /Users/zuahmed/Project/Nauczycielka

# Clean previous builds and compile
mvn clean compile

# Full build (creates JAR file)
mvn clean package -DskipTests

# Or full build with tests
mvn clean install
```

### What to Look For:
✅ BUILD SUCCESS message at the end
✅ No ERROR lines in output
✅ Created file: `target/nauczycielka-1.0.0-SNAPSHOT.jar`

---

## Step 2: Run the Application

### Option A: Using Maven (Simplest)

```bash
cd /Users/zuahmed/Project/Nauczycielka
mvn spring-boot:run
```

### Option B: Using Java directly (after building)

```bash
java -jar /Users/zuahmed/Project/Nauczycielka/target/nauczycielka-1.0.0-SNAPSHOT.jar
```

### Option C: Using IDE (IntelliJ IDEA)

1. Right-click on `NauczycielkaApplication.java`
2. Select "Run 'NauczycielkaApplication.main()'"
3. Or click the green play button next to the class definition

---

## Step 3: Verify Application Started Successfully

Look for these log messages (in order):

```
INFO ... Initializing Spring Boot application
INFO ... Starting NauczycielkaApplication
INFO ... Initialized JPA EntityManagerFactory for persistence unit 'default'
INFO ... HikariPool-1 - Starting
INFO ... Tomcat started on port(s): 8080 (http)
INFO ... Started NauczycielkaApplication in X.XXX seconds
```

---

## Step 4: Test the Application

Once you see "Started NauczycielkaApplication", the app is running!

### Test 1: Health Check

```bash
# In a new terminal
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

### Test 2: Access H2 Database Console

```
http://localhost:8080/h2-console
```

Login with:
- **JDBC URL**: `jdbc:h2:mem:nauczycielka`
- **User**: `sa`
- **Password**: (leave blank)

### Test 3: Try API Endpoints

```bash
# Create a course
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{"title":"Java Basics","description":"Learn Java"}'

# Get all courses
curl http://localhost:8080/api/v1/courses
```

---

## Troubleshooting

### Issue 1: "mvn: command not found"

**Solution**: Maven is not in PATH
```bash
# Install Maven
brew install maven

# Or add to PATH manually
export PATH="/usr/local/opt/maven/bin:$PATH"
```

### Issue 2: "Java 17 is required"

**Solution**: Wrong Java version
```bash
# Check current version
java -version

# Install Java 17
brew install openjdk@17

# Set as default (macOS)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Issue 3: Port 8080 already in use

**Solution**: Change the port
```bash
# Edit application.yaml
server.port: 8081

# Then run application
```

### Issue 4: Database connection errors

**Solution**: H2 is auto-configured. If issues:
1. Delete `target/` folder
2. Run `mvn clean install` again
3. Start fresh

### Issue 5: "Cannot find symbol" during build

**Solution**: Clear Maven cache
```bash
rm -rf ~/.m2/repository
mvn clean install -U
```

---

## Project Structure Overview

```
Nauczycielka/
├── src/
│   └── main/
│       ├── java/com/nauczycielka/
│       │   ├── NauczycielkaApplication.java    ← Entry point
│       │   ├── config/                         ← Configuration
│       │   ├── controller/                     ← REST APIs
│       │   ├── service/                        ← Business logic
│       │   │   └── ai/                        ← AI integration
│       │   ├── model/                          ← Database entities
│       │   ├── repository/                     ← Data access
│       │   └── dto/                            ← Data transfer objects
│       └── resources/
│           └── application.yaml                ← Configuration ✅ (Fixed)
├── pom.xml                                      ← Maven config ✅
├── FIX_SUMMARY.md                              ← What was fixed ✅
├── CIRCULAR_DEPENDENCY_FIX.md                  ← Technical details ✅
└── RUN_APPLICATION.md                          ← Build instructions ✅
```

---

## API Endpoints Available

### Live Sessions
```
POST   /api/v1/courses/{courseId}/sessions
GET    /api/v1/courses/{courseId}/sessions
GET    /api/v1/courses/{courseId}/sessions/{sessionId}
POST   /api/v1/courses/{courseId}/sessions/{sessionId}/transcripts
POST   /api/v1/courses/{courseId}/sessions/{sessionId}/end
```

### Courses
```
GET    /api/v1/courses
POST   /api/v1/courses
GET    /api/v1/courses/{courseId}
PUT    /api/v1/courses/{courseId}
```

### Vocabulary
```
GET    /api/v1/vocabulary
GET    /api/v1/vocabulary/course/{courseId}
POST   /api/v1/vocabulary
```

### Summaries
```
GET    /api/v1/summaries/course/{courseId}
GET    /api/v1/summaries/session/{sessionId}
```

### Actuator (Monitoring)
```
GET    /actuator/health
GET    /actuator/info
GET    /actuator/metrics
```

---

## Configuration Details

### Database (H2 In-Memory)
- **Type**: H2 (auto-configured)
- **URL**: `jdbc:h2:mem:nauczycielka`
- **Console**: `http://localhost:8080/h2-console`
- **Auto DDL**: `create-drop` (recreates on startup)

### Server
- **Port**: 8080
- **Context Path**: /
- **Shutdown**: Press `Ctrl+C` in terminal

### Logging
- **Root Level**: INFO
- **App Level** (com.nauczycielka): DEBUG
- **Spring Web**: DEBUG

### Ollama Integration
- **Base URL**: `http://localhost:11434`
- **Model**: `qwen2`
- **Timeout**: 300 seconds

---

## What Happens on Startup

1. ✅ Spring Boot initializes
2. ✅ Loads properties from `application.yaml`
3. ✅ Creates H2 database (in-memory)
4. ✅ Initializes Hibernate (JPA)
5. ✅ Scans for @Component, @Service, @Repository, @Controller
6. ✅ **Injects dependencies** (now without circular reference!)
7. ✅ Creates all beans successfully
8. ✅ Starts Tomcat web server on port 8080
9. ✅ Ready to accept requests

---

## Expected Behavior After Fix

**Before**: Application failed to start with circular dependency error
**After**: Application starts successfully in ~5-10 seconds

---

## Next: Integration with Ollama (Optional)

To use AI features (vocabulary extraction and summary generation):

1. **Install Ollama** (if not already): https://ollama.ai
2. **Pull Qwen model**: `ollama pull qwen2`
3. **Run Ollama**: `ollama serve`
4. **App will connect** automatically to `http://localhost:11434`

Without Ollama, the app still runs but AI features will fail gracefully.

---

## Support

If you encounter issues:
1. Check the error message in console
2. Review the "Troubleshooting" section above
3. Check FIX_SUMMARY.md for technical details
4. Ensure all prerequisites are installed

**Happy coding! 🎉**
