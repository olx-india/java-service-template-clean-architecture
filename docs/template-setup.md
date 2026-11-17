# Template Setup

Turn this template into your own service with one command.

## Quick start

```bash
git clone https://github.com/olx-india/java-service-template-clean-architecture.git my-service
cd my-service

make init PACKAGE=com.acme.orders ARTIFACT=orders-service APP=OrdersApplication DRY_RUN=1   # preview
make init PACKAGE=com.acme.orders ARTIFACT=orders-service APP=OrdersApplication             # apply
git add -A && git commit -m "chore: initialise service from template"
```

`make init` runs these steps in order:

1. **Reset** (`scripts/reset-template.sh`): removes the sample User and Order features, the Redis/Kafka demo `ClientController` and the pagination helpers, and the template-only citation, SEO and docs-site files (`CITATION.cff`, `mkdocs.yml`, `requirements-docs.txt`, `overrides/`, `scripts/mkdocs_hooks.py`, `.github/workflows/docs.yml`, `docs/assets/social-preview.png`), and keeps the platform pieces and package layout.
2. **Rename** (`scripts/rename-template.sh`): renames the base package, Maven artifact and main application class, and sets `service.name` in `application.yaml` to `ARTIFACT`.
3. `make format`, then `make test`.

To rename without removing the samples (for example, to keep them as a reference while you learn the layout), use `make rename` with the same variables.

## Make targets

| Target | What it does |
|--------|--------------|
| `make init` | Reset samples, rename, format and run unit tests |
| `make rename` | Rename package / artifact / main class only; samples are kept |

## Variables

| Variable | Used by | Required | Default | Example | Meaning |
|----------|---------|----------|---------|---------|---------|
| `PACKAGE` | `init`, `rename` | yes | — | `com.acme.orders` | New base Java package (lowercase, dotted) |
| `ARTIFACT` | `init`, `rename` | yes | — | `orders-service` | Maven `artifactId`; also replaces `boilerplate` in pom, YAML, Dockerfile, CI workflows and docs |
| `APP` | `init`, `rename` | yes | — | `OrdersApplication` | Main `@SpringBootApplication` class (and its `<APP>Tests` test) |
| `DRY_RUN` | `init`, `rename` | no | `0` | `DRY_RUN=1` | Print the files that would be removed, rewritten or moved, and change nothing |
| `FORCE` | `init`, `rename` | no | `0` | `FORCE=1` | Run even if the git working tree has uncommitted changes |

When a required variable is missing, the target prints usage and exits. Without `FORCE=1`, both targets refuse to run on a dirty tree, so the whole change can be reviewed with `git diff`.

## What the reset removes

| Area | Removed |
|------|---------|
| Domain | `User`, `Order`, their repository ports, `UserCreatedEvent`, `OrderCreatedEvent`, `PageQuery`, `PageResult` |
| Use cases | `usecase/users/`, `usecase/order/` |
| Controllers | `UserController`, `OrderController`, `controller/dto/user/`, `controller/dto/order/`, `controller/dto/PageResponse`, the Redis/Kafka demo `ClientController` (`/test/redis`, `/test/kafka`) |
| Infrastructure | `UserData`, `OrderData`, JPA repositories and `*RepositoryImpl` adapters |
| Tests | User/Order/Client unit tests, controller tests, Cucumber features and step definitions |
| Database | `tbl_user` / `tbl_order` DDL (`V1.0__Initial_Schema.sql` becomes a placeholder), seed data `tenant/V1.1__Insert_Initial_Data.sql`, sample rows in IT SQL |

## What the reset rewrites

| File | After the reset |
|------|-----------------|
| `domain/port/EventPublisher` | Generic `publish(String eventType, Object event)` |
| `infrastructure/outbox/OutboxEventPublisher` | Implements the generic `publish` |
| `domain/event/OutboxEventTypes` | Empty constants holder |
| `infrastructure/outbox/relay/OutboxTopicResolver` | No sample-specific default topic |
| `application.yaml`, `application-local.yaml` | `outbox.relay.topics: {}` |
| Outbox unit tests | Use an inline example event instead of `UserCreatedEvent` |
| `ArchitectureTest` | Controller → infrastructure rule no longer exempts `ClientController` |

## What the reset keeps

- The package layout. Layer directories left empty get a `.gitkeep`: `usecase`, `domain/repository`, `controller/dto`, `ut/usecase`. Delete it once you add the first class.
- Health check controller, tenancy, security, outbox relay, Kafka, Redis, logging, observability.
- Shared types: `ResourceNotFoundException`.
- Outbox migrations (`V1.2`, `V1.3`), `ArchitectureTest`, and the HealthCheck Cucumber feature.

## Next: add your first feature

Follow [clean-architecture.md](clean-architecture.md) and the "Add a REST endpoint" steps in [AGENTS.md](../AGENTS.md):

1. `domain/`: entity and repository port
2. `usecase/<feature>/`: command and use case
3. `controller/`: request/response DTOs and controller under `/api/v1/...`
4. `infrastructure/data/`: JPA entity, Spring Data repository and `*RepositoryImpl`
5. `db/migration/common/`: Flyway migration (next version after `V1.3`)
6. Tests: unit tests under `ut/`; Cucumber feature under `features_and_scenarios/`

To publish an event from a use case, call `eventPublisher.publish(OutboxEventTypes.MY_EVENT, new MyEvent(...))` inside a `@Transactional` method and map the type in `outbox.relay.topics`. See [outbox-pattern.md](outbox-pattern.md).

The removed User/Order samples are still in the template's git history if you need a worked example.
