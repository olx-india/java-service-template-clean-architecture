package com.olx.boilerplate.ut.infrastructure.outbox;

import com.olx.boilerplate.domain.event.OutboxEventTypes;
import com.olx.boilerplate.infrastructure.components.KafkaProducerService;
import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import com.olx.boilerplate.infrastructure.data.repository.OutboxEventJpaRepository;
import com.olx.boilerplate.infrastructure.outbox.relay.OutboxRelayService;
import com.olx.boilerplate.infrastructure.outbox.relay.OutboxTopicResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OutboxRelayServiceTest {

    @Mock
    private OutboxEventJpaRepository outboxEventJpaRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @Mock
    private OutboxTopicResolver outboxTopicResolver;

    @InjectMocks
    private OutboxRelayService outboxRelayService;

    @Test
    void relayEvent_shouldPublishToKafkaAndMarkPublished() {
        OutboxEventData event = OutboxEventData.builder()
                .id(7L)
                .eventType(OutboxEventTypes.USER_CREATED)
                .payload("{\"userId\":1}")
                .published(false)
                .build();

        when(outboxEventJpaRepository.findById(7L)).thenReturn(Optional.of(event));
        when(outboxTopicResolver.resolve(OutboxEventTypes.USER_CREATED)).thenReturn("user-created");

        outboxRelayService.relayEvent(7L);

        verify(kafkaProducerService).publish(eq("user-created"), eq("{\"userId\":1}"));
        verify(outboxEventJpaRepository).save(event);
        assertTrue(event.isPublished());
    }

    @Test
    void relayEvent_shouldSkipAlreadyPublishedEvents() {
        OutboxEventData event = OutboxEventData.builder()
                .id(8L)
                .eventType(OutboxEventTypes.USER_CREATED)
                .payload("{\"userId\":2}")
                .published(true)
                .build();

        when(outboxEventJpaRepository.findById(8L)).thenReturn(Optional.of(event));

        outboxRelayService.relayEvent(8L);

        verify(kafkaProducerService, never()).publish(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.anyString());
    }
}
