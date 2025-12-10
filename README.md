# Star Wars Planets API

A production-ready RESTful API for managing Star Wars planets, built with Spring Boot 4, Java 25, and modern best practices.

## 🌟 Features

- **Full CRUD Operations** - Create, read, search, and delete planets
- **Pagination Support** - Efficient data retrieval with customizable page sizes
- **Redis Caching** - 10-minute TTL cache for improved performance
- **OpenAPI Documentation** - Interactive Swagger UI for API exploration
- **Observability Stack** - Prometheus metrics + Grafana dashboards
- **Load Testing** - Built-in Python-based load test tool
- **Docker Compose** - One-command deployment with all dependencies
- **Production Hardening** - Health checks, connection pooling, JVM optimization

## 🏗️ Architecture

### Tech Stack

- **Runtime:** Java 25
- **Framework:** Spring Boot 4.0.0
- **Database:** PostgreSQL 18.1
- **Cache:** Redis 8.4.0 (Lettuce client)
- **Data Mapping:** MapStruct 1.6.3
- **API Docs:** SpringDoc OpenAPI 2.8.0
- **Monitoring:** Spring Actuator + Micrometer + Prometheus + Grafana
- **Containerization:** Docker + Docker Compose

### Design Patterns

- **Layered Architecture:** Controller → Service → Repository
- **DTO Pattern:** Separate Request/Response DTOs
- **Repository Pattern:** Spring Data JPA
- **Global Exception Handling:** @ControllerAdvice
- **Caching Strategy:** @Cacheable / @CacheEvict annotations

## 🚀 Quick Start

### Prerequisites

- Docker & Docker Compose
- Git

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/mvibraim/java-star-wars-planets.git
cd java-star-wars-planets
```

2. **Configure environment variables**
```bash
# Copy the example .env file
cat > .env << 'EOF'
POSTGRES_DB=star-wars-planets-db
POSTGRES_USER=marcus
POSTGRES_PASSWORD=your_secure_password_here
EOF
```

3. **Start the application**
```bash
docker compose up -d
```

4. **Verify health**
```bash
# Wait for all services to be healthy (30-40 seconds)
docker compose ps

# Check application health
curl http://localhost:8080/actuator/health
```

### Access Points

- **API Base URL:** http://localhost:8080/v1
- **Swagger UI:** http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs
- **Actuator Metrics:** http://localhost:8080/actuator/prometheus
- **Prometheus:** http://localhost:9090
- **Grafana:** http://localhost:3000 (admin/admin123)

## 📖 API Endpoints

### Create Planet
```http
POST /v1/planets
Content-Type: application/json

{
  "name": "Tatooine",
  "terrain": "Desert",
  "climate": "Arid"
}
```

### List Planets (Paginated)
```http
GET /v1/planets?page=0&size=20
```

### Search Planet
```http
# By name
GET /v1/planets/search?name=Tatooine

# By UUID
GET /v1/planets/search?id=123e4567-e89b-12d3-a456-426614174000
```

### Delete Planet
```http
DELETE /v1/planets/{id}
```

## 🧪 Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Load Tests
```bash
# 10 requests/sec for 60 seconds
./load-test.sh 10 60

# 25 requests/sec for 120 seconds
./load-test.sh 25 120
```

Load test results are saved to `./load-test-results/load-test-results.csv`

See [LOAD_TESTING.md](LOAD_TESTING.md) for detailed usage.

### Check Metrics
```bash
./check-metrics.sh
```

## 📊 Monitoring

### Grafana Dashboard

Access Grafana at http://localhost:3000 (admin/admin123)

The pre-configured "Spring Boot 4.x System Monitor" dashboard includes:
- **JVM Memory Usage** - Heap utilization over time
- **HTTP Request Rate** - Requests per second
- **Live Threads** - Active thread count
- **Active DB Connections** - HikariCP pool usage

### Prometheus Metrics

View raw metrics:
```bash
curl http://localhost:8080/actuator/prometheus
```

Query specific metrics:
```bash
# HTTP request rate
curl 'http://localhost:9090/api/v1/query?query=rate(http_server_requests_seconds_count[1m])'

# JVM memory used
curl 'http://localhost:9090/api/v1/query?query=jvm_memory_used_bytes'
```

## 🔧 Configuration

### Application Properties

Key configurations in `src/main/resources/application.properties`:

- **Database Connection:** HikariCP with max 10 connections
- **Redis Cache:** 10-minute TTL, Lettuce connection pooling
- **JPA:** Schema validation mode (ddl-auto=validate)
- **Actuator:** Exposes /info, /metrics, /health, /prometheus endpoints
- **Logging:** INFO level for production

### Environment Variables

Configured via `.env` file:
- `POSTGRES_DB` - Database name
- `POSTGRES_USER` - Database username
- `POSTGRES_PASSWORD` - Database password

### Docker Compose

Services with health checks and dependencies:
- **app** - Spring Boot application (port 8080, 1GB memory limit)
- **postgres** - PostgreSQL database (internal)
- **redis** - Redis cache (internal)
- **prometheus** - Metrics aggregation (port 9090)
- **grafana** - Visualization dashboard (port 3000)

### JVM Configuration

The application is tuned for optimal performance with the following settings:

**Memory Allocation:**
- Container: 1GB limit, 512MB reserved
- Heap: 512MB initial → 768MB max (50-75% of container)
- Metaspace: 128MB initial → 256MB max (for Spring Boot classes)

**Garbage Collector (G1GC):**
- Max GC pause: 200ms (low-latency target)
- Heap region size: 8MB
- GC logging enabled with rotation (50MB total)

**Performance Optimizations:**
- String deduplication (reduces memory usage)
- Parallel reference processing
- Tiered compilation (fast startup + optimized hot paths)
- AlwaysPreTouch (consistent performance)

**View GC logs:**
```bash
# Real-time monitoring
docker exec star_wars_planets_app tail -f /app/logs/gc.log

# Copy to host
docker cp star_wars_planets_app:/app/logs/gc.log ./gc.log
```

See [JVM_TUNING.md](JVM_TUNING.md) for detailed configuration and tuning options.

## 🛠️ Development

### Build from Source
```bash
# Clean and build
./gradlew clean build

# Run locally (requires PostgreSQL and Redis)
./gradlew bootRun

# Create Docker image
./gradlew bootBuildImage
```

### Project Structure
```
src/
├── main/
│   ├── java/com/example/starwarsplanets/
│   │   ├── config/          # Redis, RestClient configuration
│   │   ├── controller/      # REST endpoints
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # JPA entities
│   │   ├── error/           # Error response models
│   │   ├── exception/       # Custom exceptions
│   │   ├── mapper/          # MapStruct mappers
│   │   ├── repository/      # Spring Data repositories
│   │   └── service/         # Business logic
│   └── resources/
│       └── application.properties
└── test/                    # Unit tests
```

### Database Schema

**Planets Table:**
- `id` - UUID (primary key)
- `name` - VARCHAR (unique, indexed)
- `terrain` - VARCHAR
- `climate` - VARCHAR
- `created_at` - TIMESTAMP (auto-generated)
- `updated_at` - TIMESTAMP (auto-updated)
- `movie_appearances` - INTEGER

## 🔗 Links

- **GitHub Repository:** https://github.com/mvibraim/java-star-wars-planets
- **API Documentation:** http://localhost:8080/swagger-ui/index.html
- **Load Testing Guide:** [LOAD_TESTING.md](LOAD_TESTING.md)

---

**Built with ❤️ using Spring Boot 4 and Java 25**
