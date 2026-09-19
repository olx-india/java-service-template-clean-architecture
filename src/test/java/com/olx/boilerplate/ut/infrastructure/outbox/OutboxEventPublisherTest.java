package com.olx.boilerplate.ut.infrastructure.outbox;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.boilerplate.domain.event.OutboxEventTypes;
import com.olx.boilerplate.domain.event.UserCreatedEvent;
import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import com.olx.boilerplate.infrastructure.data.repository.OutboxEventJpaRepository;
import com.olx.boilerplate.infrastructure.outbox.OutboxEventPublisher;
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

    @Mock
    private OutboxEventJpaRepository outboxEventJpaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private OutboxEventPublisher outboxEventPublisher;

    @BeforeEach
    void setUp() {
        outboxEventPublisher = new OutboxEventPublisher(outboxEventJpaRepository, objectMapper);
    }

    @Test
    void publishUserCreated_shouldPersistUnpublishedOutboxEvent() throws Exception {
        UserCreatedEvent event = new UserCreatedEvent(42L, "Jane", "jane@example.com");

        outboxEventPublisher.publishUserCreated(event);

        ArgumentCaptor<OutboxEventData> captor = ArgumentCaptor.forClass(OutboxEventData.class);
        verify(outboxEventJpaRepository).save(captor.capture());

        OutboxEventData saved = captor.getValue();
        assertEquals(OutboxEventTypes.USER_CREATED, saved.getEventType());
        assertFalse(saved.isPublished());

        JsonNode payload = objectMapper.readTree(saved.getPayload());
        assertEquals(42L, payload.get("userId").asLong());
        assertEquals("Jane", payload.get("name").asText());
        assertEquals("jane@example.com", payload.get("email").asText());
    }
}
