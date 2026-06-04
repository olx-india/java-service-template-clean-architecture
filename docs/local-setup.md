# Local Setup

Run the service on your machine with Docker-backed infrastructure and the Spring Boot dev server.

## Prerequisites

| Requirement | Notes |
|-------------|-------|
| **JDK 21** | Required; other versions will fail the build |
| **Docker & Docker Compose** | For MySQL, Redis, Kafka, and OTel collector |
| **Git** | Clone the repository |
| **Make** | Optional but recommended |

Verify Java:

```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 21)   # macOS
java -version   # must show 21.x
```

## One-command start

```bash
git clone https://github.com/olx-india/java-service-template-clean-architecture.git
cd java-service-template-clean-architecture

cp .env.example .env    # adjust values if needed
make dev
```

`make dev` does the following in order:

1. **Loads `.env`** — exports `DB_HOST`, `REDIS_HOST`, `KAFKA_HOST`, and other variables
2. **Starts infrastructure** — MySQL, Redis, Zookeeper, Kafka, and OTel collector (not the packaged Docker app)
3. **Runs migrations** — Flyway via `./migrate.sh` for schemas in `SCHEMAS_TO_MIGRATE`
4. **Starts the server** — Spring Boot on profile `SPRING_PROFILE` (default: `local`)

## Environment variables

Copy [`.env.example`](../.env.example) to `.env`:

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_HOST` | Master JDBC URL | `jdbc:mysql://localhost:3306/olxin` |
| `DB_REPLICA_HOST` | Replica JDBC URL | same as master locally |
| `DB_USERNAME` | Database user | `root` |
| `DB_PASSWORD` | Database password | `testDBPassword` |
| `REDIS_HOST` | Redis hostname | `localhost` |
| `KAFKA_HOST` | Kafka bootstrap servers | `localhost:9092` |
| `DEFAULT_SCHEMA` | Schema name for tenant `default` | `public` |
| `SCHEMAS_TO_MIGRATE` | Comma-separated Flyway schemas | `olxin` |
| `IT_MODE` | Integration-test delay flag | `false` |
| `SPRING_PROFILE` | Active Spring profile | `local` |

Optional JWT (uncomment in `.env`):

```bash
SPRING_SECURITY_ENABLED=true
SECURITY_JWT_SECRET=change-me-in-production
```

## Manual step-by-step

If you prefer separate commands:

```bash
cp .env.example .env
set -a && source .env && set +a   # bash/zsh

make docker-up    # start infrastructure
make migrate      # apply Flyway migrations
make run          # start Spring Boot
```

Stop infrastructure:

```bash
make docker-down
```

## Endpoints

| URL | Purpose |
|-----|---------|
| http://localhost:8080 | API |
| http://localhost:8080/swagger-ui/index.html | Swagger UI |
| http://localhost:8080/health | App health ping |
| http://localhost:8081/health/liveness | Liveness probe |
| http://localhost:8081/health/readiness | Readiness probe |
| http://localhost:8081/metrics | Prometheus metrics |

## Calling the API

All API requests need a tenant header:

```bash
curl -H "X-Default-Tenant: default" http://localhost:8080/user
```

Create a user:

```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -H "X-Default-Tenant: default" \
  -d '{"name":"Jane","email":"jane@example.com"}'
```

## IDE setup

- Import as a **Maven** project
- Set project SDK to **JDK 21**
- Enable **annotation processing** (Lombok)

## Running tests

```bash
make test     # unit tests
make it       # Cucumber + Testcontainers (Docker required)
make verify   # full Maven verify including SpotBugs and formatter
```

Integration tests start their own MySQL, Redis, and Kafka containers — no `.env` or `make docker-up` needed.

## Troubleshooting

| Symptom | Fix |
|---------|-----|
| `Missing .env` | Run `cp .env.example .env` |
| Build fails with wrong Java version | Set `JAVA_HOME` to JDK 21 |
| Port 8080 in use | Stop other apps or the `myapp` Docker service |
| 400 No tenant present | Add header `X-Default-Tenant: default` |
| DB connection refused | Run `make docker-up` and check `DB_HOST` in `.env` |
| Maven cannot resolve deps | Use `./mvnw -s settings.xml` |

See [runbook.md](runbook.md) for deploy and operations guidance.
