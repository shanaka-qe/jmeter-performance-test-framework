# 📖 Performance Testing Runbook

This runbook provides step-by-step instructions for executing performance tests locally and in CI/CD pipelines.

---

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Local Execution](#local-execution)
3. [Docker Execution](#docker-execution)
4. [CI/CD Execution](#cicd-execution)
5. [Troubleshooting](#troubleshooting)
6. [Best Practices](#best-practices)

---

## Prerequisites

### Required Software

**For Local Execution:**
- Java 11 or higher
- Apache JMeter 5.6.3+
- Python 3.11+ (for quality gates)
- Git

**For Docker Execution:**
- Docker 20.10+
- Docker Compose 2.0+

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd jmeter-performance-test-framework
   ```

2. **Set environment variables** (for local execution)
   ```bash
   export JMETER_HOME=/path/to/apache-jmeter
   export PATH=$JMETER_HOME/bin:$PATH
   ```

3. **Install Python dependencies** (for quality gates)
   ```bash
   pip install pandas numpy
   ```

---

## Local Execution

### Method 1: Using the Convenience Script

The `run-test.sh` script is the easiest way to execute tests locally.

#### Run a Smoke Test

```bash
./utils/run-test.sh -t smoke -e dev
```

#### Run a Load Test

```bash
./utils/run-test.sh -t load -e stage
```

#### Run with Custom Parameters

```bash
./utils/run-test.sh -t load -e dev -u 50 -d 600 -r 120
```

**Parameters:**
- `-t, --test-type`: Test type (smoke, load, stress, soak)
- `-e, --environment`: Target environment (dev, stage, prod)
- `-u, --threads`: Number of virtual users (overrides environment config)
- `-d, --duration`: Test duration in seconds
- `-r, --ramp-up`: Ramp-up period in seconds

---

### Method 2: Direct JMeter Command

For more control, use JMeter directly:

#### Smoke Test

```bash
jmeter -n \
  -t scripts/test-plans/smoke-test.jmx \
  -q env/smoke.properties \
  -l results/smoke-test.jtl \
  -j results/smoke-test.log \
  -e -o reports/smoke-test
```

#### Load Test with Overrides

```bash
jmeter -n \
  -t scripts/test-plans/load-test.jmx \
  -q env/dev.properties \
  -Jtarget.host=dev-api.example.com \
  -Jthreads=20 \
  -Jduration=300 \
  -Jramp.up=60 \
  -l results/load-test.jtl \
  -j results/load-test.log \
  -e -o reports/load-test
```

**JMeter CLI Options:**
- `-n`: Non-GUI mode (required for headless execution)
- `-t`: Path to test plan (JMX file)
- `-q`: Properties file to load
- `-J`: Override property values
- `-l`: Path to result file (JTL)
- `-j`: Path to log file
- `-e`: Generate HTML report
- `-o`: Output directory for HTML report

---

### Method 3: Validate Quality Gates

After test execution, validate results against quality gates:

```bash
python3 utils/quality-gates.py \
  --jtl-file results/load-test.jtl \
  --error-rate-threshold 2.0 \
  --p95-threshold 800 \
  --p99-threshold 1200 \
  --throughput-threshold 10
```

**Quality Gate Parameters:**
- `--jtl-file`: Path to JTL result file (required)
- `--error-rate-threshold`: Max error rate % (default: 2.0)
- `--p95-threshold`: Max p95 response time in ms (default: 800)
- `--p99-threshold`: Max p99 response time in ms (default: 1200)
- `--throughput-threshold`: Min throughput in TPS (default: 10.0)
- `--avg-response-threshold`: Max avg response time in ms (default: 500)

---

## Docker Execution

### Setup

1. **Build the Docker image**
   ```bash
   cd docker
   docker build -t jmeter-framework:latest -f Dockerfile ..
   ```

2. **Start all services** (JMeter, InfluxDB, Grafana)
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

3. **Verify services are running**
   ```bash
   docker-compose -f docker/docker-compose.yml ps
   ```

---

### Running Tests in Docker

#### Smoke Test

```bash
docker-compose -f docker/docker-compose.yml run --rm jmeter \
  jmeter -n \
  -t /tests/scripts/test-plans/smoke-test.jmx \
  -q /tests/env/smoke.properties \
  -l /tests/results/smoke-test.jtl \
  -e -o /tests/reports/smoke-test
```

#### Load Test

```bash
docker-compose -f docker/docker-compose.yml run --rm jmeter \
  jmeter -n \
  -t /tests/scripts/test-plans/load-test.jmx \
  -q /tests/env/dev.properties \
  -Jthreads=20 \
  -Jduration=300 \
  -l /tests/results/load-test.jtl \
  -e -o /tests/reports/load-test
```

---

### Viewing Results

#### HTML Reports

```bash
# Results are in the reports/ directory on your host machine
open reports/smoke-test/index.html
```

#### Grafana Dashboard

1. Open browser to http://localhost:3000
2. Login with username: `admin`, password: `admin123`
3. Navigate to the JMeter Performance Dashboard
4. View real-time metrics during test execution

#### InfluxDB Metrics

```bash
# Access InfluxDB CLI
docker exec -it jmeter-influxdb influx

# Query JMeter database
> USE jmeter
> SHOW MEASUREMENTS
> SELECT * FROM jmeter LIMIT 10
```

---

### Cleanup

```bash
# Stop all services
docker-compose -f docker/docker-compose.yml down

# Remove volumes (deletes all data)
docker-compose -f docker/docker-compose.yml down -v
```

---

## CI/CD Execution

### GitHub Actions

Tests run automatically on:
- Pull requests to main/master
- Push to main/master
- Manual workflow dispatch

#### Trigger Manual Test

1. Go to GitHub repository
2. Navigate to **Actions** tab
3. Select **Performance Test Pipeline**
4. Click **Run workflow**
5. Select test type and environment
6. Click **Run workflow**

#### View Results

1. Go to the workflow run
2. Check **Summary** for quality gate results
3. Download artifacts:
   - `smoke-test-results`: JTL and log files
   - `smoke-test-report`: HTML report

---

### Jenkins

#### Run Pipeline

1. Open Jenkins
2. Select the performance test job
3. Click **Build with Parameters**
4. Select:
   - **TEST_TYPE**: smoke, load, stress, or soak
   - **ENVIRONMENT**: dev, stage, or prod
   - **THREADS**: (optional) Override thread count
   - **DURATION**: (optional) Override duration
5. Click **Build**

#### View Results

1. Click on the build number
2. View **Console Output** for execution logs
3. Open **Performance Test Report** for HTML report
4. Download **Artifacts** for JTL files

---

## Troubleshooting

### Common Issues

#### Issue 1: JMeter Not Found

**Symptom**: `jmeter: command not found`

**Solution**:
```bash
export JMETER_HOME=/path/to/apache-jmeter
export PATH=$JMETER_HOME/bin:$PATH
```

---

#### Issue 2: Permission Denied

**Symptom**: `Permission denied: ./utils/run-test.sh`

**Solution**:
```bash
chmod +x utils/run-test.sh
```

---

#### Issue 3: Connection Refused

**Symptom**: `Connection refused` errors in JMeter

**Solution**:
1. Verify target host is reachable
2. Check firewall settings
3. Ensure correct host/port in environment properties
4. Check API is running

---

#### Issue 4: Out of Memory

**Symptom**: `java.lang.OutOfMemoryError: Java heap space`

**Solution**:
1. Increase JVM heap size:
   ```bash
   export JVM_ARGS="-Xms512m -Xmx4096m"
   ```
2. Or edit `jmeter.bat` / `jmeter.sh` and modify:
   ```bash
   HEAP="-Xms1g -Xmx4g"
   ```

---

#### Issue 5: Docker Container Fails

**Symptom**: Container exits immediately

**Solution**:
```bash
# Check logs
docker-compose -f docker/docker-compose.yml logs jmeter

# Rebuild image
docker-compose -f docker/docker-compose.yml build --no-cache jmeter
```

---

#### Issue 6: Grafana Not Showing Data

**Symptom**: Empty Grafana dashboard

**Solution**:
1. Enable InfluxDB backend listener in JMX file
2. Verify InfluxDB is running:
   ```bash
   docker-compose -f docker/docker-compose.yml ps influxdb
   ```
3. Check InfluxDB has data:
   ```bash
   docker exec -it jmeter-influxdb influx -execute "SHOW DATABASES"
   ```

---

### Debug Mode

Enable detailed logging for troubleshooting:

```bash
jmeter -n \
  -t scripts/test-plans/smoke-test.jmx \
  -q env/smoke.properties \
  -l results/debug-test.jtl \
  -j results/debug-test.log \
  -Jlog.level=DEBUG \
  -e -o reports/debug-test
```

---

## Best Practices

### 1. Test Execution

✅ **Do:**
- Always run smoke test before larger tests
- Use descriptive names for result files (include timestamp)
- Archive results for historical comparison
- Run multiple iterations for consistency

❌ **Don't:**
- Run load tests on production without approval
- Execute tests during peak business hours
- Commit result files or reports to Git
- Ignore warning messages in logs

---

### 2. Result Analysis

✅ **Do:**
- Review HTML reports thoroughly
- Check quality gate results
- Compare trends over time
- Investigate all failures

❌ **Don't:**
- Rely solely on average metrics
- Ignore outliers in response times
- Skip error analysis
- Deploy without passing quality gates

---

### 3. Maintenance

✅ **Do:**
- Update test data regularly
- Review and adjust SLA thresholds
- Keep JMeter and plugins updated
- Document test changes

❌ **Don't:**
- Use outdated test scenarios
- Hard-code sensitive credentials
- Leave test environment misconfigured
- Skip documentation updates

---

### 4. Reporting

✅ **Do:**
- Generate reports after every test
- Share results with stakeholders
- Archive reports for compliance
- Track performance trends

❌ **Don't:**
- Delete historical results
- Share incomplete analysis
- Ignore trend degradation
- Miss reporting deadlines

---

## Quick Reference

### Common Commands

```bash
# Smoke test (quick)
./utils/run-test.sh -t smoke -e dev

# Load test (standard)
./utils/run-test.sh -t load -e stage

# Load test (custom)
./utils/run-test.sh -t load -e dev -u 100 -d 1800

# Validate quality gates
python3 utils/quality-gates.py --jtl-file results/test.jtl

# View JMeter version
jmeter --version

# Start Docker stack
docker-compose -f docker/docker-compose.yml up -d

# View Docker logs
docker-compose -f docker/docker-compose.yml logs -f jmeter
```

---

## Support

For additional help:
- Review documentation in `docs/` directory
- Check GitHub issues
- Contact performance engineering team

---

**Document Version**: 1.0  
**Last Updated**: 2025-10-18  
**Maintained By**: Performance Engineering Team

