package com.olx.boilerplate.infrastructure.appConfig.client;

import lombok.Data;

@Data
public class RetryConfiguration {

    private Integer maxAttempts;
    private Long waitDuration;
    private Long waitInterval;
    private Double waitIntervalMultiplier;
}
