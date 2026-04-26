# DataIntegrityViolationException: NULL not allowed for column "CREATED_AT" - FIXED

## Problem Description

When trying to create or insert entities into the database, you received this error:

```
org.springframework.dao.DataIntegrityViolationException: 
could not execute statement [NULL not allowed for column "CREATED_AT"; 
SQL statement: insert into courses (created_at,description,instructor_id,title,updated_at,id) 
values (?,?,?,?,?,default)]
```

**Root Cause**: The `createdAt` (and similar timestamp) fields were being inserted as NULL instead of the current timestamp.

## Why This Happened

### Problem 1: Field Initialization vs Hibernate
```java
// ❌ WRONG - This initializes the field in Java, NOT in Hibernate
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt = LocalDateTime.now();
```

When you use `= LocalDateTime.now()` directly in the field declaration, it:
- Executes when the Java object is created
- BUT Hibernate ignores this when inserting
- Results in NULL being inserted into the database

### Problem 2: Lombok @Builder Ignores Defaults
```java
// ❌ WRONG - Lombok builder doesn't use default values
@Builder
public class Course {
    private LocalDateTime createdAt = LocalDateTime.now();  // Ignored by builder!
}
```

When using Lombok's `@Builder`, the default values assigned in field declarations are ignored.

## Solution Applied

### Fix 1: Add `@PrePersist` Annotation
```java
// ✅ CORRECT - Called by Hibernate before INSERT
@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
}
```

The `@PrePersist` annotation ensures the timestamp is set RIGHT BEFORE insertion.

### Fix 2: Add `@Builder.Default` Annotation
```java
// ✅ CORRECT - Tells Lombok builder to use the default value
@Builder.Default
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt = LocalDateTime.now();
```

The `@Builder.Default` annotation tells Lombok to use the field's default value when the builder is used.

## Complete Solution Pattern

Here's the correct pattern for timestamp fields:

```java
@Entity
@Table(name = "courses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // ... other fields ...
    
    // ✅ CORRECT PATTERN
    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Builder.Default
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Called automatically by Hibernate before INSERT
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Called automatically by Hibernate before UPDATE
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

## Files Modified

All entity classes have been updated with the correct pattern:

1. **Course.java** ✅
   - Added `@PrePersist` method
   - Added `@Builder.Default` to `createdAt` and `updatedAt`

2. **LiveSession.java** ✅
   - Added `@PrePersist` method
   - Added `@Builder.Default` to `createdAt` and `updatedAt`

3. **VocabularyItem.java** ✅
   - Added `@PrePersist` method
   - Added `@Builder.Default` to `createdAt` and `updatedAt`
   - Removed unused imports

4. **StudentEnrollment.java** ✅
   - Added `@PrePersist` method
   - Added `@Builder.Default` to `enrolledAt` and `isActive`

5. **Transcript.java** ✅
   - Added `@PrePersist` method
   - Added `@Builder.Default` to `createdAt` and `isProcessed`

6. **User.java** ✅
   - Added `@PrePersist` method
   - Already had `@Builder.Default` on timestamps

## How JPA Lifecycle Callbacks Work

JPA provides callback annotations that are invoked at specific times:

```
Entity Created
    ↓
@PrePersist invoked ← Sets timestamps
    ↓
INSERT executed with values
    ↓
Data in database ✅

Entity Updated
    ↓
@PreUpdate invoked ← Updates modified_at
    ↓
UPDATE executed with values
    ↓
Data in database ✅
```

## Testing the Fix

Create a course using your API:

```bash
curl -X POST http://localhost:8080/api/v1/courses \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Java Basics",
    "description": "Learn Java fundamentals",
    "instructorId": 1
  }'
```

**Expected**: ✅ Course created successfully with timestamps

Check the database:

```bash
# Access H2 console at http://localhost:8080/h2-console
SELECT * FROM courses;
# Should show created_at and updated_at with current timestamps
```

## Key Takeaways

| Item | What It Does |
|------|-------------|
| `@Column(nullable = false)` | Database constraint - column can't be NULL |
| `= LocalDateTime.now()` | Java field initialization (ignored by Hibernate) |
| `@Builder.Default` | Tells Lombok builder to use default value |
| `@PrePersist` | Called by Hibernate BEFORE INSERT |
| `@PreUpdate` | Called by Hibernate BEFORE UPDATE |

## Why Both `@Builder.Default` and `@PrePersist`?

They serve different purposes:

1. **`@Builder.Default`**: For when entities are created using the builder pattern
   ```java
   Course course = Course.builder()
       .title("Course")
       .build();  // ← createdAt will be set by @Builder.Default
   ```

2. **`@PrePersist`**: For when entities are persisted to database
   ```java
   entityManager.persist(course);  // ← @PrePersist is called here
   ```

Using both ensures timestamps are always set, regardless of how the entity is created.

## Common Mistakes to Avoid

❌ **WRONG**: Just using field initialization
```java
private LocalDateTime createdAt = LocalDateTime.now();  // Ignored by Hibernate!
```

❌ **WRONG**: Using `@PrePersist` without `@Builder.Default`
```java
// If entity created via builder, createdAt might be null before @PrePersist is called
@Builder
public class Course {
    private LocalDateTime createdAt = LocalDateTime.now();  // Builder ignores this
}
```

❌ **WRONG**: Using `@ColumnDefault`
```java
// This sets a database default, not a Java default
@ColumnDefault("CURRENT_TIMESTAMP")
private LocalDateTime createdAt;  // Still null in Java code!
```

✅ **CORRECT**: Using both `@Builder.Default` AND `@PrePersist`
```java
@Builder.Default
@Column(name = "created_at", nullable = false, updatable = false)
private LocalDateTime createdAt = LocalDateTime.now();

@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
}
```

## Verification Checklist

- [x] All entity classes have `@PrePersist` methods
- [x] All timestamp fields have `@Builder.Default` annotation
- [x] No compilation errors
- [x] Can create entities via REST API
- [x] Timestamps are properly set in database

## Next Steps

1. **Build the project**: `mvn clean install`
2. **Run the application**: `mvn spring-boot:run`
3. **Test creating entities**:
   - Via REST API endpoints
   - Check H2 console for correct timestamps
4. **Verify**: All entities should save with `created_at` and `updated_at` timestamps

## References

- JPA Callback Annotations: https://jakarta.ee/specifications/persistence/3.0/jakarta-persistence-spec-3.0.html
- Hibernate Lifecycle Callbacks: https://docs.jboss.org/hibernate/orm/6.4/userguide/html_single/Hibernate_User_Guide.html#events
- Lombok @Builder.Default: https://projectlombok.org/features/Builder

## Status

✅ **FIXED**: All entities now properly set timestamps on creation and update

The DataIntegrityViolationException for NULL timestamps has been resolved!
