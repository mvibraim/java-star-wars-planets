# Load Testing Guide

This project includes an easy-to-use load testing script powered by Apache JMeter.

## Quick Start

### 1. Start the application
```bash
docker compose up -d
```

### 2. Run the load test
```bash
# Default: 10 requests/sec for 60 seconds
./load-test.sh

# Custom: 20 requests/sec for 120 seconds
./load-test.sh 20 120
```

The load test will automatically test **all API endpoints** in a round-robin fashion:
- `GET /v1/planets` (multiple pagination variations)
- `GET /v1/planets/search` (by planet name)
- `POST /v1/planets` (create new planets)

## Usage

```bash
./load-test.sh [rps] [duration]
```

### Parameters:
- **rps** (optional, default: 10) - Requests per second
- **duration** (optional, default: 60) - Duration in seconds

### Examples:

```bash
# Light load test (5 req/sec)
./load-test.sh 5 30

# Medium load test (10 req/sec)
./load-test.sh 10 60

# Heavy load test (25 req/sec)
./load-test.sh 25 120

# Stress test (50 req/sec)
./load-test.sh 50 300
```

## Understanding the Results

After the test completes, you'll see a summary with:

```
================================
Load Test Results Summary
================================
Total Requests: 600
Successful: 598
Failed: 2
Success Rate: 99.67%

Response Time Statistics (seconds):
  Minimum: 0.012s
  Average: 0.045s
  P50: 0.038s
  P95: 0.082s
  P99: 0.156s
  Maximum: 0.234s

Requests by Method:
  GET: 500 total, 500 success (100.0%)
  POST: 100 total, 98 success (98.0%)
```

### Key Metrics:
- **Total Requests** - Total number of requests sent
- **Successful** - Requests that returned 2xx/3xx status codes
- **Failed** - Requests that failed or timed out
- **Success Rate** - Percentage of successful requests
- **Response Times:**
  - **Minimum** - Fastest response
  - **Average** - Mean response time
  - **P50** - Median (50th percentile)
  - **P95** - 95th percentile (most requests are faster)
  - **P99** - 99th percentile (99% of requests are faster)
  - **Maximum** - Slowest response

### Results Files

Results are saved to `./load-test-results/load-test-results.csv` with columns:
- `timestamp` - When the request was sent
- `endpoint` - Which endpoint was tested (e.g., "GET /planets", "POST /planets")
- `method` - HTTP method (GET, POST, DELETE)
- `elapsed_ms` - Response time in milliseconds
- `status_code` - HTTP status code (200, 201, 404, etc.)
- `success` - true/false (was request successful)

### Analyzing Results

View the raw CSV:
```bash
cat ./load-test-results/load-test-results.csv
```

Count successful vs failed by method:
```bash
grep "GET.*true" ./load-test-results/load-test-results.csv | wc -l
grep "GET.*false" ./load-test-results/load-test-results.csv | wc -l
grep "POST.*true" ./load-test-results/load-test-results.csv | wc -l
```

Find slowest requests:
```bash
tail -50 ./load-test-results/load-test-results.csv | sort -t',' -k4 -nr | head -10
```

Average response time per endpoint:
```bash
awk -F',' 'NR>1 {sum[$2]+=$4; count[$2]++} END {for (ep in sum) print ep ": " int(sum[ep]/count[ep]) "ms"}' ./load-test-results/load-test-results.csv
```

## Monitoring During Load Test

While the test runs, you can monitor metrics in Grafana:

1. Open http://localhost:3000 (admin/admin123)
2. Navigate to "Spring Boot 4.x System Monitor" dashboard
3. Watch real-time metrics:
   - **JVM Memory Usage** - How much heap is being used
   - **HTTP Request Rate** - Requests per second
   - **HTTP P95 Latency** - 95th percentile response time
   - **Live Threads** - Number of active threads
   - **Active DB Connections** - Connection pool usage

## Monitoring Prometheus Metrics

View raw metrics from the application:
```bash
# Current metrics
curl -s http://localhost:8080/actuator/prometheus | grep http_server_requests

# Query specific metric
curl -s http://localhost:9090/api/v1/query?query=http_server_requests_seconds_count
```

## Performance Baseline

Recommended starting points for load testing:

| Scenario | RPS | Duration | Use Case |
|----------|-----|----------|----------|
| Smoke Test | 1 | 10s | Verify API is responding |
| Light Load | 5 | 30s | Normal daytime traffic |
| Moderate Load | 10 | 60s | Peak hour traffic |
| Heavy Load | 25 | 120s | Stress testing |
| Extreme Load | 50+ | 300s | Breaking point testing |

## Troubleshooting

### Docker network not found
If you get "Docker network not found" error:
```bash
# Make sure Docker Compose is running
docker compose ps

# Run again
./load-test.sh 10 60
```

### Port already in use
If port 8080 is already in use:
```bash
# Check what's using the port
lsof -i :8080

# Or specify different host/port
HOST=127.0.0.1 PORT=8080 ./load-test.sh 10 60
```

### JMeter Docker image not found
```bash
# Pull the JMeter image
docker pull justb4/jmeter:latest

# Run the test again
./load-test.sh 10 60
```

## Advanced Usage

### Custom test plan (manual)
Edit `load-test.jmx` for custom test scenarios (requires JMeter knowledge).

### Remote testing
To test a remote API:
```bash
HOST=api.example.com PORT=443 ./load-test.sh 10 60
```

### Combining with caching tests
Monitor Redis cache hit rates while running load tests:
```bash
# In another terminal, watch Redis stats
watch -n 1 'redis-cli INFO stats'

# Then run the test
./load-test.sh 10 60
```

## Analyzing Performance

After testing, use these queries to understand performance:

### Response time distribution (JMeter CSV format)
```bash
# P50 (median) response time
awk -F',' 'NR>1 {print $3}' /tmp/load-test-results.csv | sort -n | awk '{a[NR]=$1} END {print a[int(NR*0.5)]}'

# P95 response time
awk -F',' 'NR>1 {print $3}' /tmp/load-test-results.csv | sort -n | awk '{a[NR]=$1} END {print a[int(NR*0.95)]}'

# P99 response time
awk -F',' 'NR>1 {print $3}' /tmp/load-test-results.csv | sort -n | awk '{a[NR]=$1} END {print a[int(NR*0.99)]}'
```

### Error analysis
```bash
# Count errors by response code
awk -F',' 'NR>1 && $5!=200 {print $5}' /tmp/load-test-results.csv | sort | uniq -c
```
