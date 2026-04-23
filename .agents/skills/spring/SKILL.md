---
name: spring-boot-latest-standards
description: Best practices for Spring Boot 3.4+ and Spring Framework 6.2+.
---

# Spring Boot Latest Standards

Adhere to these standards for modern Spring development, focusing on performance, observability, and modern API usage.

## Web & REST
- **RestClient**: Prefer `RestClient` over `RestTemplate` or `WebClient` for synchronous, blocking operations.
- **Problem Details**: Implement RFC 7807 using `@ControllerAdvice` or `ResponseEntityExceptionHandler` for standardized error responses.
- **HTTP Interfaces**: Use `@HttpExchange` interfaces for declarative client definitions.

## Dependency Injection & Configuration
- **Constructor Injection**: Use final fields and mandatory constructor injection. Avoid `@Autowired` on fields.
- **@ConfigurationProperties**: Prefer type-safe properties over `@Value` annotations.
- **Functional Bean Definitions**: Consider functional bean registration for core infrastructure when startup time is a priority.

## Persistence (Spring Data)
- **Derived Methods**: Use method name derivation for simple queries.
- **Project Results**: Use Interface or Class-based Projections instead of returning full `@Entity` objects to controllers.
- **Database Client**: Prefer `JdbcClient` for modern, fluent-style JDBC access without the overhead of JPA when appropriate.

## Observability & Security
- **Micrometer Observation**: Use the Observation API for combined metrics and tracing.
- **Security**: Use the `SecurityFilterChain` bean DSL. Avoid legacy `WebSecurityConfigurerAdapter`.
- **Validation**: Use Jakarta Bean Validation 3.0+ constraints (`@NotNull`, `@Size`, etc.).

## Native Image & Cloud
- **GraalVM**: Ensure code is "Native-Friendly." Avoid classpath scanning or reflection that hasn't been hinted.
- **Docker Compose/Testcontainers**: Use `spring-boot-docker-compose` for local development and Testcontainers for integration tests.

## Constraints
- NO XML configuration.
- NO `@Field` level injection.
- AVOID `@Lazy` unless strictly necessary for circular dependency resolution.
