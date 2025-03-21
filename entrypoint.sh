#!/bin/sh

set -e  # Exit on error

echo "🚀 Starting application..."

# Default JVM options
DEFAULT_OPTS="-Xms512m -Xmx512m -Dspring.profiles.active=${SPRING_PROFILE:-default}"

# Allow overrides via environment variables
OPTS="${OVERRIDE_OPTS:-${DEFAULT_OPTS}}"
APP_OPTS=${APP_OPTS:-""}

# Check if OpenTelemetry agent exists
OTEL_JAR_PATH="/opt/otel/opentelemetry-javaagent.jar"

if [ -f "$OTEL_JAR_PATH" ]; then
    echo "🟢 OpenTelemetry Java Agent found, enabling it..."
    OTEL_ARG="-javaagent:$OTEL_JAR_PATH"
else
    echo "⚠️ OpenTelemetry Java Agent not found, running without it."
    OTEL_ARG=""
fi

# Run the application with OpenTelemetry
exec java $OTEL_ARG $OPTS -jar /app/app.jar $APP_OPTS

