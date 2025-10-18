# 🔧 Setup Guide

Complete setup instructions for the JMeter Performance Test Framework. Follow these steps to get the framework up and running in your environment.

---

## Table of Contents

1. [System Requirements](#system-requirements)
2. [Local Setup](#local-setup)
3. [Docker Setup](#docker-setup)
4. [IDE Configuration](#ide-configuration)
5. [Verification](#verification)
6. [Troubleshooting](#troubleshooting)

---

## System Requirements

### Hardware Requirements

**Minimum:**
- CPU: 2 cores
- RAM: 4 GB
- Disk Space: 2 GB

**Recommended:**
- CPU: 4+ cores
- RAM: 8+ GB
- Disk Space: 10 GB

### Software Requirements

**For Local Execution:**
- **Operating System**: Windows 10+, macOS 10.15+, or Linux (Ubuntu 20.04+)
- **Java**: JDK 11 or higher (JDK 17 recommended)
- **Apache JMeter**: Version 5.6.3 or higher
- **Python**: Version 3.11 or higher
- **Git**: Latest version

**For Docker Execution:**
- **Docker**: Version 20.10 or higher
- **Docker Compose**: Version 2.0 or higher

---

## Local Setup

### Step 1: Install Java

#### macOS (using Homebrew)

```bash
# Install Homebrew (if not already installed)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Java
brew install openjdk@11

# Add Java to PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@11/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
java -version
```

#### Ubuntu/Debian Linux

```bash
# Update package list
sudo apt update

# Install Java
sudo apt install openjdk-11-jdk -y

# Verify installation
java -version
```

#### Windows

1. Download JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [Adoptium](https://adoptium.net/)
2. Run the installer
3. Add Java to PATH:
   - Right-click "This PC" → Properties
   - Advanced system settings → Environment Variables
   - Add `C:\Program Files\Java\jdk-11\bin` to PATH

---

### Step 2: Install Apache JMeter

#### macOS (using Homebrew)

```bash
# Install JMeter
brew install jmeter

# Set JMETER_HOME environment variable
echo 'export JMETER_HOME="/opt/homebrew/Cellar/jmeter/5.6.3/libexec"' >> ~/.zshrc
echo 'export PATH="$JMETER_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verify installation
jmeter --version
```

#### Linux (Manual Installation)

```bash
# Download JMeter
cd ~/Downloads
wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.3.tgz

# Extract
tar -xzf apache-jmeter-5.6.3.tgz

# Move to /opt
sudo mv apache-jmeter-5.6.3 /opt/

# Set environment variables
echo 'export JMETER_HOME="/opt/apache-jmeter-5.6.3"' >> ~/.bashrc
echo 'export PATH="$JMETER_HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc

# Verify installation
jmeter --version
```

#### Windows

1. Download JMeter from [Apache JMeter Downloads](https://jmeter.apache.org/download_jmeter.cgi)
2. Extract the ZIP file to `C:\apache-jmeter-5.6.3`
3. Add to PATH:
   - Add `C:\apache-jmeter-5.6.3\bin` to System PATH
4. Verify: Open Command Prompt and run `jmeter --version`

---

### Step 3: Install Python

#### macOS

```bash
# Install Python using Homebrew
brew install python@3.11

# Verify installation
python3 --version
pip3 --version
```

#### Ubuntu/Debian Linux

```bash
# Install Python
sudo apt update
sudo apt install python3.11 python3-pip -y

# Verify installation
python3 --version
pip3 --version
```

#### Windows

1. Download Python from [python.org](https://www.python.org/downloads/)
2. Run installer (check "Add Python to PATH")
3. Verify: `python --version`

---

### Step 4: Install Python Dependencies

```bash
# Install required Python packages
pip3 install pandas numpy

# Verify installation
python3 -c "import pandas; import numpy; print('Dependencies installed successfully')"
```

---

### Step 5: Clone the Repository

```bash
# Clone the repository
git clone https://github.com/YOUR-USERNAME/jmeter-performance-test-framework.git

# Navigate to project directory
cd jmeter-performance-test-framework

# Verify structure
ls -la
```

---

### Step 6: Configure Environment

#### Create Environment-Specific Configuration

The framework includes template environment files. Customize them for your setup:

```bash
# Edit environment files
nano env/dev.properties

# Update these values:
# target.host=your-dev-api.example.com
# target.port=443
# target.protocol=https
```

#### Set Environment Variables (Optional)

For sensitive data, use environment variables instead of committing to properties files:

```bash
# Add to ~/.bashrc or ~/.zshrc
export API_KEY="your-api-key"
export AUTH_TOKEN="your-auth-token"
export DEV_API_HOST="dev-api.example.com"

# Reload
source ~/.bashrc  # or source ~/.zshrc
```

---

### Step 7: Make Scripts Executable

```bash
# Make utility scripts executable
chmod +x utils/run-test.sh
chmod +x utils/quality-gates.py
chmod +x docker/install-plugins.sh
```

---

## Docker Setup

### Step 1: Install Docker

#### macOS

```bash
# Download Docker Desktop from https://www.docker.com/products/docker-desktop
# Install and start Docker Desktop
# Verify installation
docker --version
docker-compose --version
```

#### Ubuntu Linux

```bash
# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh

# Add user to docker group
sudo usermod -aG docker $USER

# Log out and log back in, then verify
docker --version

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/download/v2.20.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version
```

#### Windows

1. Download [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
2. Run installer
3. Start Docker Desktop
4. Verify: `docker --version`

---

### Step 2: Build Docker Image

```bash
# Navigate to project root
cd jmeter-performance-test-framework

# Build the JMeter image
docker build -t jmeter-framework:latest -f docker/Dockerfile .

# Verify build
docker images | grep jmeter-framework
```

---

### Step 3: Start Docker Services

```bash
# Start all services (JMeter, InfluxDB, Grafana)
docker-compose -f docker/docker-compose.yml up -d

# Verify all services are running
docker-compose -f docker/docker-compose.yml ps

# Expected output:
# NAME                STATUS              PORTS
# jmeter-runner       Up                  
# jmeter-influxdb     Up                  0.0.0.0:8086->8086/tcp
# jmeter-grafana      Up                  0.0.0.0:3000->3000/tcp
```

---

### Step 4: Configure Grafana

```bash
# Access Grafana in browser
# URL: http://localhost:3000
# Username: admin
# Password: admin123

# Grafana will automatically:
# - Connect to InfluxDB
# - Load JMeter dashboard
# - Configure datasource
```

---

## IDE Configuration

### Visual Studio Code

#### Install Extensions

```bash
# Install recommended extensions
code --install-extension vscjava.vscode-java-pack
code --install-extension ms-python.python
code --install-extension redhat.vscode-xml
```

#### Workspace Settings

Create `.vscode/settings.json`:

```json
{
  "files.exclude": {
    "**/.git": true,
    "**/__pycache__": true,
    "results/": true,
    "reports/": true
  },
  "python.linting.enabled": true,
  "python.linting.pylintEnabled": true,
  "python.formatting.provider": "autopep8",
  "[python]": {
    "editor.tabSize": 4
  },
  "[xml]": {
    "editor.tabSize": 2
  }
}
```

---

### IntelliJ IDEA

1. **Open Project**: File → Open → Select project directory
2. **Configure JDK**: File → Project Structure → SDKs → Add JDK
3. **Install Plugins**:
   - Python
   - Groovy
   - Markdown

---

## Verification

### Test Local Setup

```bash
# Run smoke test
./utils/run-test.sh -t smoke -e dev

# Expected output:
# =========================================
#   JMeter Performance Test Execution
# =========================================
# Test Type:      smoke
# Environment:    dev
# ...
# ✓ Test execution completed successfully!
```

---

### Test Docker Setup

```bash
# Run smoke test in Docker
docker-compose -f docker/docker-compose.yml run --rm jmeter \
  jmeter -n -t /tests/scripts/test-plans/smoke-test.jmx \
  -q /tests/env/smoke.properties \
  -l /tests/results/smoke-test.jtl

# Check if results are generated
ls -lh results/

# Check Grafana
# Open browser: http://localhost:3000
# Login and verify dashboard loads
```

---

### Verify Python Quality Gates

```bash
# Generate a test result first
./utils/run-test.sh -t smoke -e dev

# Run quality gates validation
python3 utils/quality-gates.py \
  --jtl-file results/smoke-dev-*.jtl \
  --error-rate-threshold 5.0

# Expected output:
# ✅ All quality gates PASSED!
```

---

## Troubleshooting

### Issue: Java Not Found

**Symptom**: `java: command not found`

**Solution**:
```bash
# Verify Java installation
which java

# If not found, check JAVA_HOME
echo $JAVA_HOME

# Add Java to PATH
export PATH="/path/to/java/bin:$PATH"
```

---

### Issue: JMeter Not Found

**Symptom**: `jmeter: command not found`

**Solution**:
```bash
# Set JMETER_HOME
export JMETER_HOME="/path/to/apache-jmeter"
export PATH="$JMETER_HOME/bin:$PATH"

# Verify
jmeter --version
```

---

### Issue: Python Module Not Found

**Symptom**: `ModuleNotFoundError: No module named 'pandas'`

**Solution**:
```bash
# Install missing modules
pip3 install pandas numpy

# Or use requirements file
pip3 install -r requirements.txt
```

---

### Issue: Docker Permission Denied

**Symptom**: `permission denied while trying to connect to the Docker daemon`

**Solution**:
```bash
# Add user to docker group
sudo usermod -aG docker $USER

# Log out and log back in
# Verify
docker ps
```

---

### Issue: Port Already in Use

**Symptom**: `Bind for 0.0.0.0:3000 failed: port is already allocated`

**Solution**:
```bash
# Find process using the port
lsof -i :3000

# Kill the process
kill -9 <PID>

# Or change port in docker-compose.yml
# Change "3000:3000" to "3001:3000"
```

---

## Next Steps

After successful setup:

1. **Read the Documentation**:
   - [Test Strategy](test-strategy.md)
   - [Runbook](runbook.md)
   - [Conventions](conventions.md)

2. **Run Your First Test**:
   ```bash
   ./utils/run-test.sh -t smoke -e dev
   ```

3. **Explore the Framework**:
   - Review test plans in `scripts/test-plans/`
   - Check Groovy scripts in `scripts/groovy/`
   - Customize environment configs in `env/`

4. **Contribute**:
   - Read [Contributing Guide](contributing.md)
   - Fork the repository
   - Submit improvements

---

## Additional Resources

- [Apache JMeter Documentation](https://jmeter.apache.org/usermanual/index.html)
- [Docker Documentation](https://docs.docker.com/)
- [Grafana Documentation](https://grafana.com/docs/)
- [InfluxDB Documentation](https://docs.influxdata.com/)

---

## Support

Need help with setup?
- Check [Runbook](runbook.md) for common issues
- Open an issue on GitHub
- Contact: team@example.com

---

**Document Version**: 1.0  
**Last Updated**: 2025-10-18  
**Maintained By**: Performance Engineering Team

