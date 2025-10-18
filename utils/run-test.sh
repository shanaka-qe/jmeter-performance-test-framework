#!/bin/bash
# JMeter Test Execution Script
# This script simplifies running JMeter tests with proper configuration
# It handles environment setup, parameter passing, and result generation

set -e  # Exit on error
set -u  # Exit on undefined variable

# Color codes for output formatting
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Print script header
echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}  JMeter Performance Test Execution${NC}"
echo -e "${BLUE}=========================================${NC}\n"

# Function to print usage instructions
usage() {
    echo "Usage: $0 -t <test-type> -e <environment> [options]"
    echo ""
    echo "Required arguments:"
    echo "  -t, --test-type       Test type: smoke, load, stress, soak"
    echo "  -e, --environment     Target environment: dev, stage, prod"
    echo ""
    echo "Optional arguments:"
    echo "  -u, --threads         Number of virtual users (overrides env config)"
    echo "  -d, --duration        Test duration in seconds (overrides env config)"
    echo "  -r, --ramp-up         Ramp-up period in seconds (overrides env config)"
    echo "  -h, --help            Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 -t smoke -e dev"
    echo "  $0 -t load -e stage -u 50 -d 600"
    exit 1
}

# Default values
TEST_TYPE=""
ENVIRONMENT=""
THREADS=""
DURATION=""
RAMP_UP=""
JMETER_HOME="${JMETER_HOME:-/opt/apache-jmeter}"
JMETER_BIN="${JMETER_HOME}/bin/jmeter"

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        -t|--test-type)
            TEST_TYPE="$2"
            shift 2
            ;;
        -e|--environment)
            ENVIRONMENT="$2"
            shift 2
            ;;
        -u|--threads)
            THREADS="$2"
            shift 2
            ;;
        -d|--duration)
            DURATION="$2"
            shift 2
            ;;
        -r|--ramp-up)
            RAMP_UP="$2"
            shift 2
            ;;
        -h|--help)
            usage
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            usage
            ;;
    esac
done

# Validate required arguments
if [ -z "$TEST_TYPE" ] || [ -z "$ENVIRONMENT" ]; then
    echo -e "${RED}Error: Test type and environment are required${NC}\n"
    usage
fi

# Validate test type
if [[ ! "$TEST_TYPE" =~ ^(smoke|load|stress|soak)$ ]]; then
    echo -e "${RED}Error: Invalid test type: $TEST_TYPE${NC}"
    echo -e "Valid types: smoke, load, stress, soak\n"
    exit 1
fi

# Validate environment
if [[ ! "$ENVIRONMENT" =~ ^(dev|stage|prod)$ ]]; then
    echo -e "${RED}Error: Invalid environment: $ENVIRONMENT${NC}"
    echo -e "Valid environments: dev, stage, prod\n"
    exit 1
fi

# Set project directories (relative to script location)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
SCRIPTS_DIR="${PROJECT_ROOT}/scripts"
ENV_DIR="${PROJECT_ROOT}/env"
DATA_DIR="${PROJECT_ROOT}/data"
RESULTS_DIR="${PROJECT_ROOT}/results"
REPORTS_DIR="${PROJECT_ROOT}/reports"

# Create directories if they don't exist
mkdir -p "${RESULTS_DIR}"
mkdir -p "${REPORTS_DIR}"

# Determine test plan file based on test type
if [ "$TEST_TYPE" == "smoke" ]; then
    TEST_PLAN="${SCRIPTS_DIR}/test-plans/smoke-test.jmx"
    ENV_FILE="${ENV_DIR}/smoke.properties"
else
    TEST_PLAN="${SCRIPTS_DIR}/test-plans/load-test.jmx"
    ENV_FILE="${ENV_DIR}/${ENVIRONMENT}.properties"
fi

# Validate that test plan exists
if [ ! -f "$TEST_PLAN" ]; then
    echo -e "${RED}Error: Test plan not found: $TEST_PLAN${NC}\n"
    exit 1
fi

# Validate that environment file exists
if [ ! -f "$ENV_FILE" ]; then
    echo -e "${RED}Error: Environment file not found: $ENV_FILE${NC}\n"
    exit 1
fi

# Generate timestamp for unique result files
TIMESTAMP=$(date +%Y%m%d-%H%M%S)
RESULT_FILE="${RESULTS_DIR}/${TEST_TYPE}-${ENVIRONMENT}-${TIMESTAMP}.jtl"
LOG_FILE="${RESULTS_DIR}/${TEST_TYPE}-${ENVIRONMENT}-${TIMESTAMP}.log"
REPORT_DIR="${REPORTS_DIR}/${TEST_TYPE}-${ENVIRONMENT}-${TIMESTAMP}"

# Print test configuration
echo -e "${YELLOW}Test Configuration:${NC}"
echo -e "  Test Type:      ${GREEN}${TEST_TYPE}${NC}"
echo -e "  Environment:    ${GREEN}${ENVIRONMENT}${NC}"
echo -e "  Test Plan:      ${TEST_PLAN}"
echo -e "  Env Config:     ${ENV_FILE}"
echo -e "  Result File:    ${RESULT_FILE}"
echo -e "  Report Dir:     ${REPORT_DIR}\n"

# Build JMeter command
JMETER_CMD="${JMETER_BIN} -n -t ${TEST_PLAN} -q ${ENV_FILE}"

# Add optional parameter overrides
if [ -n "$THREADS" ]; then
    JMETER_CMD="${JMETER_CMD} -Jthreads=${THREADS}"
    echo -e "  ${YELLOW}Override:${NC} Threads = ${THREADS}"
fi

if [ -n "$DURATION" ]; then
    JMETER_CMD="${JMETER_CMD} -Jduration=${DURATION}"
    echo -e "  ${YELLOW}Override:${NC} Duration = ${DURATION}"
fi

if [ -n "$RAMP_UP" ]; then
    JMETER_CMD="${JMETER_CMD} -Jramp.up=${RAMP_UP}"
    echo -e "  ${YELLOW}Override:${NC} Ramp-up = ${RAMP_UP}"
fi

# Add result file paths
JMETER_CMD="${JMETER_CMD} -l ${RESULT_FILE} -j ${LOG_FILE} -e -o ${REPORT_DIR}"

echo ""

# Check if JMeter is available
if [ ! -f "$JMETER_BIN" ]; then
    echo -e "${RED}Error: JMeter not found at: $JMETER_BIN${NC}"
    echo -e "Please install JMeter or set JMETER_HOME environment variable\n"
    exit 1
fi

# Execute JMeter test
echo -e "${BLUE}=========================================${NC}"
echo -e "${BLUE}Starting test execution...${NC}"
echo -e "${BLUE}=========================================${NC}\n"

# Run the JMeter command
if eval "$JMETER_CMD"; then
    echo -e "\n${BLUE}=========================================${NC}"
    echo -e "${GREEN}✓ Test execution completed successfully!${NC}"
    echo -e "${BLUE}=========================================${NC}\n"
    
    # Print results location
    echo -e "${YELLOW}Results available at:${NC}"
    echo -e "  JTL File:       ${RESULT_FILE}"
    echo -e "  Log File:       ${LOG_FILE}"
    echo -e "  HTML Report:    ${REPORT_DIR}/index.html\n"
    
    # Run quality gates validation (if Python script exists)
    if [ -f "${SCRIPT_DIR}/quality-gates.py" ]; then
        echo -e "${YELLOW}Running quality gates validation...${NC}\n"
        if python3 "${SCRIPT_DIR}/quality-gates.py" \
            --jtl-file "${RESULT_FILE}" \
            --error-rate-threshold 2.0 \
            --p95-threshold 800; then
            echo -e "\n${GREEN}✓ All quality gates passed!${NC}\n"
            exit 0
        else
            echo -e "\n${RED}✗ Quality gates validation failed!${NC}\n"
            exit 1
        fi
    else
        exit 0
    fi
else
    echo -e "\n${BLUE}=========================================${NC}"
    echo -e "${RED}✗ Test execution failed!${NC}"
    echo -e "${BLUE}=========================================${NC}\n"
    echo -e "Check log file for details: ${LOG_FILE}\n"
    exit 1
fi

