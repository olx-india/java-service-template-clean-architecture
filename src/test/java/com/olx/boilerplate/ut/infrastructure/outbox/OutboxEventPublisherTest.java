package com.olx.boilerplate.ut.infrastructure.outbox;

import com.google.gson.Gson;
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

    private OutboxEventPublisher outboxEventPublisher;

    @BeforeEach
    void setUp() {
        outboxEventPublisher = new OutboxEventPublisher(outboxEventJpaRepository, new Gson());
    }

    @Test
    void publishUserCreated_shouldPersistUnpublishedOutboxEvent() {
        UserCreatedEvent event = new UserCreatedEvent(42L, "Jane", "jane@example.com");

        outboxEventPublisher.publishUserCreated(event);

        ArgumentCaptor<OutboxEventData> captor = ArgumentCaptor.forClass(OutboxEventData.class);
        verify(outboxEventJpaRepository).save(captor.capture());

        OutboxEventData saved = captor.getValue();
        assertEquals(OutboxEventTypes.USER_CREATED, saved.getEventType());
        assertFalse(saved.isPublished());
        assertEquals("{\"userId\":42,\"name\":\"Jane\",\"email\":\"jane@example.com\"}", saved.getPayload());
    }
}
