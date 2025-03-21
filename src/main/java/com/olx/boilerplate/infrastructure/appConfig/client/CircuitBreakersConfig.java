package com.olx.boilerplate.infrastructure.appConfig.client;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CircuitBreakersConfig {

    private float failureRateThreshold;
    private long waitDurationInOpenState;
    private SlidingWindowType slidingWindowType;
    private int minimumNumberOfCalls;
    private int permittedNumberOfCallsInHalfOpenState;
    private int slidingWindowSize;
    private int slowCallDurationThreshold;
    private int slowCallRateThreshold;

}
