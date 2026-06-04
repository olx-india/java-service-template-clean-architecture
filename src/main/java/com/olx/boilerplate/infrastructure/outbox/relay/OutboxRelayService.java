package com.olx.boilerplate.infrastructure.outbox.relay;

import com.olx.boilerplate.infrastructure.components.KafkaProducerService;
import com.olx.boilerplate.infrastructure.data.entities.OutboxEventData;
import com.olx.boilerplate.infrastructure.data.repository.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboxRelayService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxRelayService.class);

    private final OutboxEventJpaRepository outboxEventJpaRepository;
    private final KafkaProducerService kafkaProducerService;
    private final OutboxTopicResolver outboxTopicResolver;

    public OutboxRelayService(OutboxEventJpaRepository outboxEventJpaRepository,
                              KafkaProducerService kafkaProducerService, OutboxTopicResolver outboxTopicResolver) {
        this.outboxEventJpaRepository = outboxEventJpaRepository;
        this.kafkaProducerService = kafkaProducerService;
        this.outboxTopicResolver = outboxTopicResolver;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
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
