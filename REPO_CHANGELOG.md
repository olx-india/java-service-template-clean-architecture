# Template repository changelog

Tracks **upstream template** changes by date. This repo does not use versioned releases — maintainers append entries when the template changes.

**Contributors:** add bullets under today's date (create a new `## YYYY-MM-DD` section if needed). Group by Added / Changed / Fixed / Removed.

**Template users:** use this file to see what changed in the template when updating a fork. Keep your own service history in [CHANGELOG.md](CHANGELOG.md).

---

## 2026-06-05

### Added

- **`AGENTS.md`** — root agent quick reference (Cursor, Claude, other tools)
- **`CLAUDE.md`** — Claude Code entry point
- **`docs/ai-agents.md`** — detailed agent workflows, prompts, and file map
- **`.cursor/rules/`** — Cursor project rules (template core, Java layers, tests/IT)
- **`make dev`** — loads `.env`, starts Docker infrastructure, runs Flyway migrations, and starts the Spring Boot server
- **`docs/`** — local setup, features, clean architecture, low-level design, runbook, transactional outbox
- **Transactional outbox pattern** — `OutboxEventPublisher` + `OutboxRelayScheduler` relay worker
- **Outbox relay configuration** — `outbox.relay.*` (enabled, poll interval, batch size, event-type → topic mapping)
- **Flyway index** — `V1.3__Outbox_Event_Index.sql`
- **`.env.example`** — `SCHEMAS_TO_MIGRATE`
- **Unit tests** — outbox publisher, relay service, topic resolver
- **`docker-java.properties`** — Docker API 1.44 for Testcontainers on Docker Engine 29+
- **`REPO_CHANGELOG.md`** — dated template change log (this file)

### Changed

- **README** — expanded quick start, Makefile commands, links to `docs/`
- **`docker-up` / `make dev`** — infrastructure containers only (excludes packaged `myapp`)
- **`migrate` Makefile target** — loads `.env` when present
- **Root docs** — `ARCHITECTURE.md` and `RUNBOOK.md` moved under `docs/`
- **Event publishing** — outbox write path instead of direct Kafka publish
- **`CreateUser`** — `@Transactional` for atomic user + outbox insert
- **Outbox packages** — `data/entities`, `data/repository`, `outbox/`, `outbox/relay/`
- **`Tenant`** — opaque site codes (e.g. `default`)
- **`User` / `Order`** — `Serializable` for Redis cache
- **Testcontainers** — 1.21.4 for Docker 29.x
- **`resilience4j-retry`** — aligned with Resilience4j 2.x
- **Cucumber IT** — glue packages, step regex, integration-test profile tuning
- **`CHANGELOG.md`** — empty starter for forked services

### Fixed

- **Integration tests** — Docker detection, Spring context, Cucumber steps, tenant parsing, cache serialization
- **`ServiceDependencyModule`** — `IntervalFunction` import for Resilience4j 2.x
- **`Client.feature`** — host background for random-port IT runs

### Removed

- **`KafkaEventPublisher`** — replaced by outbox + relay
- **Root `ARCHITECTURE.md` and `RUNBOOK.md`** — superseded by `docs/`

---

## 2024-07-09

### Added

- Initial `CHANGELOG.md` scaffold (now the empty service changelog for template users)
