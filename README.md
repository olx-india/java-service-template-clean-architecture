# Java Service Template — Clean Architecture

A production-ready **Spring Boot** template for building Java microservices using **Clean Architecture**. Use it to bootstrap new services with multi-tenancy, Redis caching, Kafka, database migrations, and observability built in.

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://docs.oracle.com/en/java/javase/21/)
[![Spring Boot 3.1](https://img.shields.io/badge/Spring%20Boot-3.1-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

---

## Features

- **Clean Architecture** — Layered design with clear separation: controllers, use cases, domain, and infrastructure
- **Multi-tenant MySQL** — Database-per-tenant support with configurable data sources
- **Redis** — Caching and session storage
- **Kafka** — Event producer integration
- **Flyway** — Versioned database migrations
- **OpenAPI / Swagger** — Auto-generated API docs (SpringDoc)
- **Resilience** — Retry, circuit breaker, and rate limiting (Resilience4j)
- **Observability** — OpenTelemetry tracing, structured logging (Logstash logback encoder)
- **Quality gates** — SpotBugs, OWASP dependency check, JaCoCo coverage, code formatter

---

## Tech Stack

| Category        | Technology                    |
|----------------|-------------------------------|
| Runtime        | Java 21                        |
| Framework      | Spring Boot 3.1.4              |
| Build          | Maven 3.6+                     |
| Database       | MySQL 8, Flyway                |
| Cache          | Redis, Spring Data Redis       |
| Messaging      | Apache Kafka                   |
| API docs       | SpringDoc OpenAPI              |
| Testing        | JUnit 5, Mockito, RestAssured, Cucumber, Testcontainers |

---

## Prerequisites

- **JDK 21** (required; other versions may cause Lombok/build issues)
- **Maven 3.6+**
- **Docker & Docker Compose** (for local MySQL, Redis, and integration tests)
- **Git**

---

## Quick Start

### 1. Clone the repository

```bash
git clone https://github.com/your-org/java-service-template-clean-architecture.git
cd java-service-template-clean-architecture
```

### 2. Use JDK 21

If you have multiple JDKs, set Java 21 before building or running:

```bash
# macOS / Linux (bash/zsh)
export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS
# or: export JAVA_HOME=/path/to/jdk21               # Linux

java -version   # Should show 21.x
```

### 3. Build

```bash
# Full build (unit + integration tests; requires Docker)
mvn clean package

# Unit tests only (no Docker required)
mvn clean package -DskipIntegration=true
```

### 4. Run locally (with Docker)

Start dependencies and run the app:

```bash
# Start MySQL, Redis, OpenTelemetry collector
docker compose -f docker-compose-local.yml up -d

# Run database migrations
./migrate.sh

# Run the application (local profile)
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

API: **http://localhost:8080**  
Swagger UI: **http://localhost:8080/swagger-ui.html** (if enabled in profile)

---

## Project Structure

```
├── src/main/java/com/olx/boilerplate/
│   ├── controller/       # REST API, DTOs, request/response mapping
│   ├── usercase/         # Application use cases (business flows)
│   ├── domain/           # Domain entities and interfaces
│   ├── repository/       # Data access interfaces
│   └── infrastructure/   # Config, DB, Redis, Kafka, HTTP clients, etc.
├── src/test/
│   ├── java/             # Unit tests (ut/), integration tests
│   └── resources/        # Test config, Cucumber features, mocks
├── docker-compose-local.yml
├── Dockerfile
├── migrate.sh            # Flyway migration runner
├── formatter.xml         # Eclipse formatter (Maven formatter plugin)
└── pom.xml
```

---

## Architecture

The project follows **Clean Architecture**: outer layers depend on inner layers; the domain has no framework or infrastructure dependencies.

![Clean Architecture](CleanArchitecture.jpg)

| Layer            | Responsibility |
|------------------|----------------|
| **Controller**   | HTTP handling, validation, DTO mapping |
| **Use case**     | Orchestrate business logic, call domain and repositories |
| **Domain**       | Entities and business rules |
| **Repository**   | Data access contracts (interfaces) |
| **Infrastructure** | Implementations: DB, Redis, Kafka, HTTP, config |

Dependency injection is used so that use cases and controllers depend on **abstractions** (interfaces), not concrete implementations.

---

## Configuration

- **Profiles:** `local`, `integration-test`, etc. (see `src/main/resources/application*.yaml`).
- **Local:** Use `application-local.yaml` and `docker-compose-local.yml` for DB, Redis, and Kafka URLs.
- **Environment variables:** Override config via env (e.g. datasource, Redis host, Kafka bootstrap servers).

---

## Testing

### Unit tests

- **Location:** `src/test/java/.../ut/`
- **Run:** `mvn test`
- **Stack:** JUnit 5, Mockito; dependencies mocked.

### Integration tests

- **Location:** Cucumber `.feature` files and Java step definitions; run with Failsafe.
- **Run:** `mvn verify` or `mvn integration-test` (uses `integration-test` profile).
- **Requirements:** Docker (MySQL, Redis, etc. via Testcontainers or `docker-compose-local.yml`).

### Example Cucumber scenario

```gherkin
Scenario: Create a new user successfully
  Given I have /user API
  And I have following headers
    | Content-Type     |
    | application/json |
  And I have a request body in {"name":"Test User", "email": "test@email.com"}
  When Execute POST request using REST
  Then Validate status code is: 201
  And Validate user response
```

---

## Development

### Code formatting

Format code and validate style:

```bash
mvn formatter:format
# or use formatter:validate in CI
```

Formatter config: `formatter.xml` (Eclipse-style, used by Maven formatter plugin).

### Quality and security

- **SpotBugs:** `mvn spotbugs:check`
- **OWASP dependency check:** `mvn dependency-check:check`
- **JaCoCo:** Coverage reports in `target/coverage-reports/` and `target/site/jacoco/`

### Run only unit tests

```bash
mvn clean test
```

### Build without integration tests

```bash
mvn clean package -DskipIntegration=true
```

---

## Docker

- **Build image:** The root `Dockerfile` builds a Java 21 image and copies the packaged JAR (e.g. `target/boilerplate-0.1.0.jar`). Build with `mvn package` first.
- **Local stack:** `docker compose -f docker-compose-local.yml up -d` runs MySQL, Redis, and an OpenTelemetry collector; the app can be run from the host with the `local` profile.

---

## FAQ

### Build fails with Lombok `TypeTag :: UNKNOWN` or “Unsupported class file major version”

The project **must** be built with **JDK 21**. Using a different JDK (e.g. 22 or 24) can break Lombok and the compiler.

- Check: `java -version` and `mvn -version` (Maven uses `JAVA_HOME`).
- Set JDK 21: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` (macOS) or point `JAVA_HOME` to your JDK 21 install.
- List JDKs on macOS: `/usr/libexec/java_home -V`.

### How do I add a new API or use case?

1. Add or reuse a **domain** entity/interface under `domain/`.
2. Define a **repository** interface under `repository/` if you need persistence.
3. Implement the **use case** in `usercase/` (call domain + repository).
4. Expose it via a **controller** in `controller/` and map DTOs.
5. Add **infrastructure** (e.g. JPA, Redis) under `infrastructure/` if needed.

### Where is the OpenAPI / Swagger spec?

SpringDoc is included; the UI and JSON are typically available at `/swagger-ui.html` and `/v3/api-docs` when the app runs, depending on profile and configuration.

---

## Contributing

Contributions are welcome. Please read [CONTRIBUTING.md](CONTRIBUTING.md) for:

- How to report bugs and suggest features
- Development setup and code style
- Branch and pull request process
- Commit message guidelines

---

## License

This project is licensed under the **Apache License 2.0**. See the [LICENSE](LICENSE) file for the full text, or [https://www.apache.org/licenses/LICENSE-2.0](https://www.apache.org/licenses/LICENSE-2.0).

---

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html) (Robert C. Martin)
- All [contributors](https://github.com/your-org/java-service-template-clean-architecture/graphs/contributors) and users of this template
