# Quick Fix Reference: DataIntegrityViolationException - NULL Timestamps

## Problem
```
DataIntegrityViolationException: NULL not allowed for column "CREATED_AT"
```

## What Was Wrong
Timestamp fields weren't being set before database insertion.

## What Was Fixed

### The Pattern (Applied to All 6 Entity Classes)

```java
@Entity
public class YourEntity {
    
    // ✅ CORRECT: Both annotations needed!
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Hibernate calls this BEFORE INSERT
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Hibernate calls this BEFORE UPDATE
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

## Files Modified

1. ✅ Course.java
2. ✅ LiveSession.java
3. ✅ VocabularyItem.java
4. ✅ StudentEnrollment.java
5. ✅ Transcript.java
6. ✅ User.java

## Why Both Annotations?

| Annotation | Purpose |
|-----------|---------|
| `@Builder.Default` | For builder pattern: `Course.builder().build()` |
| `@PrePersist` | For JPA persistence: `entityManager.persist(course)` |

**Both are needed for complete coverage!**

## Test It

```bash
# 1. Build
mvn clean install

# 2. Run
mvn spring-boot:run

# 3. Create entity
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","description":"Test","instructorId":1}'

# 4. Check database (H2 console)
# Visit: http://localhost:8080/h2-console
# SQL: SELECT * FROM courses;
# Should see: created_at and updated_at with timestamps!
```

## Status
✅ **FIXED** - All entities now properly set timestamps on creation and update.

For detailed explanation, see: `TIMESTAMP_FIX.md`
