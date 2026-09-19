package com.olx.boilerplate.infrastructure.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.boilerplate.domain.event.OrderCreatedEvent;
import com.olx.boilerplate.domain.event.OutboxEventTypes;
import com.olx.boilerplate.domain.event.UserCreatedEvent;
import com.olx.boilerplate.logging.AppLogger;
import com.olx.boilerplate.logging.AppLoggers;
import com.olx.boilerplate.domain.port.EventPublisher;
import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import com.olx.boilerplate.infrastructure.data.repository.OutboxEventJpaRepository;
import org.springframework.stereotype.Component;

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
    public void publishUserCreated(UserCreatedEvent event) {
        enqueue(OutboxEventTypes.USER_CREATED, event);
        LOGGER.debug("Enqueued UserCreatedEvent for userId={} in outbox", event.getUserId());
    }

    @Override
    public void publishOrderCreated(OrderCreatedEvent event) {
        enqueue(OutboxEventTypes.ORDER_CREATED, event);
        LOGGER.debug("Enqueued OrderCreatedEvent for orderId={} in outbox", event.orderId());
    }

    private void enqueue(String eventType, Object event) {
        try {
            OutboxEventData outboxEvent = OutboxEventData.builder()
                            .eventType(eventType)
                            .payload(objectMapper.writeValueAsString(event))
                            .published(false)
                            .build();
            outboxEventJpaRepository.save(outboxEvent);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize outbox event: " + eventType, e);
        }
    }
}
