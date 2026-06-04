# Runbook

## Deploy

1. Build: `make build` or `mvn -s settings.xml clean package -DskipIntegration=true`
2. Run migrations: `make migrate` (or `./migrate.sh` with env vars set)
3. Start: `java -jar target/boilerplate-0.1.0.jar --spring.profiles.active=local`
4. Or use Docker: `docker compose -f docker-compose-local.yml up -d`

## Health checks

- Liveness: `GET http://localhost:8081/health/liveness`
- Readiness: `GET http://localhost:8081/health/readiness`
- App ping: `GET http://localhost:8080/health`

## Migrations

Multi-tenant Flyway layout:

- `src/main/resources/db/migration/common/` — shared schema
- `src/main/resources/db/migration/<tenant>/` — tenant-specific seeds

Run `make migrate` (loads `.env`) or `./migrate.sh` with `SCHEMAS_TO_MIGRATE` set.

## Observability

- Prometheus scrape: `http://localhost:8081/metrics`
- Clear caches: `POST http://localhost:8081/cache-evict`
- Logs: JSON to stdout with `tid` and `cid` correlation fields

## Troubleshooting

| Symptom | Check |
|---------|--------|
| 400 No tenant present | Send header `X-Default-Tenant: default` |
| DB connection refused | `docker compose ps`, verify `DB_HOST` in `.env` |
| Kafka publish fails | Ensure Kafka is up (`make docker-up`) and `KAFKA_HOST` is set |
| Build uses wrong JDK | `java -version` must show 21; set `JAVA_HOME` |
| Maven cannot resolve deps | Use `./mvnw -s settings.xml` (project settings use Maven Central) |

## Security

- JWT disabled by default. Enable with `spring.security.enabled=true` and set `security.jwt.secret`.
- Run OWASP check before release: `make dependency-check` or `make security`
