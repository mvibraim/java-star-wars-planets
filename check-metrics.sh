#!/bin/bash

# Check available metrics from Spring Boot actuator

echo "================================"
echo "Checking Available Metrics"
echo "================================"
echo ""

echo "Testing Prometheus endpoint..."
if curl -s http://localhost:8080/actuator/prometheus > /dev/null 2>&1; then
    echo "✓ Prometheus endpoint is accessible"
else
    echo "✗ Prometheus endpoint not accessible"
    exit 1
fi

echo ""
echo "Looking for HikariCP metrics..."
HIKARI_COUNT=$(curl -s http://localhost:8080/actuator/prometheus | grep -c "hikaricp" || echo "0")
if [ "$HIKARI_COUNT" -gt 0 ]; then
    echo "✓ Found $HIKARI_COUNT HikariCP metric(s)"
    curl -s http://localhost:8080/actuator/prometheus | grep "hikaricp" | head -10
else
    echo "✗ No HikariCP metrics found"
fi

echo ""
echo "Looking for HTTP metrics..."
HTTP_COUNT=$(curl -s http://localhost:8080/actuator/prometheus | grep -c "http_server_requests" || echo "0")
if [ "$HTTP_COUNT" -gt 0 ]; then
    echo "✓ Found $HTTP_COUNT HTTP metric(s)"
    curl -s http://localhost:8080/actuator/prometheus | grep "http_server_requests" | head -10
else
    echo "✗ No HTTP metrics found"
fi

echo ""
echo "Looking for JVM metrics..."
JVM_COUNT=$(curl -s http://localhost:8080/actuator/prometheus | grep -c "jvm_" || echo "0")
if [ "$JVM_COUNT" -gt 0 ]; then
    echo "✓ Found $JVM_COUNT JVM metric(s)"
    curl -s http://localhost:8080/actuator/prometheus | grep "jvm_memory_used" | head -5
else
    echo "✗ No JVM metrics found"
fi

echo ""
echo "================================"
echo "Checking Prometheus connectivity..."
echo "================================"
if curl -s http://localhost:9090/api/v1/query?query=up > /dev/null 2>&1; then
    echo "✓ Prometheus is accessible"
    # Check if app is being scraped
    SCRAPE_COUNT=$(curl -s http://localhost:9090/api/v1/query?query=up | grep -c "spring-boot-app" || echo "0")
    if [ "$SCRAPE_COUNT" -gt 0 ]; then
        echo "✓ Spring Boot app is being scraped by Prometheus"
    else
        echo "⚠ Spring Boot app may not be in Prometheus targets"
    fi
else
    echo "✗ Prometheus not accessible"
fi

echo ""
echo "Checking Grafana..."
if curl -s http://localhost:3000/api/datasources > /dev/null 2>&1; then
    echo "✓ Grafana is accessible"
else
    echo "✗ Grafana not accessible"
fi
