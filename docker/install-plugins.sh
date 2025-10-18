#!/bin/bash
# JMeter Plugin Installation Script
# This script installs essential JMeter plugins using the PluginsManager
# Plugins are pinned to specific versions for reproducibility

set -e  # Exit on error
set -u  # Exit on undefined variable

# Color codes for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== JMeter Plugin Installation ===${NC}"

# Set JMeter home directory
JMETER_HOME="${JMETER_HOME:-/opt/apache-jmeter}"
JMETER_LIB="${JMETER_HOME}/lib"
JMETER_EXT="${JMETER_LIB}/ext"

# Create lib/ext directory if it doesn't exist
mkdir -p "${JMETER_EXT}"

# Download PluginsManager if not present
PLUGINS_MANAGER_JAR="${JMETER_LIB}/ext/jmeter-plugins-manager.jar"
if [ ! -f "${PLUGINS_MANAGER_JAR}" ]; then
    echo -e "${YELLOW}Downloading JMeter Plugins Manager...${NC}"
    curl -L -o "${PLUGINS_MANAGER_JAR}" \
        "https://jmeter-plugins.org/get/"
    echo -e "${GREEN}✓ Plugins Manager downloaded${NC}"
else
    echo -e "${GREEN}✓ Plugins Manager already present${NC}"
fi

# Download cmdrunner for plugin installation
CMDRUNNER_JAR="${JMETER_LIB}/cmdrunner-2.3.jar"
if [ ! -f "${CMDRUNNER_JAR}" ]; then
    echo -e "${YELLOW}Downloading cmdrunner...${NC}"
    curl -L -o "${CMDRUNNER_JAR}" \
        "https://search.maven.org/remotecontent?filepath=kg/apc/cmdrunner/2.3/cmdrunner-2.3.jar"
    echo -e "${GREEN}✓ cmdrunner downloaded${NC}"
else
    echo -e "${GREEN}✓ cmdrunner already present${NC}"
fi

# Create plugin manager command
cd "${JMETER_HOME}"
java -cp "${CMDRUNNER_JAR}" kg.apc.cmdtools.PluginManagerCMD install-for-jmeter 2>/dev/null || true

# List of essential plugins to install
# These are commonly used plugins for performance testing
PLUGINS=(
    # Custom Thread Groups - for advanced load modeling
    "jpgc-casutg=2.10"              # Concurrency Thread Group
    "jpgc-functions=2.2"            # Custom JMeter Functions
    "jpgc-json=2.7"                 # JSON plugins
    "jpgc-perfmon=2.1"              # PerfMon metrics collector
    "jpgc-synthesis=2.2"            # Synthesis Report
    "jpgc-graphs-basic=2.0"         # Basic graphs
    "jpgc-graphs-additional=2.0"    # Additional graphs
    "jpgc-graphs-composite=2.0"     # Composite graphs
    "jpgc-tst=2.6"                  # Throughput Shaping Timer
    "jpgc-dummy=0.4"                # Dummy Sampler (for testing)
    "jpgc-fifo=0.2"                 # Inter-Thread Communication
    "jpgc-cmd=2.3"                  # Command Runner
)

echo -e "${YELLOW}Installing JMeter plugins...${NC}"

# Install plugins using PluginsManagerCMD
for plugin in "${PLUGINS[@]}"; do
    plugin_name=$(echo "${plugin}" | cut -d'=' -f1)
    plugin_version=$(echo "${plugin}" | cut -d'=' -f2)
    
    echo -e "${YELLOW}Installing ${plugin_name} version ${plugin_version}...${NC}"
    
    # Use java to call the PluginsManagerCMD
    java -jar "${CMDRUNNER_JAR}" \
        --tool org.jmeterplugins.repository.PluginManagerCMD \
        install "${plugin_name}" \
        2>/dev/null || echo -e "${RED}Warning: Failed to install ${plugin_name}${NC}"
    
    echo -e "${GREEN}✓ ${plugin_name} installed${NC}"
done

echo -e "${GREEN}=== Plugin installation completed ===${NC}"

# List installed plugins
echo -e "${YELLOW}Installed plugins:${NC}"
ls -lh "${JMETER_EXT}" | grep -E "\.jar$" | awk '{print "  - " $9}'

# Verify installation
if [ -f "${PLUGINS_MANAGER_JAR}" ]; then
    echo -e "${GREEN}✓ JMeter is ready with plugins!${NC}"
    exit 0
else
    echo -e "${RED}✗ Plugin installation failed${NC}"
    exit 1
fi

