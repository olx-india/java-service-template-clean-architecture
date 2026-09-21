# Changelog

Tracks **upstream template** changes by date. This repo does not use versioned releases — maintainers append entries when the template changes.

**Contributors:** add bullets under today's date (create a new `## YYYY-MM-DD` section if needed). Group by Added / Changed / Fixed / Removed.

**Fork owners:** keep this file for upstream template history when syncing, or replace it with your service release notes as you prefer.

---

## 2026-09-19

### Added

- **AppLogger facade** — `com.olx.boilerplate.logging` (`AppLogger` / `AppLoggers`); SLF4J adapter under `infrastructure.logging`. Domain ports stay business-only (`EventPublisher`, repositories). ArchUnit forbids direct SLF4J outside the adapter package.
- **OIDC security mode** — `security.mode=oidc` with Spring OAuth2 resource server; HMAC JWT remains `security.mode=hmac`
- **API versioning** — sample APIs under `/api/v1/users` and `/api/v1/orders`
- **Domain pagination** — `PageQuery` / `PageResult` on repository ports (no Spring Data in domain)
- **OrderCreatedEvent** outbox parity; relay uses `FOR UPDATE SKIP LOCKED` + Micrometer counters
- **RFC 7807 ProblemDetail** error responses
- **CI** — verify job runs `mvn verify -DskipIntegration=true` (skips OWASP); IT stage unchanged; weekly OWASP; container Trivy scan; `.gitlab-ci.yml`
- **CycloneDX SBOM** on verify; `scripts/rename-template.sh`; ADRs; CODEOWNERS; MAINTAINERS
- **Virtual threads** — `spring.threads.virtual.enabled=true`

### Changed

- **Dockerfile** — Temurin 21 multi-stage JRE on Ubuntu Noble (fewer CRITICAL OS CVEs than Jammy), pinned OTel agent, HEALTHCHECK
- **Dockerfile.Migrate** — public `flyway/flyway` default
- **TenantFilter** — clears tenant context; JSON 400 without tenant; skips health/swagger
- **ClientController** — `@Profile("local")` only
- **JaCoCo** UT floor raised; Cucumber/RestAssured/Surefire bumped; SpotBugs plugin to 4.8.6.6; Toxiproxy dependency removed
- **Changelog** — template history lives in `CHANGELOG.md` (removed `REPO_CHANGELOG.md`)
- **README.md** — OSS-oriented rewrite: evidence-led pitch, mermaid layer diagram, CI two-stage docs
- **docs/README.md** / **docs/features.md** — aligned with platform upgrades
- **CONTRIBUTING.md** — maintainer notes for suggested GitHub About description and topics

### Fixed

- **`.env` gitignored** (was documented but missing)
- **otel-config.yml** rename (was `otel-confg.yml`)
- **CI** — formatter after main merge (`SecurityConfig`); Trivy action `@v0.36.0` (fixes broken `setup-trivy@v0.2.1` pin in `@v0.28.0`); Dockerfile copies explicit boot JAR
- **Jackson 3** — inject `tools.jackson.databind.ObjectMapper` (Boot 4 auto-config); fixes IT `TenantFilter` / outbox wiring
- **Container scan CVEs** — Boot `4.0.8` + Tomcat `11.0.26`; OTel Java agent `2.31.1` (fixes CRITICAL Trivy jar findings)
- **Cucumber** — host step no longer embeds `http://.../` (Cucumber 7 treats `/` as alternatives)
- **ClientController** — also active on `integration-test` so Redis/Kafka scenarios are mapped
- **Dependency bumps** — springdoc `3.1.1`, OkHttp `5.5.0` (`okhttp-jvm`), Retrofit `3.0.0`, RestAssured `6.0.1`, WireMock `3.13.2`, Lombok `1.18.48`, Tika `4.0.0`, OWASP dependency-check `13.0.0`, cucumber-messages `34.2.1`, extentreports-cucumber4-adapter `1.2.1`
