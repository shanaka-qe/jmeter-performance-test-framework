# 🚀 JMeter Performance Test Framework

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![JMeter Version](https://img.shields.io/badge/JMeter-5.6.3-brightgreen.svg)](https://jmeter.apache.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

> **Code-first JMeter performance testing framework for multi-application testing**

**Author:** Shanaka Fernando  
**LinkedIn:** https://www.linkedin.com/in/shanaka-qe/

---

## 💡 About This Framework

This is a production-grade performance testing framework that I developed and successfully implemented with one of my previous clients to streamline their performance testing operations across multiple applications. The framework has been battle-tested in real-world scenarios and has significantly improved test execution efficiency, maintainability, and reporting capabilities.

I'm now sharing this framework with the community to help other QA engineers and performance testers build robust, scalable, and maintainable performance testing solutions. Whether you're starting from scratch or looking to improve your existing performance testing practices, this framework provides a solid foundation with industry best practices baked in.

---

## 📖 Overview

A comprehensive, code-first JMeter performance testing framework designed for real-world enterprise applications. This framework provides a complete solution for performance testing with modular components, CI/CD integration, and advanced reporting capabilities.

---

## 📋 Table of Contents

- [About This Framework](#-about-this-framework)
- [Overview](#-overview)
- [Features](#-features)
- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [Setup Guide](#-setup-guide)
- [Running Tests](#-running-tests)
- [CI/CD Integration](#-cicd-integration)
- [Reporting](#-reporting)
- [Quality Gates](#-quality-gates)
- [Documentation](#-documentation)
- [Contributing](#-contributing)
- [License](#-license)

---

## ✨ Features

### Core Capabilities
- **Environment Management**: Separate configuration files for dev, stage, and prod environments
- **Reusable Components**: Modular HTTP configs, headers, and extractors using Include Controller
- **Groovy Scripting**: Pre-built scripts for UUIDs, timestamps, token extraction, and data generation
- **Test Types**: Pre-configured smoke, load, stress, and soak test plans
- **Docker Support**: Complete containerized setup with JMeter, InfluxDB, and Grafana
- **CI/CD Ready**: GitHub Actions and Jenkins pipeline configurations included
- **Quality Gates**: Automated SLA validation with customizable thresholds
- **Real-time Monitoring**: InfluxDB + Grafana integration for live metrics visualization

### Best Practices
- ✅ Uses JSR223 Groovy processors for performance (no Beanshell)
- ✅ Cached compiled scripts for optimal execution
- ✅ JSON Path and Regex extractors for dynamic correlation
- ✅ Comprehensive assertions and response validations
- ✅ Modular and maintainable test structure
- ✅ Secrets management via CI/CD variables
- ✅ HTML reports generated automatically

---

## 📁 Project Structure

```
jmeter-performance-test-framework/
├── .github/
│   └── workflows/
│       └── performance-test.yml      # GitHub Actions workflow
├── ci/
│   └── Jenkinsfile                   # Jenkins pipeline configuration
├── docker/
│   ├── Dockerfile                    # JMeter Docker image
│   ├── docker-compose.yml            # Complete stack setup
│   ├── install-plugins.sh            # Plugin installation script
│   └── .dockerignore                 # Docker ignore file
├── docs/
│   ├── grafana/
│   │   ├── dashboards/               # Grafana dashboard templates
│   │   └── datasources/              # InfluxDB datasource config
│   ├── test-strategy.md              # Testing strategy and objectives
│   ├── modeling-and-scenarios.md     # Load modeling guide
│   ├── runbook.md                    # Execution runbook
│   ├── conventions.md                # Coding standards
│   └── contributing.md               # Contribution guidelines
├── env/
│   ├── dev.properties                # Development environment config
│   ├── stage.properties              # Staging environment config
│   ├── prod.properties               # Production environment config
│   └── smoke.properties              # Smoke test config
├── scripts/
│   ├── components/
│   │   ├── http-defaults.jmx         # HTTP request defaults
│   │   └── http-headers.jmx          # Common HTTP headers
│   ├── groovy/
│   │   ├── uuid-generator.groovy     # UUID generation
│   │   ├── timestamp-generator.groovy# Timestamp utilities
│   │   ├── token-extractor.groovy    # JWT/token extraction
│   │   ├── custom-logger.groovy      # Enhanced logging
│   │   ├── dynamic-correlation.groovy# Dynamic value extraction
│   │   └── random-data-generator.groovy # Test data generation
│   └── test-plans/
│       ├── smoke-test.jmx            # Smoke test plan
│       └── load-test.jmx             # Load test plan
├── data/
│   ├── test-users.csv                # Test user data
│   └── payloads/                     # JSON payload templates
├── utils/
│   ├── run-test.sh                   # Test execution script
│   └── quality-gates.py              # SLA validation script
├── results/                          # Test results (JTL files)
├── reports/                          # HTML reports
└── README.md                         # This file
```

---

## 🚀 Quick Start

### Prerequisites

- **Docker** (recommended) or **JMeter 5.6.3+**
- **Java 11+** (if running JMeter locally)
- **Python 3.11+** (for quality gates validation)

### Option 1: Docker Setup (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd jmeter-performance-test-framework
   ```

2. **Build and start services**
   ```bash
   docker-compose -f docker/docker-compose.yml up -d
   ```

3. **Run a smoke test**
   ```bash
   docker-compose -f docker/docker-compose.yml run --rm jmeter \
     jmeter -n -t /tests/scripts/test-plans/smoke-test.jmx \
     -Jenv=smoke -q /tests/env/smoke.properties \
     -l /tests/results/smoke-test.jtl \
     -e -o /tests/reports/smoke-test
   ```

4. **View results**
   - HTML Report: `reports/smoke-test/index.html`
   - Grafana Dashboard: http://localhost:3000 (admin/admin123)

### Option 2: Local Setup

1. **Install JMeter**
   ```bash
   wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.tgz
   tar -xzf apache-jmeter-5.6.3.tgz
   export JMETER_HOME=$(pwd)/apache-jmeter-5.6.3
   export PATH=$JMETER_HOME/bin:$PATH
   ```

2. **Run a test**
   ```bash
   ./utils/run-test.sh -t smoke -e dev
   ```

> 📖 **Need more detailed setup instructions?** See the complete [Setup Guide](docs/setup-guide.md) for step-by-step installation for all platforms.

---

## 📦 Setup Guide

For detailed installation and configuration instructions, please refer to the comprehensive [Setup Guide](docs/setup-guide.md), which includes:

- System requirements for different platforms
- Step-by-step installation for Windows, macOS, and Linux
- Docker setup and configuration
- IDE configuration (VS Code, IntelliJ)
- Environment configuration
- Troubleshooting common setup issues
- Verification steps

---

## 🧪 Running Tests

### Using the Convenience Script

The `run-test.sh` script simplifies test execution:

```bash
# Run smoke test on dev environment
./utils/run-test.sh -t smoke -e dev

# Run load test on stage with custom parameters
./utils/run-test.sh -t load -e stage -u 50 -d 600

# Options:
#   -t, --test-type       Test type: smoke, load, stress, soak
#   -e, --environment     Environment: dev, stage, prod
#   -u, --threads         Number of virtual users (override)
#   -d, --duration        Duration in seconds (override)
#   -r, --ramp-up         Ramp-up period in seconds (override)
```

### Direct JMeter Command

```bash
jmeter -n \
  -t scripts/test-plans/load-test.jmx \
  -q env/dev.properties \
  -Jthreads=20 \
  -Jduration=300 \
  -l results/test-results.jtl \
  -e -o reports/test-report
```

### Docker Command

```bash
docker-compose -f docker/docker-compose.yml run --rm jmeter \
  jmeter -n -t /tests/scripts/test-plans/load-test.jmx \
  -q /tests/env/dev.properties \
  -l /tests/results/load-test.jtl \
  -e -o /tests/reports/load-test
```

---

## 🔄 CI/CD Integration

### GitHub Actions

The framework includes a complete GitHub Actions workflow (`.github/workflows/performance-test.yml`) that:

1. Validates test plans
2. Runs smoke tests on every PR
3. Validates quality gates
4. Generates and archives HTML reports
5. Supports manual load test triggers

**Required Secrets:**
- `DEV_API_HOST`: Target host for dev environment
- `STAGE_API_HOST`: Target host for stage environment
- `API_KEY`: API key for authentication
- `AUTH_TOKEN`: Authorization token

### Jenkins

Use the included `ci/Jenkinsfile` for Jenkins pipelines:

1. Create a new Pipeline job
2. Point to the `ci/Jenkinsfile` in your repository
3. Configure required credentials in Jenkins
4. Run with parameters (test type, environment, threads, etc.)

---

## 📊 Reporting

### HTML Reports

JMeter generates comprehensive HTML reports automatically:
- Response time trends
- Throughput graphs
- Error rate analysis
- Percentile distributions

Reports are saved in the `reports/` directory.

### Grafana Dashboards

Real-time monitoring with Grafana:

1. Access Grafana: http://localhost:3000
2. Login: admin/admin123
3. View pre-configured JMeter dashboard

**Metrics Tracked:**
- p50, p95, p99 response times
- Error rate percentage
- Throughput (TPS)
- Active virtual users
- Success vs. error counts

### InfluxDB Backend

Enable InfluxDB backend listener in JMX files to stream metrics in real-time.

---

## ✅ Quality Gates

The framework includes automated quality gate validation:

```bash
python3 utils/quality-gates.py \
  --jtl-file results/test-results.jtl \
  --error-rate-threshold 2.0 \
  --p95-threshold 800 \
  --p99-threshold 1200 \
  --throughput-threshold 10
```

**Default Thresholds:**
- Error Rate: < 2%
- Average Response Time: < 500ms
- P95 Response Time: < 800ms
- P99 Response Time: < 1200ms
- Throughput: > 10 TPS

Customize thresholds based on your SLAs.

---

## 📚 Documentation

Comprehensive documentation is available in the `docs/` directory:

### Getting Started
- **[Setup Guide](docs/setup-guide.md)**: Complete installation and configuration guide for all platforms
- **[Quick Start & Runbook](docs/runbook.md)**: Step-by-step execution guide and troubleshooting

### Testing Strategy & Design
- **[Test Strategy](docs/test-strategy.md)**: Testing objectives, types, environments, and SLAs
- **[Modeling & Scenarios](docs/modeling-and-scenarios.md)**: Load modeling, ramp-up strategies, and user journeys

### Development & Contribution
- **[Conventions](docs/conventions.md)**: Coding standards and naming conventions
- **[Contributing Guide](docs/contributing.md)**: How to contribute to the framework

---

## 🤝 Contributing

We welcome contributions from the community! This framework is shared to help others, and your contributions can help make it even better.

Please see the **[Contributing Guide](docs/contributing.md)** for detailed information on:
- Setting up your development environment
- Branching strategy and workflow
- Code review process
- Pull request checklist and guidelines
- Testing requirements
- Coding standards and best practices

### Quick Contribution Steps

1. Fork the repository
2. Create a feature branch (`feature/your-feature-name`)
3. Make your changes following our [conventions](docs/conventions.md)
4. Test your changes thoroughly
5. Submit a pull request with a clear description

All contributions, whether bug fixes, new features, documentation improvements, or suggestions, are greatly appreciated! 🙏

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🆘 Support

For issues, questions, or feature requests:
- Open an issue on GitHub
- Contact: team@example.com

---

## 🎯 Test Type Quick Reference

| Type | Purpose | Duration | Users | Use Case |
|------|---------|----------|-------|----------|
| **Smoke** | Health check | < 5 min | 2-5 | Verify basic functionality |
| **Load** | Baseline performance | 30-60 min | 10-100 | Validate SLAs |
| **Stress** | Breakpoint discovery | 30-90 min | Increasing | Find capacity limits |
| **Soak** | Stability testing | 2-8 hours | Constant | Detect memory leaks |

---

© 2025 • SNK Media Pty Ltd — Designed for real-world performance engineering excellence.

