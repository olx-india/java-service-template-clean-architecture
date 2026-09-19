package com.olx.boilerplate.infrastructure.outbox.relay;

import com.olx.boilerplate.domain.port.AppLogger;
import com.olx.boilerplate.domain.port.AppLoggers;
import com.olx.boilerplate.infrastructure.components.KafkaProducerService;
import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import com.olx.boilerplate.infrastructure.data.repository.OutboxEventJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OutboxRelayService {

    private static final AppLogger LOGGER = AppLoggers.getLogger(OutboxRelayService.class);

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final KafkaProducerService kafkaProducerService;
    private final OutboxTopicResolver outboxTopicResolver;

    public OutboxRelayService(OutboxEventJpaRepository outboxEventJpaRepository,
                              KafkaProducerService kafkaProducerService, OutboxTopicResolver outboxTopicResolver) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.outboxTopicResolver = outboxTopicResolver;
    }

    /**
     * Claims unpublished rows with SKIP LOCKED and publishes them in the same transaction so multiple relay instances do not
     * race.
     */
    @Transactional
    public int relayBatch(int batchSize) {
        List<OutboxEventData> pendingEvents = outboxEventJpaRepository.findUnpublishedForUpdate(batchSize);
        int published = 0;
        for (OutboxEventData event : pendingEvents) {
            if (event.isPublished()) {
                continue;
            }
            String topic = outboxTopicResolver.resolve(event.getEventType());
            kafkaProducerService.publish(topic, event.getPayload());
            event.setPublished(true);
            outboxEventJpaRepository.save(event);
            published++;
            LOGGER.info("Relayed outbox event id={} type={} to topic={}", event.getId(), event.getEventType(), topic);
        }
        return published;
    }

    @Transactional
    public void relayEvent(Long eventId) {
        OutboxEventData event = outboxEventJpaRepository.findById(eventId)
                        .orElseThrow(() -> new IllegalStateException("Outbox event not found: " + eventId));

        if (event.isPublished()) {
            return;
        }

        String topic = outboxTopicResolver.resolve(event.getEventType());
        kafkaProducerService.publish(topic, event.getPayload());
        event.setPublished(true);
        outboxEventJpaRepository.save(event);
        LOGGER.info("Relayed outbox event id={} type={} to topic={}", event.getId(), event.getEventType(), topic);
    }
}
