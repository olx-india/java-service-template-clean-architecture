package com.olx.boilerplate.ut.infrastructure.components;

import static org.mockito.Mockito.*;

import com.olx.boilerplate.infrastructure.components.KafkaProducerService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

class KafkaProducerServiceTest {

    @InjectMocks
    private KafkaProducerService kafkaProducerService;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private Retry retryStrategy;

    @Mock
    private CircuitBreaker circuitBreaker;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublish_Success() {
        String topic = "test-topic";
        String payload = "test-payload";

        doReturn(new CompletableFuture<>()).when(kafkaTemplate).send(topic, payload);

        // Simulate circuit breaker execution
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(circuitBreaker).executeRunnable(any(Runnable.class));

        // Simulate retry strategy execution
        doAnswer(invocation -> {
            Runnable runnable = invocation.getArgument(0);
            runnable.run();
            return null;
        }).when(retryStrategy).executeRunnable(any(Runnable.class));

        kafkaProducerService.publish(topic, payload);

        verify(kafkaTemplate, times(1)).send(topic, payload);
    }
}

