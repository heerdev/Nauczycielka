FROM eclipse-temurin:21-jre-jammy

# Set working directory
WORKDIR /app

# Copy the JAR file
COPY target/nauczycielka-1.0.0-SNAPSHOT.jar nauczycielka.jar

# Expose port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar nauczycielka.jar"]
