package com.olx.boilerplate.infrastructure.outbox.relay;

import com.olx.boilerplate.infrastructure.appConfig.OutboxRelayProperties;
import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import com.olx.boilerplate.infrastructure.data.repository.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "outbox.relay.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelayScheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxRelayScheduler.class);

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final OutboxRelayService outboxRelayService;
    private final OutboxRelayProperties outboxRelayProperties;

    public OutboxRelayScheduler(OutboxEventJpaRepository outboxEventJpaRepository, OutboxRelayService outboxRelayService,
                                OutboxRelayProperties outboxRelayProperties) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
        this.outboxRelayService = outboxRelayService;
        this.outboxRelayProperties = outboxRelayProperties;
    }

    @Scheduled(fixedDelayString = "${outbox.relay.fixed-delay-ms:5000}")
    public void relayPendingEvents() {
        List<OutboxEventData> pendingEvents = outboxEventJpaRepository.findByPublishedFalseOrderByCreatedAtAsc(
                                                                                                               PageRequest
                                                                                                                               .of(0,
                                                                                                                                   outboxRelayProperties
                                                                                                                                                   .getBatchSize()));

        for (OutboxEventData event : pendingEvents) {
            try {
                outboxRelayService.relayEvent(event.getId());
            } catch (Exception e) {
                LOGGER.error("Failed to relay outbox event id={} type={}", event.getId(), event.getEventType(), e);
            }
        }
    }
}
