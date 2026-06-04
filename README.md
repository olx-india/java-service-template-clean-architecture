# Java Service Template — Clean Architecture

A production-ready **Spring Boot** template for building Java microservices using **Clean Architecture**. Use it to bootstrap new services with multi-tenancy, Redis caching, Kafka, database migrations, and observability built in.

[![Java 21](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk)](https://docs.oracle.com/en/java/javase/21/)
[![Spring Boot 3.1](https://img.shields.io/badge/Spring%20Boot-3.1-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6+-C71A36?logo=apachemaven)](https://maven.apache.org/)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

---

## Features

- **Clean Architecture** — Layered design with ArchUnit boundary tests; see [docs/clean-architecture.md](docs/clean-architecture.md)
- **Multi-tenant MySQL** — Database-per-tenant with read/write replica routing
- **Redis** — Spring Cache on use cases with actuator cache eviction
- **Kafka** — Domain events via `EventPublisher` port (`UserCreatedEvent`)
- **Flyway** — Versioned migrations including transactional outbox table
- **OpenAPI / Swagger** — SpringDoc with annotated controllers
- **Resilience** — Circuit breaker, retry, and rate limiter (Resilience4j) on `ExternalHttpClient`
- **Security** — Optional JWT auth (`spring.security.enabled=true`)
- **Observability** — Prometheus metrics, MDC correlation IDs, OpenTelemetry agent in Docker
- **Quality gates** — SpotBugs, OWASP dependency-check, JaCoCo, formatter (Maven verify)

See [docs/features.md](docs/features.md) for a full breakdown.

---

## Tech Stack

| Category        | Technology                    |
|----------------|-------------------------------|
| Runtime        | Java 21                        |
| Framework      | Spring Boot 3.1.4              |
| Build          | Maven 3.6+ (wrapper included)  |
| Database       | MySQL 8, Flyway                |
| Cache          | Redis, Spring Cache            |
| Messaging      | Apache Kafka                   |
| API docs       | SpringDoc OpenAPI              |
| Testing        | JUnit 5, Mockito, Cucumber, Testcontainers, RestAssured |

---

## Prerequisites

- **JDK 21** (required)
- **Docker & Docker Compose** (local stack and integration tests)
- **Git**
- **Make** (optional but recommended)

---

## Quick Start

```bash
git clone https://github.com/olx-india/java-service-template-clean-architecture.git
cd java-service-template-clean-architecture

export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS
cp .env.example .env                                # optional

make dev
```

`make dev` loads `.env`, starts Docker infrastructure (MySQL, Redis, Kafka), runs Flyway migrations, and starts the Spring Boot server.

**Manual steps** (if you prefer):

```bash
make docker-up
make migrate
make run
```

API: **http://localhost:8080**  
Swagger UI: **http://localhost:8080/swagger-ui/index.html**  
Actuator (metrics): **http://localhost:8081/metrics**

Use the `X-Default-Tenant: default` header on API requests.

More detail: [docs/local-setup.md](docs/local-setup.md)

---

## Project Structure

```
├── src/main/java/com/olx/boilerplate/
│   ├── controller/           # REST API, DTOs
│   ├── usecase/              # Application use cases and commands
│   ├── domain/               # Entities, ports, domain exceptions
│   │   └── repository/       # Repository port interfaces
│   └── infrastructure/       # JPA, Redis, Kafka, config, security
├── src/test/
│   ├── java/.../ut/          # Unit tests + ArchUnit
│   └── java/.../it/          # Cucumber + Testcontainers integration tests
├── docs/                     # Architecture, features, setup, runbook
├── docker-compose-local.yml  # MySQL, Redis, Kafka, OTel collector
├── Makefile                  # build, test, dev, migrate
└── pom.xml
```

---

## Architecture

See **[docs/clean-architecture.md](docs/clean-architecture.md)** for layer responsibilities, multi-tenancy, caching, events, and security. Component-level detail is in **[docs/low-level-design.md](docs/low-level-design.md)**.

| Layer            | Responsibility |
|------------------|----------------|
| **Controller**   | HTTP, validation, DTO ↔ command mapping |
| **Use case**     | Application workflows |
| **Domain**       | Entities, ports, business rules |
| **Infrastructure** | Adapters and framework wiring |

---

## Configuration

- **Profiles:** `local`, `integration-test` — see `src/main/resources/application*.yaml`
- **Environment variables:** Copy [`.env.example`](.env.example) — `DB_HOST`, `REDIS_HOST`, `KAFKA_HOST`, `SCHEMAS_TO_MIGRATE`, etc.
- **JWT security (optional):** Set `spring.security.enabled=true` and `security.jwt.secret`

---

## Makefile commands

```bash
make dev          # Load .env, start infra, migrate, run server
make build        # Package (skip integration tests)
make test         # Unit tests
make it           # Cucumber integration tests
make verify       # Verify with SpotBugs + formatter (skips OWASP; matches CI)
make verify-all   # Full verify including OWASP dependency-check (slow)
make spotbugs     # SpotBugs only (spotbugs-exclude-filter.xml)
make dependency-check  # OWASP only (owasp-dependency-check-suppressions.xml)
make security     # SpotBugs + OWASP
make docker-up    # Start infrastructure containers only
make docker-down  # Stop containers
make migrate      # Run Flyway migrations (loads .env)
make format       # Apply Eclipse formatter
```

---

## Testing

```bash
make test          # Unit tests only
make it            # Cucumber integration tests (Testcontainers + Docker)
make verify        # SpotBugs + formatter (CI-like; skips OWASP)
make verify-all    # Full verify including OWASP (slow)
make security      # SpotBugs + OWASP dependency-check
```

Integration tests spin up **MySQL, Redis, and Kafka** via Testcontainers — no private dependencies required.

---

## Development

```bash
make format           # Apply Eclipse formatter
make spotbugs         # SpotBugs (uses spotbugs-exclude-filter.xml)
make dependency-check # OWASP dependency-check (uses suppressions XML; slow)
make security         # Both SpotBugs and OWASP
```

---

## Docker

```bash
make build
make docker-up     # MySQL, Redis, Kafka, OTel (infra only — use make run for the app)
```

To run the packaged app in Docker as well:

```bash
docker compose -f docker-compose-local.yml up -d
```

---

## Documentation

| Topic | Link |
|-------|------|
| Local setup | [docs/local-setup.md](docs/local-setup.md) |
| Features | [docs/features.md](docs/features.md) |
| Clean architecture | [docs/clean-architecture.md](docs/clean-architecture.md) |
| Low-level design | [docs/low-level-design.md](docs/low-level-design.md) |
| Runbook | [docs/runbook.md](docs/runbook.md) |
| All docs | [docs/README.md](docs/README.md) |
| AI agents (Cursor / Claude) | [AGENTS.md](AGENTS.md) · [docs/ai-agents.md](docs/ai-agents.md) |

---

## FAQ

### Build fails with Lombok or “Unsupported class file major version”

Use **JDK 21**: `export JAVA_HOME=$(/usr/libexec/java_home -v 21)` and use `./mvnw -s settings.xml` if your global Maven settings point to a private repository.

### How do I add a new API?

1. Add domain entity and repository port under `domain/`
2. Add command + use case under `usecase/`
3. Add request/response DTOs and controller
4. Implement repository adapter in `infrastructure/data/repository/`
5. Add unit tests and a Cucumber scenario

### Rename the template for your service

Replace `com.olx.boilerplate` package and `boilerplate` artifact in `pom.xml` with your namespace.

---

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md), [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md), and [SECURITY.md](SECURITY.md).

Template change history (upstream, by date): [REPO_CHANGELOG.md](REPO_CHANGELOG.md)

---

## License

Apache License 2.0 — see [LICENSE](LICENSE).
