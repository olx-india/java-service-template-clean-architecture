package com.olx.boilerplate.infrastructure.appConfig.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("redis")
public class RedisConfig {

    private String host;
    private int port;
    private RedisMode mode;
    private Long timeToLive;
}
