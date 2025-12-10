FROM eclipse-temurin:25-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle wrapper and build files first (better caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Download dependencies (cached layer if build files don't change)
RUN ./gradlew dependencies --no-daemon || true

# Copy the source code
COPY src src

# Build the Spring Boot application with layered JAR support
RUN ./gradlew bootJar --no-daemon && \
  java -Djarmode=layertools -jar build/libs/*.jar extract

FROM eclipse-temurin:25-jre-jammy AS final

# Install curl for healthcheck and create spring user in one layer
RUN apt-get update && \
  apt-get install -y curl && \
  rm -rf /var/lib/apt/lists/* && \
  addgroup --system spring && \
  adduser --system --ingroup spring spring

# Set the working directory
WORKDIR /app

# Copy extracted layers from builder (better Docker layer caching)
COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

# Set the user for running the application
USER spring

# Expose the port your Spring Boot application listens on (e.g., 8080)
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Define the entrypoint with optimized JVM settings for containers
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+UseG1GC", \
  "-XX:+OptimizeStringConcat", \
  "org.springframework.boot.loader.launch.JarLauncher"]
