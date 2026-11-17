# Changelog

Tracks **upstream template** changes by date. Maintainers also cut versioned GitHub releases (`vX.Y.Z` tags, built by `.github/workflows/release.yml`) for meaningful template changes; release notes summarise the dated entries below.

**Contributors:** add bullets under today's date (create a new `## YYYY-MM-DD` section if needed). Group by Added / Changed / Fixed / Removed.

**Fork owners:** keep this file for upstream template history when syncing, or replace it with your service release notes as you prefer.

---

## 2026-11-17

### Changed

- **README highlights `make init`**: the one-command bootstrap is now a callout near the top and the first item under "Why this template".
- **`make init` drops template-only files**: `scripts/reset-template.sh` now also removes `CITATION.cff`, the social preview image, the Google Search Console override (`overrides/`) and the MkDocs docs site (`mkdocs.yml`, `requirements-docs.txt`, `scripts/mkdocs_hooks.py`, `.github/workflows/docs.yml`, and the MkDocs `.gitignore` entries). This step runs even when the samples were already removed.

## 2026-11-16

### Added

- **Google Search Console verification**: `overrides/main.html` (set as the MkDocs theme `custom_dir`) adds the `google-site-verification` meta tag to every docs page. Keep it, because Google re-checks it. `docs.yml` now also runs when `overrides/**` changes.

- **Documentation site**: `mkdocs.yml` (MkDocs Material) builds `docs/` into a site on GitHub Pages, deployed by the new `.github/workflows/docs.yml`, which also checks the build with `--strict` on pull requests. `scripts/mkdocs_hooks.py` rewrites links that leave `docs/` (e.g. `../AGENTS.md`) to GitHub URLs on the site. Pinned tooling lives in `requirements-docs.txt`.
- **Social preview image**: `docs/assets/social-preview.png` (1280×640) for the repository's social card.
- **`CITATION.cff`** so the template can be cited and indexed.

- **`make init`**: one command to start a new service from the template. It removes the sample User/Order features (domain, use cases, controllers, adapters, tests, features, seed SQL), the Redis/Kafka demo `ClientController` with its tests and Cucumber feature, and `PageQuery` / `PageResult` / `PageResponse`. It keeps the package layout (`.gitkeep` in emptied layer directories) and platform pieces. `EventPublisher` becomes a generic `publish(eventType, event)`. It then renames package/artifact/main class, sets `service.name` to the artifactId, formats the code and runs unit tests. New `scripts/reset-template.sh` and `scripts/lib/common.sh`; see `docs/template-setup.md`.
- **`make rename`**: wraps `scripts/rename-template.sh`. Both targets take `PACKAGE`, `ARTIFACT`, `APP`, `DRY_RUN=1` (preview) and `FORCE=1` (allow a dirty git tree).

### Changed

- **README**: prominent docs-site callout and "Read the docs" / "Use this template" buttons at the top, and a docs-site link heading the Documentation section.
- **README**: keyword-first title and intro (Spring Boot 4, Clean Architecture, hexagonal, microservice template), "Use this template" and docs-site links, Docs and stars badges, and a comparison table against Spring Initializr and typical hexagonal samples.

- **`scripts/rename-template.sh`**: validates inputs, supports `DRY_RUN`/`FORCE`, also renames `BoilerplateApplicationTests`, updates `Dockerfile` and `.mdc` files, and no longer corrupts Java identifiers that contain the artifact name. `OpenApiConfig` bean method renamed to `serviceOpenApi`.

- **Dependency bumps (Dependabot #49–#62)** — Spring Boot `4.1.1`; Guava `33.7.1-jre`; Hibernate Validator `9.1.4.Final`; OTel OTLP exporter `1.66.0`; httpmime `4.5.14`; ASM `9.10.1`; JaCoCo `0.8.15`; maven-javadoc-plugin `3.12.0`; maven-release-plugin `3.3.1`; Maven `3.9.16` / wrapper `3.3.4`; `actions/setup-java@v6.2.0`; `docker/setup-buildx-action@v4`

### Fixed

- **Spring Boot back to `4.0.9`** (from `4.1.1`): Spring Cloud has no GA release for Boot 4.1 yet, so the app failed to start (`Spring Boot [4.1.1] is not compatible with this Spring Cloud release train`). `spring-cloud-commons` `5.0.0` → `5.0.4`. Boot 4.0.9 manages FreeMarker `2.3.35`, which fixes the CRITICAL Trivy finding CVE-2026-84939 in `app.jar`, and Tomcat `11.0.27`, so the old `tomcat.version` pin (`11.0.26`) is removed.

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
