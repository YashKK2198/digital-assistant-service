# ===================================================================
# DIGITAL ASSISTANT SERVICE - DOCKERFILE
# ===================================================================
# This Dockerfile containerizes the Spring Boot application for CI/CD deployment
# It uses a multi-stage build to keep the runtime image small and optimized.
# ===================================================================

# ===================================================================
# BUILD STAGE - Compile and package the application
# ===================================================================
FROM openjdk:8-jdk-alpine AS builder

# Install Maven (required for Java builds in Alpine)
RUN apk add --no-cache maven

# Set working directory
WORKDIR /app

# Copy Maven configuration
COPY pom.xml .
COPY .mvn .mvn

# Pre-download dependencies (helps caching between builds)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src src

# Build the application (skip tests for faster CI builds)
RUN mvn clean package -DskipTests

# ===================================================================
# RUNTIME STAGE - Lightweight image to run the application
# ===================================================================
FROM openjdk:8-jre-alpine AS runtime

# Set working directory
WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/digital-assistant-service-1.0.0.jar app.jar

# Expose port 8080 for the application
EXPOSE 8080

# Set JVM options for better container performance
ENV JAVA_OPTS="-Xmx512m -Xms256m -Djava.security.egd=file:/dev/./urandom"

# Health check for Spring Boot actuator endpoint
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
