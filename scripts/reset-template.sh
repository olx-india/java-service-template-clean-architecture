#!/usr/bin/env bash
# Remove the sample User and Order features, the Redis/Kafka demo controller
# and the pagination helpers from the template, and keep the clean-architecture
# package layout (emptied layer directories get a .gitkeep).
#
# Usually run through `make init`, before scripts/rename-template.sh.
#
# Usage:
#   ./scripts/reset-template.sh
#   DRY_RUN=1 ./scripts/reset-template.sh   # show what would change
#   FORCE=1   ./scripts/reset-template.sh   # allow a dirty git tree
set -euo pipefail

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT"
# shellcheck source=lib/common.sh
source "$ROOT/scripts/lib/common.sh"

# Detect the base package from the @SpringBootApplication class so the reset
# also works on a repo that was already renamed.
APP_FILE="$(grep -rl --include='*.java' '@SpringBootApplication' src/main/java | head -1 || true)"
[[ -n "$APP_FILE" ]] || die "Could not find the @SpringBootApplication class under src/main/java"
PKG="$(sed -n 's/^package \(.*\);/\1/p' "$APP_FILE" | head -1)"
PKG_PATH="${PKG//.//}"
MAIN="src/main/java/${PKG_PATH}"
TEST="src/test/java/${PKG_PATH}"
RES="src/main/resources"
TRES="src/test/resources"

if [[ ! -f "${MAIN}/domain/User.java" && ! -f "${MAIN}/domain/Order.java" ]]; then
  echo "Sample User/Order code not found under ${MAIN} — nothing to reset."
  exit 0
fi

require_clean_git_tree

echo "==> Resetting template samples (base package: ${PKG})"
is_dry_run && echo "    DRY_RUN=1 — no files will be changed"

echo "==> Removing sample User and Order features"
for path in \
  "${MAIN}/domain/User.java" \
  "${MAIN}/domain/Order.java" \
  "${MAIN}/domain/repository/UserRepository.java" \
  "${MAIN}/domain/repository/OrderRepository.java" \
  "${MAIN}/domain/event/UserCreatedEvent.java" \
  "${MAIN}/domain/event/OrderCreatedEvent.java" \
  "${MAIN}/usecase/users" \
  "${MAIN}/usecase/order" \
  "${MAIN}/controller/UserController.java" \
  "${MAIN}/controller/OrderController.java" \
  "${MAIN}/controller/dto/user" \
  "${MAIN}/controller/dto/order" \
  "${MAIN}/infrastructure/data/entities/UserData.java" \
  "${MAIN}/infrastructure/data/entities/OrderData.java" \
  "${MAIN}/infrastructure/data/repository/JPAUserRepository.java" \
  "${MAIN}/infrastructure/data/repository/JpaOrderRepository.java" \
  "${MAIN}/infrastructure/data/repository/UserRepositoryImpl.java" \
  "${MAIN}/infrastructure/data/repository/OrderRepositoryImpl.java" \
  "${TEST}/ut/usecase/CreateUserTest.java" \
  "${TEST}/ut/usecase/GetUserTest.java" \
  "${TEST}/ut/usecase/DeleteUserTest.java" \
  "${TEST}/ut/usecase/CreateOrderTest.java" \
  "${TEST}/ut/usecase/GetOrderTest.java" \
  "${TEST}/ut/usecase/DeleteOrderTest.java" \
  "${TEST}/ut/controller/UserControllerTest.java" \
  "${TEST}/ut/controller/OrderControllerTest.java" \
  "${TEST}/it/stepdefinition/UserAPIStepDefinitions.java" \
  "${TEST}/it/stepdefinition/OrderAPIStepDefinitions.java" \
  "${TRES}/features_and_scenarios/User.feature" \
  "${TRES}/features_and_scenarios/Order.feature" \
  "${RES}/db/migration/olxin/V1.1__Insert_Initial_Data.sql"; do
  remove_path "$path"
done

echo "==> Removing Redis/Kafka demo ClientController"
for path in \
  "${MAIN}/controller/ClientController.java" \
  "${TEST}/ut/controller/ClientControllerTest.java" \
  "${TEST}/it/stepdefinition/ClientAPIStepDefinitions.java" \
  "${TRES}/features_and_scenarios/Client.feature"; do
  remove_path "$path"
done

ARCH_TEST="${TEST}/ut/ArchitectureTest.java"
if [[ -f "$ARCH_TEST" ]]; then
  echo "  ~ edit    $ARCH_TEST"
  if ! is_dry_run; then
    # Drop the ClientController exemption from the controller -> infrastructure rule.
    sed_inplace "$ARCH_TEST" \
      -e '/doNotHaveSimpleName("ClientController")/d' \
      -e 's/controllersShouldNotDependOnInfrastructureExceptLocalDemo/controllersShouldNotDependOnInfrastructure/'
  fi
fi

echo "==> Removing sample pagination types"
for path in \
  "${MAIN}/domain/PageQuery.java" \
  "${MAIN}/domain/PageResult.java" \
  "${MAIN}/controller/dto/PageResponse.java"; do
  remove_path "$path"
done

echo "==> Rewriting event publishing to a generic port"
write_file "${MAIN}/domain/port/EventPublisher.java" <<EOF
package ${PKG}.domain.port;

/**
 * Port for publishing domain events. The infrastructure adapter stores events in the transactional outbox; the relay
 * forwards them to Kafka.
 */
public interface EventPublisher {

    /**
     * @param eventType logical event name, e.g. a constant from {@code OutboxEventTypes}
     * @param event payload serialised to JSON (use an immutable record)
     */
    void publish(String eventType, Object event);
}
EOF

write_file "${MAIN}/domain/event/OutboxEventTypes.java" <<EOF
package ${PKG}.domain.event;

/**
 * Logical event type names written to the outbox. Map each to a Kafka topic under {@code outbox.relay.topics} in
 * application.yaml.
 *
 * <p>
 * Example: {@code public static final String PRODUCT_CREATED = "ProductCreatedEvent";}
 */
public final class OutboxEventTypes {

    private OutboxEventTypes() {}
}
EOF

write_file "${MAIN}/infrastructure/outbox/OutboxEventPublisher.java" <<EOF
package ${PKG}.infrastructure.outbox;

import ${PKG}.domain.port.EventPublisher;
import ${PKG}.infrastructure.data.entities.OutboxEventData;
import ${PKG}.infrastructure.data.repository.OutboxEventJpaRepository;
import ${PKG}.logging.AppLogger;
import ${PKG}.logging.AppLoggers;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class OutboxEventPublisher implements EventPublisher {

    private static final AppLogger LOGGER = AppLoggers.getLogger(OutboxEventPublisher.class);

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final ObjectMapper objectMapper;

    public OutboxEventPublisher(OutboxEventJpaRepository outboxEventJpaRepository, ObjectMapper objectMapper) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(String eventType, Object event) {
        try {
            OutboxEventData outboxEvent = OutboxEventData.builder()
                            .eventType(eventType)
                            .payload(objectMapper.writeValueAsString(event))
                            .published(false)
                            .build();
            outboxEventJpaRepository.save(outboxEvent);
            LOGGER.debug("Enqueued {} in outbox", eventType);
        } catch (JacksonException e) {
            throw new IllegalStateException("Failed to serialize outbox event: " + eventType, e);
        }
    }
}
EOF

write_file "${MAIN}/infrastructure/outbox/relay/OutboxTopicResolver.java" <<EOF
package ${PKG}.infrastructure.outbox.relay;

import ${PKG}.infrastructure.appConfig.OutboxRelayProperties;
import ${PKG}.infrastructure.appConfig.kafka.KafkaCommonConfig;
import org.springframework.stereotype.Component;

/**
 * Resolves the Kafka topic for an outbox event type: {@code outbox.relay.topics} first, then the first configured
 * Kafka topic, then the event type itself.
 */
@Component
public class OutboxTopicResolver {

    private final OutboxRelayProperties outboxRelayProperties;
    private final KafkaCommonConfig kafkaCommonConfig;

    public OutboxTopicResolver(OutboxRelayProperties outboxRelayProperties, KafkaCommonConfig kafkaCommonConfig) {
        this.outboxRelayProperties = outboxRelayProperties;
        this.kafkaCommonConfig = kafkaCommonConfig;
    }

    public String resolve(String eventType) {
        String configuredTopic = outboxRelayProperties.getTopics().get(eventType);
        if (configuredTopic != null && !configuredTopic.isBlank()) {
            return configuredTopic;
        }
        if (kafkaCommonConfig.getTopics() != null && !kafkaCommonConfig.getTopics().isEmpty()) {
            return kafkaCommonConfig.getTopics().get(0);
        }
        return eventType;
    }
}
EOF

write_file "${TEST}/ut/infrastructure/outbox/OutboxEventPublisherTest.java" <<EOF
package ${PKG}.ut.infrastructure.outbox;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import ${PKG}.infrastructure.data.entities.OutboxEventData;
import ${PKG}.infrastructure.data.repository.OutboxEventJpaRepository;
import ${PKG}.infrastructure.outbox.OutboxEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxEventPublisherTest {

    private static final String EVENT_TYPE = "ExampleCreatedEvent";

    record ExampleCreatedEvent(Long exampleId, String name) {
    }

    @Mock
    private OutboxEventJpaRepository outboxEventJpaRepository;

    private final ObjectMapper objectMapper = new JsonMapper();
    private OutboxEventPublisher outboxEventPublisher;

    @BeforeEach
    void setUp() {
        outboxEventPublisher = new OutboxEventPublisher(outboxEventJpaRepository, objectMapper);
    }

    @Test
    void publish_shouldPersistUnpublishedOutboxEvent() throws Exception {
        outboxEventPublisher.publish(EVENT_TYPE, new ExampleCreatedEvent(42L, "Jane"));

        ArgumentCaptor<OutboxEventData> captor = ArgumentCaptor.forClass(OutboxEventData.class);
        verify(outboxEventJpaRepository).save(captor.capture());

        OutboxEventData saved = captor.getValue();
        assertEquals(EVENT_TYPE, saved.getEventType());
        assertFalse(saved.isPublished());

        JsonNode payload = objectMapper.readTree(saved.getPayload());
        assertEquals(42L, payload.get("exampleId").asLong());
        assertEquals("Jane", payload.get("name").asText());
    }
}
EOF

echo "==> Updating outbox tests that referenced sample event types"
for f in "${TEST}/ut/infrastructure/outbox/OutboxTopicResolverTest.java" \
         "${TEST}/ut/infrastructure/outbox/OutboxRelayServiceTest.java"; do
  [[ -f "$f" ]] || continue
  echo "  ~ edit    $f"
  is_dry_run && continue
  sed_inplace "$f" \
    -e '/import .*domain\.event\.OutboxEventTypes;/d' \
    -e 's/OutboxEventTypes\.USER_CREATED/"ExampleCreatedEvent"/g' \
    -e 's/user-created/example-created/g' \
    -e 's/userId/exampleId/g'
done

echo "==> Clearing sample outbox topic mappings"
for f in "${RES}/application.yaml" "${RES}/application-local.yaml"; do
  [[ -f "$f" ]] || continue
  echo "  ~ edit    $f"
  is_dry_run && continue
  # Replace the `topics:` block under outbox.relay with an empty map + example.
  perl -0pi -e 's/(outbox:\n  relay:\n(?:    (?!topics:).*\n)*)    topics:\n(?:      .*(?:\n|\z))*/$1    # Map outbox event types to Kafka topics, e.g. ProductCreatedEvent: product-created\n    topics: {}\n/' "$f"
done

echo "==> Resetting database schema and integration-test seed data"
write_file "${RES}/db/migration/common/V1.0__Initial_Schema.sql" <<'EOF'
-- Initial schema for this service.
-- Add your first tables here, or create new versioned migrations (V1.4__..., V1.5__...).
-- The outbox_event table is created by V1.2__Outbox_Event.sql.
SELECT 1;
EOF
if ! is_dry_run; then
  mkdir -p "${RES}/db/migration/olxin"
  touch "${RES}/db/migration/olxin/.gitkeep"
fi
echo "  ~ keep    ${RES}/db/migration/olxin/.gitkeep"

for f in "${TRES}/db/init-it.sql" "${TRES}/mocks/mysql/warmup-scripts/warmup1.sql"; do
  [[ -f "$f" ]] || continue
  echo "  ~ edit    $f"
  is_dry_run && continue
  # Drop CREATE TABLE / INSERT statements for the sample tables.
  perl -0pi -e 's/CREATE TABLE (?:IF NOT EXISTS )?tbl_(?:user|order)\b.*?;\n+//gs; s/INSERT INTO tbl_(?:user|order)\b.*?;\n+//gs' "$f"
done

echo "==> Keeping empty layer directories (.gitkeep)"
keep_dir() {
  local dir="$1"
  if [[ -d "$dir" ]] && [[ -n "$(find "$dir" -type f ! -name .gitkeep -print -quit)" ]]; then
    return 0
  fi
  echo "  ~ keep    ${dir}/.gitkeep"
  is_dry_run && return 0
  mkdir -p "$dir"
  touch "${dir}/.gitkeep"
}
for dir in \
  "${MAIN}/usecase" \
  "${MAIN}/domain/repository" \
  "${MAIN}/controller/dto" \
  "${MAIN}/infrastructure/data/entities" \
  "${MAIN}/infrastructure/data/repository" \
  "${TEST}/ut/usecase" \
  "${TEST}/ut/controller"; do
  keep_dir "$dir"
done

echo
if is_dry_run; then
  echo "Dry run complete. Re-run without DRY_RUN=1 to apply."
else
  echo "Samples removed. Next: add your first feature (domain -> usecase -> controller -> adapter -> tests)."
fi
