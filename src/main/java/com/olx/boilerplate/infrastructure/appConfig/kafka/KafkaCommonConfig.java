package com.olx.boilerplate.infrastructure.appConfig.kafka;

import com.olx.boilerplate.infrastructure.appConfig.client.BaseClientConfig;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@EqualsAndHashCode(callSuper = true)
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaCommonConfig extends BaseClientConfig {

    private String bootstrapServers;
    private int retries;
    private String acks;
    private int batchSize;
    private int lingerMs;
    private int bufferMemory;
    private String keySerializer;
    private String valueSerializer;
    private String groupId;
    private String autoOffsetReset;
    private boolean enableAutoCommit;
    private String keyDeserializer;
    private String valueDeserializer;

}
