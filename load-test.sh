#!/bin/bash

# Load Test Script for Star Wars Planets API
# Usage: ./load-test.sh [rps] [duration]
# Example: ./load-test.sh 10 60

set -e

# Default values
RPS=${1:-10}              # Requests per second
DURATION=${2:-60}         # Duration in seconds
HOST=${HOST:-localhost}
PORT=${PORT:-8080}

# Calculate total requests
TOTAL_REQUESTS=$((RPS * DURATION))

echo "================================"
echo "Star Wars Planets API Load Test"
echo "================================"
echo "Target: http://${HOST}:${PORT}/v1/"
echo "RPS: ${RPS} requests/sec"
echo "Duration: ${DURATION} seconds"
echo "Total Requests: ~${TOTAL_REQUESTS}"
echo "Endpoints: GET /planets, POST /planets, GET /planets/search"
echo "================================"
echo ""

# Create results directory
RESULTS_DIR="./load-test-results"
mkdir -p "$RESULTS_DIR"

# Create Python load test script
LOAD_TEST_SCRIPT=$(mktemp --suffix=.py)
trap "rm -f $LOAD_TEST_SCRIPT" EXIT

cat > "$LOAD_TEST_SCRIPT" << 'PYTHON_SCRIPT'
#!/usr/bin/env python3
import sys
import time
import requests
import random
import uuid
from datetime import datetime
from concurrent.futures import ThreadPoolExecutor

def make_request(url, method='GET', data=None):
    """Make a single HTTP request and return timing"""
    start = time.time()
    try:
        if method == 'POST':
            response = requests.post(url, json=data, timeout=30)
        elif method == 'DELETE':
            response = requests.delete(url, timeout=30)
        else:  # GET
            response = requests.get(url, timeout=30)
        elapsed = time.time() - start
        success = 200 <= response.status_code < 300
        return elapsed, response.status_code, success, method
    except Exception as e:
        elapsed = time.time() - start
        return elapsed, 0, False, method

def get_test_endpoints(base_url):
    """Get all test endpoints with different variations"""
    endpoints = [
        {'url': f'{base_url}/v1/planets', 'method': 'GET', 'name': 'GET /planets'},
        {'url': f'{base_url}/v1/planets?page=0&size=20', 'method': 'GET', 'name': 'GET /planets (page 0)'},
        {'url': f'{base_url}/v1/planets?page=1&size=50', 'method': 'GET', 'name': 'GET /planets (page 1)'},
        {'url': f'{base_url}/v1/planets/search?name=Tatooine', 'method': 'GET', 'name': 'GET /search (Tatooine)'},
        {'url': f'{base_url}/v1/planets/search?name=Alderaan', 'method': 'GET', 'name': 'GET /search (Alderaan)'},
        {'url': f'{base_url}/v1/planets', 'method': 'POST', 'data': {
            'name': f'TestPlanet{int(time.time() * 1000) % 100000}',
            'terrain': random.choice(['Desert', 'Forest', 'Ocean', 'Mountain']),
            'climate': random.choice(['Arid', 'Humid', 'Cold', 'Temperate'])
        }, 'name': 'POST /planets'},
    ]
    return endpoints

def load_test(base_url, rps, duration, results_file):
    """Run load test across all API endpoints"""
    start_time = time.time()
    end_time = start_time + duration
    request_count = 0
    success_count = 0
    error_count = 0
    response_times = []
    method_stats = {}
    
    endpoints = get_test_endpoints(base_url)
    endpoint_index = 0
    
    with open(results_file, 'w') as f:
        f.write("timestamp,endpoint,method,elapsed_ms,status_code,success\n")
        
        with ThreadPoolExecutor(max_workers=10) as executor:
            futures = []
            
            while time.time() < end_time:
                endpoint = endpoints[endpoint_index % len(endpoints)]
                endpoint_index += 1
                
                if endpoint['method'] == 'POST':
                    future = executor.submit(make_request, endpoint['url'], 'POST', endpoint['data'])
                elif endpoint['method'] == 'DELETE':
                    future = executor.submit(make_request, endpoint['url'], 'DELETE')
                else:
                    future = executor.submit(make_request, endpoint['url'], 'GET')
                
                futures.append((future, endpoint))
                
                for i in range(len(futures)-1, -1, -1):
                    future, ep = futures[i]
                    if future.done():
                        try:
                            elapsed, status, success, method = future.result()
                            request_count += 1
                            
                            if method not in method_stats:
                                method_stats[method] = {'count': 0, 'success': 0}
                            method_stats[method]['count'] += 1
                            
                            if success:
                                success_count += 1
                                method_stats[method]['success'] += 1
                            else:
                                error_count += 1
                            
                            response_times.append(elapsed)
                            
                            timestamp = datetime.now().isoformat()
                            f.write(f"{timestamp},{ep['name']},{ep['method']},{int(elapsed*1000)},{status},{success}\n")
                            f.flush()
                        except Exception as e:
                            error_count += 1
                        
                        futures.pop(i)
                
                time.sleep(1.0 / rps)
    
    if response_times:
        response_times.sort()
        avg_response = sum(response_times) / len(response_times)
        min_response = min(response_times)
        max_response = max(response_times)
        p50 = response_times[len(response_times)//2]
        p95 = response_times[int(len(response_times)*0.95)] if len(response_times) > 20 else response_times[-1]
        p99 = response_times[int(len(response_times)*0.99)] if len(response_times) > 100 else response_times[-1]
    else:
        avg_response = min_response = max_response = p50 = p95 = p99 = 0
    
    return {
        'total_requests': request_count,
        'success_count': success_count,
        'error_count': error_count,
        'avg_response': avg_response,
        'min_response': min_response,
        'max_response': max_response,
        'p50': p50,
        'p95': p95,
        'p99': p99,
        'method_stats': method_stats
    }

if __name__ == '__main__':
    base_url = sys.argv[1]
    rps = int(sys.argv[2])
    duration = int(sys.argv[3])
    results_file = sys.argv[4]
    
    results = load_test(base_url, rps, duration, results_file)
    
    print("================================")
    print("Load Test Results Summary")
    print("================================")
    print(f"Total Requests: {results['total_requests']}")
    print(f"Successful: {results['success_count']}")
    print(f"Failed: {results['error_count']}")
    if results['total_requests'] > 0:
        print(f"Success Rate: {(results['success_count']/results['total_requests']*100):.2f}%")
    print()
    print("Response Time Statistics (seconds):")
    print(f"  Minimum: {results['min_response']:.3f}s")
    print(f"  Average: {results['avg_response']:.3f}s")
    print(f"  P50: {results['p50']:.3f}s")
    print(f"  P95: {results['p95']:.3f}s")
    print(f"  P99: {results['p99']:.3f}s")
    print(f"  Maximum: {results['max_response']:.3f}s")
    print()
    print("Requests by Method:")
    for method, stats in sorted(results['method_stats'].items()):
        success_rate = (stats['success'] / stats['count'] * 100) if stats['count'] > 0 else 0
        print(f"  {method}: {stats['count']} total, {stats['success']} success ({success_rate:.1f}%)")
PYTHON_SCRIPT

echo "Starting load test..."
echo ""

# Create results directory
RESULTS_DIR="./load-test-results"
mkdir -p "$RESULTS_DIR"

# Run using Python
if command -v python3 &> /dev/null; then
    # Run locally if Python is available
    python3 "$LOAD_TEST_SCRIPT" "http://${HOST}:${PORT}" "$RPS" "$DURATION" "$RESULTS_DIR/load-test-results.csv"
else
    # Run in Docker
    docker run --rm \
      --network startwarsplanets_app-network \
      -v "$(pwd)/$RESULTS_DIR:/results" \
      python:3.11-slim \
      bash -c "pip install requests -q && python3 -c \"$(cat $LOAD_TEST_SCRIPT)\" http://${HOST}:${PORT} $RPS $DURATION /results/load-test-results.csv"
fi

echo ""
echo "✓ Load test completed!"
echo ""
echo "Results saved to: $RESULTS_DIR/load-test-results.csv"
echo ""
echo "View detailed results:"
echo "  cat $RESULTS_DIR/load-test-results.csv"
echo ""
echo "Analyze results by method:"
echo "  grep 'GET' $RESULTS_DIR/load-test-results.csv | wc -l"
echo "  grep 'POST' $RESULTS_DIR/load-test-results.csv | wc -l"echo "Starting load test..."
echo ""

# Run using Python in Docker or locally
if command -v python3 &> /dev/null; then
    # Run locally if Python is available
    python3 "$LOAD_TEST_SCRIPT" "http://${HOST}:${PORT}${ENDPOINT}" "$RPS" "$DURATION" "$RESULTS_DIR/load-test-results.csv"
else
    # Run in Docker
    docker run --rm \
      --network startwarsplanets_app-network \
      -v "$(pwd)/$RESULTS_DIR:/results" \
      python:3.11-slim \
      bash -c "pip install requests -q && python3 -c \"$(cat $LOAD_TEST_SCRIPT)\" http://${HOST}:${PORT}${ENDPOINT} $RPS $DURATION /results/load-test-results.csv"
fi

echo ""
echo "✓ Load test completed!"
echo ""
echo "Results saved to: $RESULTS_DIR/load-test-results.csv"
echo ""
echo "View detailed results:"
echo "  cat $RESULTS_DIR/load-test-results.csv"
