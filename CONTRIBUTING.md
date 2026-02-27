# Contributing to Java Service Template

Thank you for your interest in contributing. This document explains how to set up your environment, follow our conventions, and submit changes.

---

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How to Contribute](#how-to-contribute)
- [Development Setup](#development-setup)
- [Code Style and Formatting](#code-style-and-formatting)
- [Branching and Pull Requests](#branching-and-pull-requests)
- [Commit Messages](#commit-messages)
- [Testing Requirements](#testing-requirements)

---

## Code of Conduct

This project expects respectful, inclusive, and constructive behavior from everyone. By participating, you agree to:

- Be respectful of differing viewpoints and experience levels
- Accept constructive criticism and focus on what is best for the project
- Show empathy towards other contributors and users

Report unacceptable behavior to the maintainers (e.g. via GitHub issues or repository contacts). We reserve the right to remove or block participants who violate these expectations.

---

## How to Contribute

### Reporting bugs

- **Use GitHub Issues** and choose the “Bug” (or similar) template if available.
- Include:
  - Clear title and description
  - Steps to reproduce
  - Expected vs actual behavior
  - Your environment: OS, JDK version (`java -version`), Maven version
- Avoid duplicating existing issues; search before opening a new one.

### Suggesting features or improvements

- Open an issue with the “Enhancement” or “Feature request” label (if available).
- Describe the use case, expected behavior, and why it would help.
- Discussion may happen in the issue before any code is written.

### Contributing code

1. **Comment on the issue** you want to work on (or open one first) so maintainers can assign it and avoid duplicate work.
2. **Fork the repository** and create a branch (see [Branching and Pull Requests](#branching-and-pull-requests)).
3. **Implement your changes** following [Code Style and Formatting](#code-style-and-formatting) and [Testing Requirements](#testing-requirements).
4. **Open a pull request** with a clear description and link to the issue.

---

## Development Setup

### Requirements

- **JDK 21** (required; the project does not support other versions for build)
- **Maven 3.6+**
- **Docker & Docker Compose** (for integration tests and local services)
- **Git**

### One-time setup

1. **Fork and clone**

   ```bash
   git clone https://github.com/YOUR_USERNAME/java-service-template-clean-architecture.git
   cd java-service-template-clean-architecture
   ```

2. **Use JDK 21**

   ```bash
   export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS
   # Or on Linux: export JAVA_HOME=/path/to/jdk21
   java -version   # Must show 21.x
   ```

3. **Verify build**

   ```bash
   mvn clean package -DskipIntegration=true
   ```

4. **Optional: run full build with integration tests**

   ```bash
   docker compose -f docker-compose-local.yml up -d
   mvn clean package
   ```

### IDE

- **IntelliJ IDEA / Eclipse:** Import as Maven project; set project SDK to JDK 21.
- **Annotation processing:** Enable annotation processing (Lombok) so getters/setters/builders are generated and the IDE does not show false errors.

---

## Code Style and Formatting

- We use the **Eclipse-based formatter** configured in `formatter.xml` and the **Maven Formatter Plugin**.
- Before committing, format the code:

  ```bash
  mvn formatter:format
  ```

- CI may run `formatter:validate`; ensure your branch passes it.
- Prefer:
  - Meaningful names for classes, methods, and variables
  - Small, focused methods and classes
  - Clear separation between layers (controller → use case → domain/repository → infrastructure)
- New code should follow the existing **Clean Architecture** layering; avoid putting business logic in controllers or infrastructure leaking into domain.

---

## Branching and Pull Requests

### Branch naming

- **Feature:** `feature/short-description` or `feat/short-description`
- **Bugfix:** `fix/short-description` or `bugfix/short-description`
- **Docs:** `docs/short-description`
- **Chore (deps, build, config):** `chore/short-description`

Examples: `feature/add-health-metrics`, `fix/redis-connection-timeout`, `docs/contributing-setup`.

### Pull request process

1. **Base branch:** Usually `main`. Confirm in the repo’s default branch settings.
2. **Keep scope small:** Prefer one logical change per PR (one feature or one bugfix).
3. **Description:**
   - What changed and why
   - Link to the issue (e.g. “Fixes #123”)
   - Any notes for reviewers (e.g. how to test)
4. **Checks:** All CI jobs (build, unit tests, formatter, etc.) must pass.
5. **Review:** Address review comments; maintainers will merge when approved and green.

### What we look for in reviews

- Correctness and alignment with Clean Architecture
- Tests added or updated where appropriate
- No unnecessary dependencies or breaking changes unless agreed
- Formatting and style consistent with the project

---

## Commit Messages

- **First line:** Short summary (about 50 characters), imperative mood (e.g. “Add retry for Kafka producer”, “Fix NPE in tenant resolver”).
- **Body (optional):** Explain *what* and *why*, not only *how*. Wrap at ~72 characters.
- **Footer (optional):** Reference issues, e.g. `Fixes #42` or `Refs #10`.

Examples:

```
Add OpenAPI tags for user and order APIs

Improves discoverability in Swagger UI and generated docs.
```

```
Fix Lombok processor path for Java 21

Fixes #15
```

---

## Testing Requirements

- **Unit tests:** New or changed behavior in use cases, services, or utilities should have unit tests (JUnit 5 + Mockito). Run with `mvn test`.
- **Integration tests:** For new or changed REST APIs or critical flows, add or update Cucumber scenarios or integration tests where appropriate. Run with `mvn verify` (Docker required for full integration tests).
- **No regressions:** Ensure existing tests pass. If you change behavior intentionally, update the tests accordingly and explain in the PR.
- **Coverage:** The project uses JaCoCo; avoid dropping coverage for modified code when possible.

---

## Questions?

- Open a **GitHub Discussion** (if enabled) or an **Issue** with the “Question” label.
- For bugs and features, use issues as described in [How to Contribute](#how-to-contribute).

Thank you for contributing.
