package com.olx.boilerplate.infrastructure.outbox;

import com.google.gson.Gson;
import com.olx.boilerplate.domain.event.OutboxEventTypes;
import com.olx.boilerplate.domain.event.UserCreatedEvent;
import com.olx.boilerplate.domain.port.EventPublisher;
import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import com.olx.boilerplate.infrastructure.data.repository.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventPublisher implements EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventPublisher.class);

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final Gson gson;

    public OutboxEventPublisher(OutboxEventJpaRepository outboxEventJpaRepository, Gson gson) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
        this.gson = gson;
    }

    @Override
    public void publishUserCreated(UserCreatedEvent event) {
        OutboxEventData outboxEvent = OutboxEventData.builder()
                .eventType(OutboxEventTypes.USER_CREATED)
                .payload(gson.toJson(event))
                .published(false)
                .build();

        outboxEventJpaRepository.save(outboxEvent);
        LOGGER.debug("Enqueued UserCreatedEvent for userId={} in outbox", event.getUserId());
    }
}
