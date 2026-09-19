package com.olx.boilerplate.controller;

import com.olx.boilerplate.infrastructure.components.KafkaProducerService;
import com.olx.boilerplate.infrastructure.components.RedisClient;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Local-only demo endpoints for Redis/Kafka smoke tests. Not part of the production API surface.
 */
@RestController
@Profile("local")
public class ClientController {

    private final RedisClient redisClient;
    private final KafkaProducerService kafkaProducerService;

    public ClientController(RedisClient redisClient, KafkaProducerService kafkaProducerService) {
        this.redisClient = redisClient;
        this.kafkaProducerService = kafkaProducerService;
    }

    @GetMapping(path = "/test/redis")
    public String test(@RequestParam(value = "key", required = false) String key,
                       @RequestParam(value = "value", required = false) String value) {
        redisClient.putValue(key, value);
        return redisClient.getValue(key);
    }

    @PostMapping(path = "/test/kafka")
    public ResponseEntity<Object> test(@RequestParam(value = "topic") String topic, @RequestBody Object payload) {
        kafkaProducerService.publish(topic, String.valueOf(payload));
        return ResponseEntity.accepted().build();
    }

}
