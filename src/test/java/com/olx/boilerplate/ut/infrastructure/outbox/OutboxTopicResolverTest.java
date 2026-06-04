package com.olx.boilerplate.ut.infrastructure.outbox;

import com.olx.boilerplate.domain.event.OutboxEventTypes;
import com.olx.boilerplate.infrastructure.appConfig.OutboxRelayProperties;
import com.olx.boilerplate.infrastructure.appConfig.kafka.KafkaCommonConfig;
import com.olx.boilerplate.infrastructure.outbox.relay.OutboxTopicResolver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OutboxTopicResolverTest {

    @Test
    void resolve_shouldPreferConfiguredTopicMapping() {
        OutboxRelayProperties properties = new OutboxRelayProperties();
        properties.setTopics(Map.of(OutboxEventTypes.USER_CREATED, "custom-topic"));

        KafkaCommonConfig kafkaCommonConfig = new KafkaCommonConfig();
        kafkaCommonConfig.setTopics(List.of("fallback-topic"));

        OutboxTopicResolver resolver = new OutboxTopicResolver(properties, kafkaCommonConfig);

        assertEquals("custom-topic", resolver.resolve(OutboxEventTypes.USER_CREATED));
    }

    @Test
    void resolve_shouldFallbackToFirstKafkaTopic() {
        OutboxRelayProperties properties = new OutboxRelayProperties();

        KafkaCommonConfig kafkaCommonConfig = new KafkaCommonConfig();
        kafkaCommonConfig.setTopics(List.of("testTopic"));

        OutboxTopicResolver resolver = new OutboxTopicResolver(properties, kafkaCommonConfig);

        assertEquals("testTopic", resolver.resolve(OutboxEventTypes.USER_CREATED));
    }
}
