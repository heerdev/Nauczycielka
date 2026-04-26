# 📋 EXECUTION CHECKLIST

## Before Building

- [ ] Read this checklist
- [ ] Verify Java 17 is installed: `java -version`
- [ ] Verify Maven is installed: `mvn -version`
- [ ] Read `BUILD_AND_RUN.md` for detailed guide
- [ ] Ensure you have internet (Maven downloads dependencies)

---

## Build Phase

```bash
cd /Users/zuahmed/Project/Nauczycielka
```

- [ ] Navigate to project directory
- [ ] Run: `mvn clean install`
- [ ] Look for: **BUILD SUCCESS**
- [ ] Wait for: All dependencies downloaded
- [ ] Verify: No ERROR messages
- [ ] Check: `target/nauczycielka-1.0.0-SNAPSHOT.jar` was created

**Expected Output (last lines):**
```
[INFO] BUILD SUCCESS
[INFO] Total time: X.XXXs
[INFO] Finished at: YYYY-MM-DDTHH:MM:SS+02:00
```

---

## Run Phase

**Choose one method:**

### Method 1: Maven Plugin (Recommended)
```bash
mvn spring-boot:run
```

### Method 2: Java Direct
```bash
java -jar target/nauczycielka-1.0.0-SNAPSHOT.jar
```

### Method 3: IntelliJ IDEA
- Right-click: `NauczycielkaApplication.java`
- Select: "Run 'NauczycielkaApplication.main()'"

---

## Startup Verification

Watch the console for these messages (in order):

- [ ] `Starting NauczycielkaApplication`
- [ ] `Initializing Spring Boot application`
- [ ] `Initialized JPA EntityManagerFactory`
- [ ] `HikariPool-1 - Starting`
- [ ] `Tomcat started on port(s): 8080`
- [ ] `Started NauczycielkaApplication`

**Success message should appear around 5-10 seconds after starting**

---

## Test Phase

Open **NEW terminal window** and run:

### Test 1: Health Check
```bash
curl http://localhost:8080/actuator/health
```

**Expected Response:**
```json
{"status":"UP"}
```

- [ ] Response received
- [ ] Status is "UP"

### Test 2: Database Console (Optional)
```
http://localhost:8080/h2-console
```

- [ ] Page loads
- [ ] JDBC URL: `jdbc:h2:mem:nauczycielka`
- [ ] User: `sa`
- [ ] Password: (blank)
- [ ] Click "Connect"

### Test 3: API Endpoint (Optional)
```bash
curl -X GET http://localhost:8080/api/v1/courses
```

- [ ] Request succeeds
- [ ] Empty array or data returned

---

## Troubleshooting Steps

If build fails:
- [ ] Check Java version: `java -version` (need 17+)
- [ ] Check Maven version: `mvn -version` (need 3.6+)
- [ ] Try: `mvn clean install -U`
- [ ] If still fails, delete `~/.m2/repository` and retry

If startup fails:
- [ ] Check console for error message
- [ ] See `BUILD_AND_RUN.md` troubleshooting section
- [ ] Verify no other process is on port 8080
- [ ] Check internet connection (if Ollama is configured)

If tests fail:
- [ ] Verify application is still running
- [ ] Check firewall isn't blocking port 8080
- [ ] Try different terminal window
- [ ] Use `http://` not `https://`

---

## Final Checklist

- [ ] Build completed successfully
- [ ] Application started without errors
- [ ] Saw "Started NauczycielkaApplication"
- [ ] Health check returned "UP"
- [ ] No exceptions in console
- [ ] Application running on port 8080

---

## Next Steps After Successful Run

1. ✅ **Keep the application running** (don't close the terminal)
2. ✅ **Test the API endpoints**
3. ✅ **Explore the database** via H2 console
4. ✅ **Review the documentation**
5. ✅ **Add Ollama** for AI features (optional)
6. ✅ **Develop your features**

---

## Ollama Integration (Optional)

To add AI capabilities:

```bash
# Terminal 2: Install and run Ollama
brew install ollama
ollama pull qwen2
ollama serve
```

- [ ] Ollama installed
- [ ] Qwen2 model pulled
- [ ] Ollama running on `http://localhost:11434`

Application will auto-detect and use it.

---

## Documentation Reference

- ✅ **BUILD_AND_RUN.md** - Complete guide
- ✅ **IMPLEMENTATION_COMPLETE.md** - Full summary
- ✅ **ARCHITECTURE_DIAGRAM.md** - Visual reference
- ✅ **CIRCULAR_DEPENDENCY_FIX.md** - Technical details
- ✅ **STATUS.md** - Quick status

---

## Contact Points

If you need to:

- **Change port**: Edit `application.yaml` → `server.port: 8081`
- **Use PostgreSQL**: Edit `application.yaml` → datasource config
- **Add endpoints**: Create files in `src/main/java/controller/`
- **Add services**: Create files in `src/main/java/service/`
- **Add entities**: Create files in `src/main/java/model/`

---

## Success Indicators ✅

- [x] Project fixed (circular dependency resolved)
- [x] Configuration corrected (YAML format)
- [x] Documentation provided (6 files)
- [x] Build scripts prepared (Maven pom.xml)
- [x] Ready to run

**You are ready to execute!**

---

## Still Have Questions?

1. Check `BUILD_AND_RUN.md` for step-by-step guide
2. Check `ARCHITECTURE_DIAGRAM.md` for visual explanation
3. Check `CIRCULAR_DEPENDENCY_FIX.md` for technical details
4. Review application logs for error messages
5. Verify prerequisites are installed

---

**Now go build and run your application! 🚀**

```bash
cd /Users/zuahmed/Project/Nauczycielka && mvn spring-boot:run
```
