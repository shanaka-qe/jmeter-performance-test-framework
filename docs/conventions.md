# 📏 Coding Standards and Conventions

This document defines the coding standards, naming conventions, and best practices for the JMeter Performance Test Framework.

---

## Table of Contents

1. [General Principles](#general-principles)
2. [File and Directory Naming](#file-and-directory-naming)
3. [JMeter Test Plans](#jmeter-test-plans)
4. [Groovy Scripts](#groovy-scripts)
5. [Environment Configuration](#environment-configuration)
6. [Test Data](#test-data)
7. [Documentation](#documentation)
8. [Version Control](#version-control)

---

## General Principles

### Code Quality Standards

1. **Readability**: Code should be self-documenting and easy to understand
2. **Modularity**: Break down complex logic into reusable components
3. **Maintainability**: Use consistent patterns and structures
4. **Performance**: Optimize for JMeter execution efficiency
5. **Security**: Never commit secrets or sensitive data

### Best Practices

✅ **Do:**
- Write descriptive comments for complex logic
- Use meaningful variable and function names
- Follow established patterns in the framework
- Test changes before committing
- Document any deviations from standards

❌ **Don't:**
- Hard-code values that should be configurable
- Use deprecated JMeter features (e.g., Beanshell)
- Commit credentials or API keys
- Create overly complex nested logic
- Skip code reviews

---

## File and Directory Naming

### Directory Structure

All directories use lowercase with hyphens:

```
✅ Good:
  scripts/test-plans/
  data/payloads/
  docs/grafana/

❌ Bad:
  Scripts/TestPlans/
  Data_Payloads/
  Docs.Grafana/
```

### File Naming Conventions

#### JMeter Test Plans (*.jmx)

```
Format: <test-type>-test.jmx

✅ Good:
  smoke-test.jmx
  load-test.jmx
  stress-test.jmx
  soak-test.jmx

❌ Bad:
  SmokeTest.jmx
  Test_Load.jmx
  mytest.jmx
```

#### Groovy Scripts (*.groovy)

```
Format: <purpose>-<noun>.groovy

✅ Good:
  uuid-generator.groovy
  token-extractor.groovy
  custom-logger.groovy
  random-data-generator.groovy

❌ Bad:
  GenerateUUID.groovy
  extract_token.groovy
  logger.groovy
```

#### Environment Files (*.properties)

```
Format: <environment>.properties

✅ Good:
  dev.properties
  stage.properties
  prod.properties
  smoke.properties

❌ Bad:
  Development.properties
  PROD_env.properties
  staging.config
```

#### Test Data Files

```
Format: <data-type>-<description>.csv

✅ Good:
  test-users.csv
  product-catalog.csv
  customer-addresses.csv

❌ Bad:
  Users.csv
  data.csv
  test_file_1.csv
```

#### Result Files

```
Format: <test-type>-<environment>-<timestamp>.jtl

✅ Good:
  smoke-dev-20251018-143022.jtl
  load-stage-20251018-150000.jtl

❌ Bad:
  results.jtl
  test_20251018.jtl
  output.jtl
```

---

## JMeter Test Plans

### Component Naming

#### Thread Groups

```
Format: <Test Type> <Purpose>

✅ Good:
  Smoke Test Users
  Load Test Users
  API Health Check

❌ Bad:
  Thread Group 1
  Users
  TG
```

#### HTTP Samplers

```
Format: <HTTP Method> <Endpoint Description>

✅ Good:
  POST Login
  GET User Profile
  PUT Update Order
  DELETE Remove Item

❌ Bad:
  HTTP Request
  API Call 1
  Request
```

#### Controllers

```
Format: <Type>: <Purpose>

✅ Good:
  Transaction: User Login Flow
  Loop: Retry Logic
  If: Error Handling

❌ Bad:
  Transaction Controller
  Loop
  If Controller
```

#### Pre/Post Processors

```
Format: <Action> <Target>

✅ Good:
  Generate UUID
  Extract Token
  Log Response
  Validate Payload

❌ Bad:
  JSR223 PreProcessor
  Processor 1
  Script
```

### Test Plan Structure

All test plans should follow this hierarchy:

```
Test Plan
├── User Defined Variables
├── CSV Data Set Config (if needed)
├── Thread Group
│   ├── Include: HTTP Defaults
│   ├── Include: HTTP Headers
│   ├── JSR223 PreProcessor: Setup
│   ├── Transaction Controller
│   │   ├── HTTP Sampler: Request 1
│   │   │   ├── JSON Extractor
│   │   │   ├── Response Assertion
│   │   │   └── JSR223 PostProcessor
│   │   ├── Think Time
│   │   └── HTTP Sampler: Request 2
│   └── Constant Throughput Timer
└── Backend Listener (InfluxDB)
```

### Comments in JMX Files

Add descriptive comments to all major components:

```xml
<!-- Test Plan: Smoke Test -->
<!-- Purpose: Quick health check with minimal load -->
```

---

## Groovy Scripts

### Script Structure

Every Groovy script should follow this template:

```groovy
// Script Name and Purpose
// Brief description of what this script does
// Usage: Where and how to use this script

import java.util.* // Required imports

// Main logic with descriptive comments
def variable = someFunction()

// Store results in JMeter variables
vars.put("variableName", variable)

// Log for debugging
log.info("Description: " + variable)

// Return value (optional)
return variable
```

### Variable Naming

```groovy
✅ Good:
  def accessToken = extractToken(response)
  def userId = jsonResponse?.user?.id
  def generatedUUID = UUID.randomUUID()
  def currentTimestamp = System.currentTimeMillis()

❌ Bad:
  def token = extract()
  def id = json.user.id
  def uuid = UUID.randomUUID()
  def ts = System.currentTimeMillis()
```

### Function Naming

```groovy
✅ Good:
  def extractAccessToken(responseBody)
  def generateRandomEmail()
  def validateResponseCode(code)

❌ Bad:
  def extract(resp)
  def gen()
  def validate(c)
```

### Best Practices

1. **Always use JSR223 with Groovy** (never Beanshell)
2. **Enable script caching** for performance
3. **Use `vars.put()` and `vars.get()`** for variables
4. **Add comprehensive logging** for debugging
5. **Handle errors gracefully** with try-catch
6. **Keep scripts focused** on a single purpose

### Error Handling

```groovy
try {
    // Main logic here
    def result = someOperation()
    vars.put("result", result)
} catch (Exception e) {
    log.error("Error in script: " + e.getMessage())
    // Set a default or flag the error
    vars.put("result", "ERROR")
}
```

---

## Environment Configuration

### Property Naming

Use dot notation with descriptive names:

```properties
✅ Good:
target.host=dev-api.example.com
target.port=443
target.protocol=https
threads=10
ramp.up=30
think.time.min=1000
data.file.path=../data/test-users.csv

❌ Bad:
HOST=dev-api.example.com
port=443
protocol=https
threads=10
rampup=30
thinktime=1000
datapath=../data/test-users.csv
```

### Property Organization

Group related properties together:

```properties
# Target application settings
target.host=dev-api.example.com
target.port=443
target.protocol=https
base.path=/api/v1

# Load testing parameters
threads=10
ramp.up=30
duration=300
throughput=10

# Test data paths
data.file.path=../data/test-users.csv
payload.path=../data/payloads
```

### Comments

Add section headers and explanatory comments:

```properties
# Development Environment Configuration

# Target application settings
# These define where the tests will run
target.host=dev-api.example.com

# Load testing parameters
# Number of concurrent virtual users
threads=10
```

---

## Test Data

### CSV File Format

```csv
✅ Good:
username,password,email,role
testuser1,TestPass123!,testuser1@example.com,user
testuser2,TestPass123!,testuser2@example.com,admin

❌ Bad:
Username,Password,Email,Role  # Don't capitalize headers
testuser1;TestPass123!;testuser1@example.com;user  # Don't use semicolons
```

### JSON Payload Templates

Use proper formatting with JMeter variable placeholders:

```json
✅ Good:
{
  "username": "${username}",
  "password": "${password}",
  "rememberMe": false,
  "deviceId": "${uuid}"
}

❌ Bad:
{"username":"${username}","password":"${password}"}
```

### Data Security

```
✅ Good:
- Use placeholder credentials
- Reference secrets from CI/CD
- Sanitize production data
- Document data requirements

❌ Bad:
- Hard-code real credentials
- Use actual customer data
- Commit secrets to Git
- Leave PII in test data
```

---

## Documentation

### Markdown Files

All documentation should use Markdown format:

```markdown
✅ Good:
# Main Title
## Section
### Subsection

- Bullet point
- Another point

1. Numbered item
2. Another item

✅ Use emojis for visual appeal
❌ Don't overuse formatting
```

### Code Blocks

Always specify the language for syntax highlighting:

```markdown
✅ Good:
```bash
jmeter -n -t test.jmx
```

❌ Bad:
```
jmeter -n -t test.jmx
```
```

### Comments

Add inline comments for clarity:

```bash
# This command starts the smoke test on dev environment
./utils/run-test.sh -t smoke -e dev
```

---

## Version Control

### Git Commit Messages

Follow the conventional commit format:

```
Format: <type>(<scope>): <subject>

✅ Good:
feat(test): Add stress test plan for API endpoints
fix(groovy): Correct token extraction logic
docs(readme): Update installation instructions
refactor(ci): Improve GitHub Actions workflow

❌ Bad:
update
fixed bug
new test
changes
```

### Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `refactor`: Code refactoring
- `test`: Test-related changes
- `chore`: Maintenance tasks
- `ci`: CI/CD changes

### Git Ignore

Never commit:
- Result files (*.jtl)
- Log files (*.log)
- Reports (reports/)
- Temporary files
- IDE-specific files
- Credentials

---

## Quality Checklist

Before committing code, verify:

- [ ] File names follow conventions
- [ ] Code has descriptive comments
- [ ] No hard-coded credentials
- [ ] Test plans are properly structured
- [ ] Groovy scripts use JSR223
- [ ] Environment files are complete
- [ ] Documentation is updated
- [ ] Quality gates pass
- [ ] No linting errors

---

**Document Version**: 1.0  
**Last Updated**: 2025-10-18  
**Maintained By**: Performance Engineering Team

