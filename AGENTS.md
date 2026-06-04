# AI agent instructions

Instructions for **Cursor**, **Claude Code**, and other coding agents working on this repository.

**Full guide:** [docs/ai-agents.md](docs/ai-agents.md)

## Project

Spring Boot 3.1 **Clean Architecture** Java template (`com.olx.boilerplate`). JDK **21** only. Maven wrapper: `./mvnw -s settings.xml`.

## Before you change code

1. Read [docs/clean-architecture.md](docs/clean-architecture.md) for layer rules.
2. Prefer **minimal diffs** — match existing naming, packages, and patterns.
3. Run **`make test`** after logic changes; **`make it`** if IT/Cucumber/Docker/Testcontainers touched (requires Docker).
4. Template maintainer changes → update **[REPO_CHANGELOG.md](REPO_CHANGELOG.md)** by date. Do **not** fill **[CHANGELOG.md](CHANGELOG.md)** (empty for fork owners).

## Layer rules (enforced by ArchUnit)

| Layer | Package | May depend on |
|-------|---------|---------------|
| Domain | `domain` | domain only (no Spring/JPA/Kafka) |
| Use case | `usecase` | `domain` |
| Controller | `controller` | `usecase`, `domain` |
| Infrastructure | `infrastructure` | `domain` (implements ports) |

- Controllers: HTTP + validation + DTO ↔ command mapping only.
- Use cases: orchestration; inject **ports** (`domain.repository.*`, `domain.port.*`), not JPA/Kafka classes.
- Infrastructure: adapters under `data/`, `components/`, `outbox/`, `appConfig/`.

## Common tasks

### Add a REST endpoint

1. `domain` — entity + repository port (if new aggregate)
2. `usecase` — command + `@Component` use case; `@Transactional` on writes that emit events
3. `controller` — request/response DTOs + controller; `@ReadOnlyTransaction` / `@ReadWriteTransaction`
4. `infrastructure/data` — JPA entity + `*RepositoryImpl` + Spring Data interface
5. `db/migration/common/` — Flyway SQL if schema changes
6. Tests — unit under `ut/`; optional Cucumber under `features_and_scenarios/`
7. API calls need header **`X-Default-Tenant: default`**

### Add a domain event (outbox)

1. Event class + constant in `domain/event/`
2. Method on `EventPublisher` port
3. Implement in `infrastructure/outbox/OutboxEventPublisher`
4. Map topic in `outbox.relay.topics` (`application.yaml`)
5. Call from use case inside **`@Transactional`** write method

See [docs/outbox-pattern.md](docs/outbox-pattern.md).

## Commands

```bash
cp .env.example .env && make dev   # local stack + server
make test                          # unit tests
make it                            # Cucumber + Testcontainers
make verify                        # full Maven verify
make format                        # Eclipse formatter
```

## Do not

- Put business logic in controllers or JPA entities
- Import `infrastructure` from `usecase` or `domain`
- Publish to Kafka directly from use cases (use `EventPublisher` → outbox)
- Use `usercase` package name (use **`usecase`**)
- Start `myapp` Docker service when running `make run` locally (`make docker-up` is infra-only)
- Commit secrets; use `.env` (gitignored) from `.env.example`

## Key paths

| Topic | Location |
|-------|----------|
| Docs index | [docs/README.md](docs/README.md) |
| Local setup | [docs/local-setup.md](docs/local-setup.md) |
| LLD | [docs/low-level-design.md](docs/low-level-design.md) |
| IT config | `src/test/java/.../it/config/CucumberSpringConfiguration.java` |
| Cucumber glue | `it.config` + `it.stepdefinition` (sibling packages, not nested) |
| ArchUnit | `src/test/java/.../ut/ArchitectureTest.java` |

## Cursor

Project rules live in **`.cursor/rules/`** (loaded automatically in Cursor).

## Claude Code

See **[CLAUDE.md](CLAUDE.md)** for Claude-specific workflow notes.
