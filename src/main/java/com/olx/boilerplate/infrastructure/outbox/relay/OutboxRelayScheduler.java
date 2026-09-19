package com.olx.boilerplate.infrastructure.outbox.relay;

import com.olx.boilerplate.domain.port.AppLogger;
import com.olx.boilerplate.domain.port.AppLoggers;
import com.olx.boilerplate.infrastructure.appConfig.OutboxRelayProperties;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "outbox.relay.enabled", havingValue = "true", matchIfMissing = true)
public class OutboxRelayScheduler {

    private static final AppLogger LOGGER = AppLoggers.getLogger(OutboxRelayScheduler.class);

    private final OutboxRelayService outboxRelayService;
    private final OutboxRelayProperties outboxRelayProperties;
    private final Counter relayedCounter;
    private final Counter failedCounter;

    public OutboxRelayScheduler(OutboxRelayService outboxRelayService, OutboxRelayProperties outboxRelayProperties,
                                MeterRegistry meterRegistry) {
        this.outboxRelayService = outboxRelayService;
        this.outboxRelayProperties = outboxRelayProperties;
        this.relayedCounter = meterRegistry.counter("outbox.relay.published");
        this.failedCounter = meterRegistry.counter("outbox.relay.failed");
    }

    @Scheduled(fixedDelayString = "${outbox.relay.fixed-delay-ms:5000}")
    public void relayPendingEvents() {
        try {
            int published = outboxRelayService.relayBatch(outboxRelayProperties.getBatchSize());
            if (published > 0) {
                relayedCounter.increment(published);
            }
        } catch (Exception e) {
            failedCounter.increment();
            LOGGER.error("Outbox relay batch failed", e);
        }
    }
}
