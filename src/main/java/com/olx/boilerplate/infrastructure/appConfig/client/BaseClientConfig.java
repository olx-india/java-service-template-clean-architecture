package com.olx.boilerplate.infrastructure.appConfig.client;

import lombok.Data;

/**
 * Inherit this class if you want to use service specific fields too, like different endpoints, specific headers, etc.
 */
@Data
public class BaseClientConfig {

    private RetryConfiguration retryConfiguration;
    private CircuitBreakersConfig circuitBreakerConfig;
    private String host;
    private String serviceName;
}
