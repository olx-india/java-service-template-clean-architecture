# Define Variables
ARG IMAGE_PREFIX=""
FROM ${IMAGE_PREFIX}openjdk:17-jdk-slim

# Install curl
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Define non-root user
RUN groupadd --gid 1000 userEntity \
  && useradd --uid 1000 --gid 1000 --shell /bin/bash userEntity

# Expose application port (8080)
EXPOSE 8080

# Create necessary directories
RUN mkdir -p /app/scripts /opt/otel

# Download OpenTelemetry Java Agent
RUN curl -L -o /opt/otel/opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.11.1/opentelemetry-javaagent.jar

# Copy application JAR file
COPY --chown=1000:1000 target/boilerplate-0.1.0.jar /app/app.jar

# Copy Entrypoint Script
COPY --chown=1000:1000 entrypoint.sh /app/scripts/
RUN chmod +x /app/scripts/entrypoint.sh

# Set working directory
WORKDIR /app

# Set OpenTelemetry environment variables
ENV OTEL_SERVICE_NAME="my-spring-app"
ENV OTEL_EXPORTER_OTLP_ENDPOINT="http://otel-collector:4318"
ENV OTEL_METRICS_EXPORTER="none"
ENV OTEL_TRACES_EXPORTER="otlp"
ENV OTEL_EXPORTER_OTLP_PROTOCOL="http/protobuf"
ENV OTEL_TRACES_SAMPLER="parentbased_traceidratio"
ENV OTEL_TRACES_SAMPLER_ARG=0.1

# Run as non-root user
USER 1000

# Run entrypoint script
CMD ["/app/scripts/entrypoint.sh"]
