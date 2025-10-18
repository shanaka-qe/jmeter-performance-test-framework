# JMeter Performance Test Framework - Docker Image
# Base image: JMeter with plugins support
FROM justb4/jmeter:5.6.3

# Maintainer information
LABEL maintainer="SNK Media Pty Ltd"
LABEL description="JMeter performance testing framework with pre-installed plugins"
LABEL version="1.0.0"

# Set environment variables
ENV JMETER_HOME=/opt/apache-jmeter
ENV JMETER_BIN=${JMETER_HOME}/bin
ENV JMETER_PLUGINS=${JMETER_HOME}/lib/ext
ENV PATH=${JMETER_BIN}:${PATH}

# Switch to root user to install dependencies
USER root

# Install additional system dependencies if needed
# curl is needed for downloading plugins
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    curl \
    unzip \
    ca-certificates && \
    rm -rf /var/lib/apt/lists/*

# Create directories for test execution
RUN mkdir -p /tests/scripts \
    /tests/env \
    /tests/data \
    /tests/results \
    /tests/reports

# Copy the plugin installation script
COPY install-plugins.sh /tmp/install-plugins.sh
RUN chmod +x /tmp/install-plugins.sh

# Install JMeter plugins using the PluginsManager
# This installs commonly used plugins for performance testing
RUN /tmp/install-plugins.sh

# Set working directory
WORKDIR /tests

# Create a non-root user for running JMeter
RUN groupadd -r jmeter && \
    useradd -r -g jmeter -d /tests jmeter && \
    chown -R jmeter:jmeter /tests

# Switch to jmeter user
USER jmeter

# Default command: Show JMeter version and available options
CMD ["jmeter", "--version"]

# Health check: Verify JMeter can run
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD jmeter --version || exit 1

# Expose ports (if running JMeter in server mode)
# Port 1099 is for RMI registry
# Port 4445 is for distributed testing
EXPOSE 1099 4445

# Volume mount points for external data
VOLUME ["/tests/scripts", "/tests/env", "/tests/data", "/tests/results", "/tests/reports"]

# Documentation
# Usage:
#   Build: docker build -t jmeter-framework:latest -f docker/Dockerfile .
#   Run:   docker run -v $(pwd):/tests jmeter-framework:latest jmeter -n -t /tests/scripts/test-plans/smoke-test.jmx

