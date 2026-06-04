# AI agents guide

How to work efficiently on this template with **Cursor**, **Claude Code**, and similar coding agents.

| Tool | Entry point |
|------|-------------|
| Any agent | [AGENTS.md](../AGENTS.md) (repo root) |
| Claude Code | [CLAUDE.md](../CLAUDE.md) |
| Cursor | [AGENTS.md](../AGENTS.md) + [.cursor/rules/](../.cursor/rules/) |

---

## What agents should read first

1. **[AGENTS.md](../AGENTS.md)** — constraints, commands, do/don't (keep this open or `@`-mention it)
2. **[clean-architecture.md](clean-architecture.md)** — layer model and dependency rule
3. **Nearest existing feature** — e.g. User flow: `CreateUser`, `UserController`, `UserRepositoryImpl`
4. **[low-level-design.md](low-level-design.md)** — filters, caching, messaging, migrations
5. Task-specific: [outbox-pattern.md](outbox-pattern.md), [local-setup.md](local-setup.md)

---

## Repository map (for navigation)

```
com.olx.boilerplate/
├── controller/          REST + DTOs
├── usecase/             application services + commands
├── domain/              entities, ports, events, repository interfaces
└── infrastructure/
    ├── data/            JPA entities + repository adapters
    ├── outbox/          EventPublisher adapter + relay/
    ├── components/      Kafka, Redis, filters
    └── appConfig/       Spring configuration

src/test/
├── ut/                  unit + ArchUnit
└── it/                  Cucumber + Testcontainers
    ├── config/          @CucumberContextConfiguration
    └── stepdefinition/

docs/                    human + agent documentation
AGENTS.md                agent quick reference (root)
REPO_CHANGELOG.md        template changes by date (maintainers)
CHANGELOG.md             empty — for fork owners only
```

---

## Workflows for common agent tasks

### 1. Add a new REST resource

Copy the **User** or **Order** vertical slice.

| Step | Action |
|------|--------|
| 1 | Domain entity + `domain/repository/*Repository` port |
| 2 | Flyway script in `db/migration/common/` if new tables |
| 3 | `usecase/<feature>/` — command objects + use case class |
| 4 | `controller/dto/` — request/response; controller with transaction annotations |
| 5 | `infrastructure/data/` — `*Data` entity, JPA repo, `*RepositoryImpl` |
| 6 | `ut/` unit tests; optional `.feature` + step defs |
| 7 | Run `make test`; add Cucumber scenario → `make it` |

**Checklist prompt (paste to agent):**

> Add a `<Resource>` API following the existing User/Order pattern. Respect Clean Architecture and ArchUnit. Include Flyway migration, unit tests, and update REPO_CHANGELOG.md if this is an upstream template change.

### 2. Add a domain event

Follow [outbox-pattern.md](outbox-pattern.md).

| Step | Action |
|------|--------|
| 1 | Event class + `OutboxEventTypes` constant |
| 2 | Extend `EventPublisher` port |
| 3 | Implement in `OutboxEventPublisher` |
| 4 | Add `outbox.relay.topics` mapping |
| 5 | Call from `@Transactional` use case after persist |
| 6 | Unit tests for publisher; extend `init-it.sql` if IT creates rows |

### 3. Fix integration tests

| Symptom | Likely fix |
|---------|------------|
| Docker / Testcontainers fails | Docker running; Testcontainers 1.21.4+; `docker-java.properties` API 1.44 |
| Duplicate step definitions | Glue = `it.config` + `it.stepdefinition` only (not parent package) |
| Undefined Cucumber steps | Path steps need regex, not `{string}` for `/user` paths |
| HTTP 500 on GET | Domain entity `Serializable` for Redis cache; tenant header `default` |
| Missing table in IT | Add DDL to `src/test/resources/db/init-it.sql` |

Run: `make it`

### 4. Local run / debug

```bash
cp .env.example .env
make dev          # infra + migrate + server
make docker-down  # stop containers
```

See [local-setup.md](local-setup.md).

---

## Cursor

### Rules (auto-loaded)

| Rule file | Scope |
|-----------|--------|
| `.cursor/rules/template-core.mdc` | Always apply |
| `.cursor/rules/java-layers.mdc` | `src/main/java/**` |
| `.cursor/rules/tests-and-it.mdc` | `src/test/**` |

### Efficient Cursor usage

- **@AGENTS.md** or **@docs/clean-architecture.md** at the start of a chat for large tasks
- **@CreateUser.java** (or similar) as a pattern reference instead of describing structure in prose
- Prefer **Agent mode** for multi-file features; **Ask mode** for architecture questions
- After edits: ask agent to run `make test` or `make it` in terminal
- Add new **project rules** under `.cursor/rules/*.mdc` when you establish fork-specific conventions (keep under ~50 lines per rule)

### Example Cursor prompts

```
@AGENTS.md @UserController.java Add DELETE /orders/{id} mirroring user delete. Unit tests only.
```

```
@docs/outbox-pattern.md Add OrderCreatedEvent through the outbox. Update relay topic config and tests.
```

```
@ArchitectureTest.java @src/main/java/.../usecase/ Refactor without breaking ArchUnit rules.
```

---

## Claude Code

### Setup

- Open the repo root; Claude reads **CLAUDE.md** and **AGENTS.md**
- For long sessions, point Claude at **docs/ai-agents.md** for workflows

### Efficient Claude usage

- Request **layer-by-layer** PRs for large features (domain first, then use case, etc.)
- Ask Claude to **run Maven** and paste failures — common fixes are documented in AGENTS.md
- Use **explore → plan → implement** for unfamiliar areas (`docs/low-level-design.md` before deep infra changes)
- Maintainer template edits: remind Claude to update **REPO_CHANGELOG.md** with today's date

### Example Claude prompts

```
Read AGENTS.md and docs/clean-architecture.md. Propose a plan to add a Product entity and CRUD API matching User/Order conventions. Do not implement until I approve the plan.
```

```
Implement the approved plan. Run make test after each layer. Minimal diff; no unrelated refactors.
```

```
Integration tests fail with Testcontainers Docker error. Diagnose using AGENTS.md and fix with minimal changes.
```

---

## Quality gates agents must respect

| Gate | Command |
|------|---------|
| Unit tests + ArchUnit | `make test` |
| Cucumber IT | `make it` |
| Formatter + SpotBugs (CI-like) | `make verify` |
| Format only | `make format` |

Agents should **not** skip hooks or use `-DskipTests` unless the user explicitly asks.

---

## Documentation and changelog policy

| File | Agent should |
|------|----------------|
| `docs/*.md` | Update when behavior or setup changes (user-visible) |
| `REPO_CHANGELOG.md` | Append under current date for **template** changes |
| `CHANGELOG.md` | Leave empty (fork owner's file) |
| `AGENTS.md` / `.cursor/rules/` | Update when agent conventions or commands change |

---

## Anti-patterns (avoid)

- Generating a monolithic “service class” instead of use case + ports
- `@Autowired` field injection in new code (use constructor injection like existing classes)
- Direct `KafkaTemplate` in use cases
- Cucumber glue on both `it` and `it.stepdefinition` packages
- Putting maintainer history in `CHANGELOG.md`
- Editing only README when behavior changed — update relevant `docs/` page too

---

## Related links

- [Features](features.md)
- [Transactional outbox](outbox-pattern.md)
- [Runbook](runbook.md)
- [Template changelog](../REPO_CHANGELOG.md)
