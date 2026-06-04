package com.olx.boilerplate.infrastructure.actuator;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Endpoint(id = "cache-evict")
public class CacheEvictActuatorEndpoint {

    private final CacheManager cacheManager;

    public CacheEvictActuatorEndpoint(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @WriteOperation
    public String evictAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
        });
        return "Evicted caches: " + String.join(", ", cacheManager.getCacheNames());
    }
}
