package com.olx.boilerplate.infrastructure.components;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Retry retryStrategy;
    private final CircuitBreaker circuitBreaker;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
            @Qualifier("defaultRetryStrategy") Retry retryStrategy,
            @Qualifier("defaultCircuitBreaker") CircuitBreaker circuitBreaker) {
        this.kafkaTemplate = kafkaTemplate;
        this.retryStrategy = retryStrategy;
        this.circuitBreaker = circuitBreaker;
    }

    @SneakyThrows
    public void publish(String topic, String payload) {
        this.circuitBreaker
                .executeRunnable(() -> this.retryStrategy.executeRunnable(() -> this.executePublish(topic, payload)));
    }

    @SneakyThrows
    private void executePublish(String topic, String payload) {
        kafkaTemplate.send(topic, payload);
        System.out.println("Event Published Succesfully");
    }
}
