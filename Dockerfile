# Multi-stage runtime image for the service
ARG IMAGE_PREFIX=""
ARG OTEL_JAVA_AGENT_VERSION=2.31.1
ARG JAR_FILE=target/boilerplate-0.1.0.jar

FROM ${IMAGE_PREFIX}eclipse-temurin:21-jdk-noble AS builder
ARG OTEL_JAVA_AGENT_VERSION
RUN apt-get update && apt-get install -y --no-install-recommends curl \
  && mkdir -p /opt/otel \
  && curl -fsSL -o /opt/otel/opentelemetry-javaagent.jar \
    "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_JAVA_AGENT_VERSION}/opentelemetry-javaagent.jar" \
  && rm -rf /var/lib/apt/lists/*

FROM ${IMAGE_PREFIX}eclipse-temurin:21-jre-noble

# Noble images already ship uid/gid 1000 (ubuntu); reuse when present.
RUN apt-get update && apt-get install -y --no-install-recommends curl \
  && (getent group 1000 >/dev/null || groupadd --gid 1000 app) \
  && (id -u 1000 >/dev/null 2>&1 || useradd --uid 1000 --gid 1000 --shell /bin/bash app) \
  && mkdir -p /app/scripts /opt/otel \
  && rm -rf /var/lib/apt/lists/*

COPY --from=builder --chown=1000:1000 /opt/otel/opentelemetry-javaagent.jar /opt/otel/opentelemetry-javaagent.jar

ARG JAR_FILE
COPY --chown=1000:1000 ${JAR_FILE} /app/app.jar
COPY --chown=1000:1000 entrypoint.sh /app/scripts/
RUN chmod +x /app/scripts/entrypoint.sh

WORKDIR /app

ENV OTEL_SERVICE_NAME="my-spring-app"
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://otel-collector:4318"
ENV OTEL_METRICS_EXPORTER="none"
ENV OTEL_TRACES_EXPORTER="otlp"
ENV OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf"
ENV OTEL_TRACES_SAMPLER="parentbased_traceidratio"
ENV OTEL_TRACES_SAMPLER_ARG=0.1

EXPOSE 8080 8081

HEALTHCHECK --interval=30s --timeout=5s --start-period=60s --retries=3 \
  CMD curl -fsS http://127.0.0.1:8081/actuator/health/readiness || exit 1

USER 1000

CMD ["/app/scripts/entrypoint.sh"]
