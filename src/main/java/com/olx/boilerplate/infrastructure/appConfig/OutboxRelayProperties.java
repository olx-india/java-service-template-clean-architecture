package com.olx.boilerplate.infrastructure.appConfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "outbox.relay")
public class OutboxRelayProperties {

    private boolean enabled = true;
    private long fixedDelayMs = 5000;
    private int batchSize = 50;
    private Map<String, String> topics = new HashMap<>();
}
