# JVM Tuning Configuration

## Overview

The application is now configured with optimized JVM settings for a REST API workload with caching, database connections, and moderate traffic.

## Container Resources

**Docker Compose Limits:**
- **Memory:** 1GB limit, 512MB reserved
- **CPU:** 2 cores limit, 0.5 core reserved

## JVM Heap Settings

### Memory Allocation
- `InitialRAMPercentage=50.0` - Start with 512MB heap (50% of 1GB)
- `MaxRAMPercentage=75.0` - Maximum 768MB heap (75% of 1GB)
- `MinRAMPercentage=50.0` - Minimum 512MB heap

**Why these values?**
- Reserves 256MB for non-heap memory (Metaspace, native memory, thread stacks)
- Provides headroom for GC and prevents OOMKiller
- Balances startup time with production capacity

### Garbage Collector (G1GC)

**G1 Garbage Collector Configuration:**
- `MaxGCPauseMillis=200` - Target max GC pause of 200ms for low latency
- `G1HeapRegionSize=8m` - 8MB regions (good for 512-768MB heap)
- `G1ReservePercent=10` - Reserve 10% heap for to-space promotion
- `InitiatingHeapOccupancyPercent=45` - Start concurrent GC at 45% heap usage

**Benefits:**
- Low-latency pauses suitable for REST API
- Predictable GC behavior
- Good throughput for moderate traffic

### Metaspace (Class Metadata)

- `MetaspaceSize=128m` - Initial Metaspace allocation
- `MaxMetaspaceSize=256m` - Maximum Metaspace size

**Why?**
- Spring Boot applications load many classes (Spring framework, libraries)
- 128MB initial prevents early resizing overhead
- 256MB maximum is sufficient for most Spring Boot apps

### Performance Optimizations

**String Handling:**
- `OptimizeStringConcat` - JIT optimizes string concatenation
- `UseStringDeduplication` - Deduplicates identical String objects (saves heap)

**GC Efficiency:**
- `ParallelRefProcEnabled` - Process references in parallel during GC
- `AlwaysPreTouch` - Touch all heap pages at startup (consistent performance)

**JIT Compilation:**
- `TieredCompilation` - Uses both C1 and C2 compilers
- `TieredStopAtLevel=1` - Quick startup, optimize hot paths later

## GC Logging

**Log Configuration:**
```
-Xlog:gc*:file=/app/logs/gc.log:time,level,tags:filecount=5,filesize=10M
```

- **Location:** `/app/logs/gc.log`
- **Rotation:** 5 files × 10MB = 50MB total
- **Format:** Timestamped with log levels and tags

**Viewing GC logs:**
```bash
# From container
docker exec star_wars_planets_app cat /app/logs/gc.log

# Copy to host
docker cp star_wars_planets_app:/app/logs/gc.log ./gc.log

# Real-time monitoring
docker exec star_wars_planets_app tail -f /app/logs/gc.log
```

## Monitoring JVM Metrics

### Via Prometheus/Grafana

Access the Grafana dashboard at http://localhost:3000 to view:
- JVM memory usage (heap, non-heap)
- GC count and duration
- Thread count
- CPU usage

### Via Actuator Endpoint

```bash
# JVM memory metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# GC metrics
curl http://localhost:8080/actuator/metrics/jvm.gc.pause

# Thread metrics
curl http://localhost:8080/actuator/metrics/jvm.threads.live
```

### Via JVM Tools

**jstat (inside container):**
```bash
docker exec star_wars_planets_app jstat -gc 1 5000
```

**VisualVM (remote connection):**
```bash
# Add to Dockerfile ENTRYPOINT if needed:
-Dcom.sun.management.jmxremote=true
-Dcom.sun.management.jmxremote.port=9010
-Dcom.sun.management.jmxremote.authenticate=false
-Dcom.sun.management.jmxremote.ssl=false
```

## Tuning for Different Workloads

### High Traffic (increase memory)

Update `docker-compose.yaml`:
```yaml
deploy:
  resources:
    limits:
      memory: 2G  # Increase to 2GB
```

JVM will automatically adjust:
- Max heap: 1.5GB (75% of 2GB)
- Initial heap: 1GB (50% of 2GB)

### Low Latency Priority

Reduce GC pause target in Dockerfile:
```dockerfile
-XX:MaxGCPauseMillis=100  # Target 100ms instead of 200ms
```

### High Throughput Priority

Switch to Parallel GC in Dockerfile:
```dockerfile
-XX:+UseParallelGC
-XX:ParallelGCThreads=2
```

## Troubleshooting

### OutOfMemoryError

1. **Check current memory usage:**
```bash
docker stats star_wars_planets_app
```

2. **Review GC logs:**
```bash
docker exec star_wars_planets_app tail -100 /app/logs/gc.log
```

3. **Increase memory limit** in `docker-compose.yaml`

### High GC Overhead

**Symptoms:** GC pause times > 500ms, frequent collections

**Solutions:**
- Increase heap size (raise memory limits)
- Reduce `MaxGCPauseMillis` target
- Check for memory leaks via heap dump

**Create heap dump:**
```bash
docker exec star_wars_planets_app jmap -dump:live,format=b,file=/app/logs/heap.hprof 1
docker cp star_wars_planets_app:/app/logs/heap.hprof ./heap.hprof
```

### Slow Startup

**Reduce initial compilation level:**
```dockerfile
-XX:TieredStopAtLevel=1  # Already configured
```

**Or remove AlwaysPreTouch for faster startup:**
```dockerfile
# Comment out: -XX:+AlwaysPreTouch
```

## Performance Baselines

Expected performance metrics with current configuration:

- **Startup Time:** 15-25 seconds
- **Memory Usage:** 400-600MB steady state
- **GC Pause Time:** <200ms (P99)
- **Throughput:** 500+ req/sec (with caching)
- **Response Time:** <50ms (P95, cached requests)

## References

- [JVM Tuning Guide](https://docs.oracle.com/en/java/javase/21/gctuning/)
- [G1GC Documentation](https://www.oracle.com/technical-resources/articles/java/g1gc.html)
- [Spring Boot Performance](https://spring.io/blog/2015/12/10/spring-boot-memory-performance)
