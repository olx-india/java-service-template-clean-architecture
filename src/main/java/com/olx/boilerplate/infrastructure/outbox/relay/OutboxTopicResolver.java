package com.olx.boilerplate.infrastructure.outbox.relay;

import com.olx.boilerplate.domain.event.OutboxEventTypes;
import com.olx.boilerplate.infrastructure.appConfig.OutboxRelayProperties;
import com.olx.boilerplate.infrastructure.appConfig.kafka.KafkaCommonConfig;
import org.springframework.stereotype.Component;

@Component
public class OutboxTopicResolver {

    private static final String DEFAULT_USER_CREATED_TOPIC = "user-created";

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

        if (OutboxEventTypes.USER_CREATED.equals(eventType)) {
            return defaultKafkaTopic(DEFAULT_USER_CREATED_TOPIC);
        }

        return defaultKafkaTopic(eventType);
    }

    private String defaultKafkaTopic(String fallback) {
        if (kafkaCommonConfig.getTopics() != null && !kafkaCommonConfig.getTopics().isEmpty()) {
            return kafkaCommonConfig.getTopics().get(0);
        }
        return fallback;
    }
}
