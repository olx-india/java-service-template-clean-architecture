package com.olx.boilerplate.infrastructure.appConfig.kafka;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

public class KafkaProducerConfig {

    private final KafkaCommonConfig kafkaCommonConfig;

    public KafkaProducerConfig(KafkaCommonConfig kafkaClientConfig) {
        this.kafkaCommonConfig = kafkaClientConfig;
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaCommonConfig.getBootstrapServers());
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, kafkaCommonConfig.getKeySerializer());
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, kafkaCommonConfig.getValueSerializer());
        configProps.put(ProducerConfig.RETRIES_CONFIG, kafkaCommonConfig.getRetries());
        configProps.put(ProducerConfig.ACKS_CONFIG, kafkaCommonConfig.getAcks());
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, kafkaCommonConfig.getBatchSize());
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, kafkaCommonConfig.getLingerMs());
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaCommonConfig.getBufferMemory());
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
