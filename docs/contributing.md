# 🤝 Contributing to JMeter Performance Test Framework

Thank you for your interest in contributing! This document provides guidelines for contributing to the project.

---

## Table of Contents

1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Workflow](#development-workflow)
4. [Pull Request Process](#pull-request-process)
5. [Coding Standards](#coding-standards)
6. [Testing Requirements](#testing-requirements)
7. [Documentation](#documentation)

---

## Code of Conduct

### Our Standards

- Be respectful and inclusive
- Welcome constructive feedback
- Focus on what is best for the community
- Show empathy towards others

### Unacceptable Behavior

- Harassment or discriminatory language
- Personal attacks or insults
- Publishing private information
- Unprofessional conduct

---

## Getting Started

### Prerequisites

- Git
- JMeter 5.6.3+ (or Docker)
- Java 11+
- Python 3.11+
- A code editor (VS Code, IntelliJ, etc.)

### Fork and Clone

1. **Fork the repository** on GitHub
2. **Clone your fork locally**:
   ```bash
   git clone https://github.com/YOUR-USERNAME/jmeter-performance-test-framework.git
   cd jmeter-performance-test-framework
   ```
3. **Add upstream remote**:
   ```bash
   git remote add upstream https://github.com/ORIGINAL-OWNER/jmeter-performance-test-framework.git
   ```

### Development Setup

```bash
# Ensure JMeter is available
export JMETER_HOME=/path/to/apache-jmeter
export PATH=$JMETER_HOME/bin:$PATH

# Install Python dependencies
pip install pandas numpy

# Verify setup
jmeter --version
python3 --version
```

---

## Development Workflow

### Branching Strategy

We use **Git Flow** branching model:

```
main (or master)
├── develop
│   ├── feature/add-new-test-plan
│   ├── feature/improve-groovy-scripts
│   ├── bugfix/fix-token-extraction
│   └── hotfix/critical-issue
```

### Branch Naming Conventions

```
Format: <type>/<short-description>

✅ Good:
  feature/add-stress-test
  bugfix/fix-csv-parsing
  docs/update-readme
  refactor/simplify-quality-gates

❌ Bad:
  new-feature
  fix
  updates
  test
```

### Creating a Feature Branch

```bash
# Update your develop branch
git checkout develop
git pull upstream develop

# Create a new feature branch
git checkout -b feature/add-new-test-plan

# Make your changes
# ...

# Commit your changes
git add .
git commit -m "feat(test): Add new stress test plan for API"

# Push to your fork
git push origin feature/add-new-test-plan
```

---

## Pull Request Process

### Before Submitting

Ensure your changes meet these requirements:

- [ ] Code follows the [coding standards](conventions.md)
- [ ] All tests pass locally
- [ ] Quality gates validation succeeds
- [ ] Documentation is updated
- [ ] Commit messages are clear and descriptive
- [ ] No merge conflicts with target branch
- [ ] No secrets or credentials committed

### Submitting a Pull Request

1. **Push your branch** to your fork
2. **Open a pull request** on GitHub
3. **Fill out the PR template** completely
4. **Link related issues** (if any)
5. **Wait for review** and address feedback

### Pull Request Template

```markdown
## Description
Brief description of the changes

## Type of Change
- [ ] New feature
- [ ] Bug fix
- [ ] Documentation update
- [ ] Refactoring
- [ ] Performance improvement

## Testing
Describe how you tested your changes:
- [ ] Local execution
- [ ] Docker execution
- [ ] Quality gates validation

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex logic
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
- [ ] Tests pass successfully

## Screenshots (if applicable)
Add screenshots for UI-related changes

## Additional Notes
Any additional context or notes for reviewers
```

### Review Process

1. **Automated checks** run (CI/CD pipeline)
2. **Code review** by maintainers
3. **Feedback** addressed by contributor
4. **Approval** from at least one maintainer
5. **Merge** into target branch

### After Merge

```bash
# Update your local develop branch
git checkout develop
git pull upstream develop

# Delete your feature branch
git branch -d feature/add-new-test-plan
git push origin --delete feature/add-new-test-plan
```

---

## Coding Standards

### General Guidelines

1. **Follow existing patterns** in the codebase
2. **Write descriptive comments** for complex logic
3. **Use meaningful names** for variables and functions
4. **Keep it simple** - avoid over-engineering
5. **Test your changes** before committing

See [Conventions](conventions.md) for detailed coding standards.

### JMeter Test Plans

```xml
<!-- Always add descriptive comments -->
<!-- Use meaningful names for all components -->
<!-- Follow the established test plan structure -->
<!-- Use Include Controllers for reusable components -->
```

### Groovy Scripts

```groovy
// Always use JSR223 with Groovy (not Beanshell)
// Enable script caching for performance
// Add comprehensive logging for debugging
// Handle errors gracefully with try-catch
// Use vars.put() and vars.get() for variables
```

### Python Scripts

```python
# Follow PEP 8 style guide
# Add descriptive comments for each function
# Use type hints where appropriate
# Handle exceptions properly
# Write docstrings for all functions
```

---

## Testing Requirements

### Test Your Changes

Before submitting a PR, ensure:

1. **Smoke test passes**:
   ```bash
   ./utils/run-test.sh -t smoke -e dev
   ```

2. **Quality gates validation succeeds**:
   ```bash
   python3 utils/quality-gates.py --jtl-file results/smoke-dev-*.jtl
   ```

3. **Docker build succeeds** (if Docker changes):
   ```bash
   docker build -t jmeter-framework:test -f docker/Dockerfile .
   ```

4. **Documentation builds** (if doc changes):
   - Check all markdown renders correctly
   - Verify all links work

### CI/CD Tests

GitHub Actions will automatically:
- Validate test plans
- Run smoke tests
- Check quality gates
- Build Docker image
- Verify documentation

All checks must pass before merge.

---

## Documentation

### When to Update Documentation

Update documentation when you:
- Add new features or test plans
- Change existing behavior
- Add new scripts or utilities
- Modify configuration options
- Fix bugs that affect usage

### Documentation Standards

1. **Use Markdown** for all documentation
2. **Add emojis** for visual appeal (✅, ❌, 📊, etc.)
3. **Include code examples** with syntax highlighting
4. **Keep it concise** but comprehensive
5. **Update table of contents** if adding sections

### Documentation Files

- `README.md`: Project overview and quick start
- `docs/test-strategy.md`: Testing strategy and SLAs
- `docs/runbook.md`: Execution instructions
- `docs/conventions.md`: Coding standards
- `docs/contributing.md`: This file

---

## Common Contribution Types

### Adding a New Test Plan

1. Create JMX file in `scripts/test-plans/`
2. Add corresponding environment file in `env/`
3. Update `README.md` with new test type
4. Add runbook entry in `docs/runbook.md`
5. Test locally before submitting PR

### Adding a New Groovy Script

1. Create script in `scripts/groovy/`
2. Follow the script template structure
3. Add comprehensive comments
4. Test integration with test plans
5. Document usage in script header

### Improving Quality Gates

1. Modify `utils/quality-gates.py`
2. Update threshold documentation
3. Test with various JTL files
4. Update CI/CD workflows if needed
5. Document new parameters

### Updating Documentation

1. Make changes to relevant .md files
2. Ensure formatting is correct
3. Check all links work
4. Update table of contents if needed
5. Verify examples are accurate

---

## Getting Help

### Resources

- **Documentation**: Check the `docs/` directory
- **Issues**: Search existing GitHub issues
- **Discussions**: Use GitHub Discussions for questions

### Contact

For questions or clarifications:
- Open a GitHub issue
- Start a GitHub Discussion
- Email: team@example.com

---

## Recognition

Contributors will be recognized in:
- Release notes
- Contributors list
- Project acknowledgments

Thank you for contributing! 🎉

---

**Document Version**: 1.0  
**Last Updated**: 2025-10-18  
**Maintained By**: Performance Engineering Team

