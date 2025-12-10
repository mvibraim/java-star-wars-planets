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

# Create logs directory for GC logs
RUN mkdir -p /app/logs && chown spring:spring /app/logs

# Set the user for running the application
USER spring

# Expose the port your Spring Boot application listens on (e.g., 8080)
EXPOSE 8080

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Define the entrypoint with optimized JVM settings for containers
ENTRYPOINT ["java", \
  # Container support and memory settings
  "-XX:+UseContainerSupport", \
  "-XX:InitialRAMPercentage=50.0", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:MinRAMPercentage=50.0", \
  # G1GC tuning for low-latency
  "-XX:+UseG1GC", \
  "-XX:MaxGCPauseMillis=200", \
  "-XX:G1HeapRegionSize=8m", \
  "-XX:G1ReservePercent=10", \
  "-XX:InitiatingHeapOccupancyPercent=45", \
  # GC logging for monitoring
  "-Xlog:gc*:file=/app/logs/gc.log:time,level,tags:filecount=5,filesize=10M", \
  # Performance optimizations
  "-XX:+OptimizeStringConcat", \
  "-XX:+UseStringDeduplication", \
  "-XX:+ParallelRefProcEnabled", \
  "-XX:+AlwaysPreTouch", \
  # JIT compiler optimizations
  "-XX:+TieredCompilation", \
  "-XX:TieredStopAtLevel=1", \
  # Metaspace settings (for Spring Boot)
  "-XX:MetaspaceSize=128m", \
  "-XX:MaxMetaspaceSize=256m", \
  # Enable JMX for monitoring (if needed)
  "-Djava.rmi.server.hostname=localhost", \
  "-Dcom.sun.management.jmxremote=false", \
  "org.springframework.boot.loader.launch.JarLauncher"]
