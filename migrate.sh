#!/bin/sh

SCHEMAS_TO_MIGRATE=${SCHEMAS_TO_MIGRATE:-""}
DB_HOST=${DB_HOST:-""}
DB_USERNAME=${DB_USERNAME:-""}
DB_PASSWORD=${DB_PASSWORD:-""}
M2_REPO=${M2_REPO:-".m2"}
REPAIR_ENABLED=${REPAIR_ENABLED:-"false"}
BASELINE_VERSION=${BASELINE_VERSION:-""}

# To make sure that the pwd is correct before executing the script
cd "${0%/*}" || exit 1

COMMON_SCRIPTS_LOCATION="filesystem:./src/main/resources/db/migration/common"

for SCHEMA in $(echo "$SCHEMAS_TO_MIGRATE" | sed "s/,/ /g"); do
  FLYWAY_SCRIPT_LOCATION=$COMMON_SCRIPTS_LOCATION

  # append site specific location only if it exists,
  # otherwise in future flyway versions an error will be thrown
  if [ -d "./src/main/resources/db/migration/$SCHEMA" ]; then
    FLYWAY_SCRIPT_LOCATION="$FLYWAY_SCRIPT_LOCATION,filesystem:./src/main/resources/db/migration/$SCHEMA"
  fi

  # Repairing is used to retry failed migration scripts
  # and updates the checksum of previous successful migration
  # files if contents are changed (without re-running it).
  if [ "$REPAIR_ENABLED" = "true" ]; then
      ./mvnw -s settings.xml \
        -Dmaven.repo.local="$M2_REPO" \
        -Dflyway.url="$DB_HOST" \
        -Dflyway.schemas="$SCHEMA" \
        -Dflyway.createSchemas=true \
        -Dflyway.user="$DB_USERNAME" \
        -Dflyway.password="$DB_PASSWORD" \
        -Dflyway.locations="$FLYWAY_SCRIPT_LOCATION" \
        flyway:repair
  fi

  if [ -n "$BASELINE_VERSION" ]; then
      ./mvnw -s settings.xml \
        -Dmaven.repo.local="$M2_REPO" \
        -Dflyway.url="$DB_HOST" \
        -Dflyway.schemas="$SCHEMA" \
        -Dflyway.createSchemas=true \
        -Dflyway.user="$DB_USERNAME" \
        -Dflyway.password="$DB_PASSWORD" \
        -Dflyway.locations="$FLYWAY_SCRIPT_LOCATION" \
        -Dflyway.baselineVersion="$BASELINE_VERSION" \
        -Dflyway.baselineDescription=InitialSetup \
        flyway:baseline
  fi

  ./mvnw -s settings.xml \
    -Dmaven.repo.local="$M2_REPO" \
    -Dflyway.url="$DB_HOST" \
    -Dflyway.user="$DB_USERNAME" \
    -Dflyway.password="$DB_PASSWORD" \
    -Dflyway.validateOnMigrate=false \
    -Dflyway.outOfOrder=true \
    -Dflyway.locations="$FLYWAY_SCRIPT_LOCATION" \
    -Dflyway.schemas="$SCHEMA" \
    -Dflyway.createSchemas=true \
    flyway:migrate
done
