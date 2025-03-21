package com.olx.boilerplate.ut.controller;

import com.olx.boilerplate.controller.ClientController;
import com.olx.boilerplate.infrastructure.components.KafkaProducerService;
import com.olx.boilerplate.infrastructure.components.RedisClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientControllerTest {

    @Mock
    private RedisClient redisClient;

    @Mock
    private KafkaProducerService kafkaProducerService;

    private ClientController clientController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientController = new ClientController(redisClient, kafkaProducerService);
    }

    @Test
    void testRedis_StoresAndRetrievesValueSuccessfully() {
        String key = "testKey";
        String value = "testValue";

        doNothing().when(redisClient).putValue(key, value);
        when(redisClient.getValue(key)).thenReturn(value);

        String response = clientController.test(key, value);

        assertEquals(value, response);
        verify(redisClient).putValue(key, value);
        verify(redisClient).getValue(key);
    }

    @Test
    void testKafka_PublishesMessageSuccessfully() {
        String topic = "testTopic";
        Object payload = "{\"message\": \"test\"}";

        doNothing().when(kafkaProducerService).publish(topic, String.valueOf(payload));

        ResponseEntity<Object> response = clientController.test(topic, payload);

        assertEquals(202, response.getStatusCodeValue()); // HTTP 202 Accepted
        verify(kafkaProducerService).publish(topic, String.valueOf(payload));
    }
}

