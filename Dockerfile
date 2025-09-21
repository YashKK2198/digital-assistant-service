# ===================================================================
# DIGITAL ASSISTANT SERVICE - DOCKERFILE
# ===================================================================
# This Dockerfile containerizes the Spring Boot application for CI/CD deployment

# ===================================================================
# BUILD STAGE - Compile and package the application
# ===================================================================
FROM openjdk:8-jdk-alpine AS builder

# Install Maven
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy pom.xml and .mvn directory (if needed for config)
COPY pom.xml .
COPY .mvn .mvn

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application
RUN mvn clean package -DskipTests

# ===================================================================
# RUNTIME STAGE - Create lightweight runtime image
# ===================================================================
FROM openjdk:8-jre-alpine AS runtime

# Set working directory
WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/digital-assistant-service-1.0.0.jar app.jar

# Expose port 8080
EXPOSE 8080

# Set JVM options for containerized environment
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Health check to ensure container is healthy
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]