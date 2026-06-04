# Features

Production-ready capabilities included in this Spring Boot template.

## Architecture and quality

- **Clean Architecture** — Layered design with controller, use case, domain, and infrastructure packages
- **ArchUnit boundary tests** — Enforces that domain and use cases do not depend on outer layers
- **Use case pattern** — Commands and application services separate HTTP from business logic
- **Quality gates** — SpotBugs, OWASP dependency-check, JaCoCo coverage, Eclipse formatter (Maven verify)

## Data and persistence

- **Multi-tenant MySQL** — Database-per-tenant with read/write replica routing via `CustomRoutingDataSource`
- **Flyway migrations** — Versioned scripts in `common/` and tenant-specific folders
- **Transactional outbox** — `outbox_event` table + `OutboxRelayScheduler` relay worker; events written atomically with business data
- **JPA repositories** — Domain ports with infrastructure adapters and entity mapping

## Caching and messaging

- **Redis caching** — Spring Cache on use cases with actuator cache eviction endpoint
- **Kafka events** — Domain events via `EventPublisher` port (`UserCreatedEvent` example)
- **Configurable TTL** — Redis entry TTL via `redis.timeToLive`

## API and documentation

- **REST APIs** — User and order CRUD with pagination (`PageResponse`)
- **Validation** — Jakarta Bean Validation on request DTOs
- **OpenAPI / Swagger** — SpringDoc with annotated controllers; reference spec in [openapi.yaml](openapi.yaml)
- **Standard error handling** — `CustomExceptionHandler` with consistent error responses

## Resilience and integrations

- **External HTTP client** — OkHttp with circuit breaker, retry, and rate limiter (Resilience4j)
- **Per-client configuration** — Retry and circuit breaker settings in `application.yaml`

## Security

- **Optional JWT auth** — Enable with `spring.security.enabled=true` and `security.jwt.secret`
- **No-security profile** — Open endpoints by default for local development
- **Tenant headers** — `X-Default-Tenant` required on API requests

## Observability

- **Prometheus metrics** — Exposed on management port 8081 at `/metrics`
- **Health probes** — Liveness and readiness endpoints for Kubernetes
- **Correlation IDs** — `CorrelationIdFilter` adds `tid` / `cid` to structured JSON logs
- **OpenTelemetry** — Agent support in Dockerfile; OTel collector in local Docker Compose
- **Custom actuator endpoints** — Cache eviction and custom health extensions

## Testing

- **Unit tests** — JUnit 5 and Mockito for controllers and use cases
- **Integration tests** — Cucumber BDD scenarios with Testcontainers (MySQL, Redis, Kafka)
- **RestAssured** — HTTP assertions in integration step definitions

## Developer experience

- **Makefile** — `make dev` loads `.env`, starts infra, migrates, and runs the app
- **Docker Compose** — Local MySQL, Redis, Kafka, and OTel collector
- **Environment template** — `.env.example` documents required variables
- **Maven wrapper** — Reproducible builds without a global Maven install

## Tech stack summary

| Category | Technology |
|----------|------------|
| Runtime | Java 21 |
| Framework | Spring Boot 3.1 |
| Build | Maven 3.6+ |
| Database | MySQL 8, Flyway |
| Cache | Redis, Spring Cache |
| Messaging | Apache Kafka |
| API docs | SpringDoc OpenAPI |
| Testing | JUnit 5, Mockito, Cucumber, Testcontainers, RestAssured |
