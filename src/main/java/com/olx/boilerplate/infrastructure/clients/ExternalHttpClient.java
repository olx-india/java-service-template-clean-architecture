package com.olx.boilerplate.infrastructure.clients;

import com.olx.boilerplate.infrastructure.exceptions.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ExternalHttpClient {

    private final OkHttpClient okHttpClient;

    @Value("${clients.testServiceClientConfig.serviceName:TestService}")
    private String serviceName;

    public ExternalHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @CircuitBreaker(name = "testServiceCircuitBreaker", fallbackMethod = "fallback")
    @Retry(name = "service1RetryStrategy")
    @RateLimiter(name = "default")
    public String fetch(String url) {
        Request request = new Request.Builder().url(url).get().build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                throw new ExternalServiceException("External service call failed: " + response.code());
            }
            return response.body().string();
        } catch (IOException e) {
            throw new ExternalServiceException("External service call failed: " + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private String fallback(String url, Throwable throwable) {
        return "{\"fallback\":true,\"service\":\"" + serviceName + "\"}";
    }
}
