.PHONY: build test it verify verify-all spotbugs dependency-check security run dev docker-up docker-down migrate format

MVN = ./mvnw -s settings.xml
JAVA21 = export JAVA_HOME=$$(/usr/libexec/java_home -v 21 2>/dev/null || echo "$$JAVA_HOME");
COMPOSE = docker compose -f docker-compose-local.yml
# Infrastructure only — excludes the packaged `myapp` service so `make run` can bind port 8080
INFRA_SERVICES = db redis zookeeper kafka otel-collector

build:
	$(JAVA21) $(MVN) clean package -DskipIntegration=true -DskipDependenciesCheck=true

test:
	$(JAVA21) $(MVN) test -DskipIntegration=true -DskipDependenciesCheck=true -DskipVerifications=true

it:
	$(JAVA21) $(MVN) integration-test -DskipUnitTests=true -DskipDependenciesCheck=true -DskipVerifications=true

verify:
	$(JAVA21) $(MVN) clean verify -DskipDependenciesCheck=true

# Full verify including OWASP dependency-check (slow; uses owasp-dependency-check-suppressions.xml)
verify-all:
	$(JAVA21) $(MVN) clean verify -DskipIntegration=true

spotbugs:
	$(JAVA21) $(MVN) spotbugs:check

dependency-check:
	$(JAVA21) $(MVN) dependency-check:check

security: spotbugs dependency-check

run:
	$(JAVA21) $(MVN) spring-boot:run -Dspring-boot.run.profiles=local

# One command: load .env, start infra, migrate, run the app locally
dev:
	@test -f .env || (echo "Missing .env — run: cp .env.example .env" && exit 1)
	@set -a && . ./.env && set +a && \
	$(JAVA21) \
	echo "==> Starting infrastructure ($(INFRA_SERVICES))..." && \
	$(COMPOSE) up -d $(INFRA_SERVICES) && \
	echo "==> Waiting for MySQL..." && \
	for i in $$(seq 1 30); do \
		$(COMPOSE) exec -T db mysqladmin ping -h localhost -uroot -p"$$DB_PASSWORD" --silent 2>/dev/null && break; \
		sleep 2; \
	done && \
	echo "==> Running database migrations..." && \
	SCHEMAS_TO_MIGRATE=$${SCHEMAS_TO_MIGRATE:-olxin} ./migrate.sh && \
	echo "==> Starting server (profile: $${SPRING_PROFILE:-local})..." && \
	$(MVN) spring-boot:run -Dspring-boot.run.profiles=$${SPRING_PROFILE:-local}

docker-up:
	$(COMPOSE) up -d $(INFRA_SERVICES)

docker-down:
	$(COMPOSE) down

migrate:
	@test -f .env && set -a && . ./.env && set +a; \
	SCHEMAS_TO_MIGRATE=$${SCHEMAS_TO_MIGRATE:-olxin} ./migrate.sh

format:
	$(JAVA21) $(MVN) formatter:format
