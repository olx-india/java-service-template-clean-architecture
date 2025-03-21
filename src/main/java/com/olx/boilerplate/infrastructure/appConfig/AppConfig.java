package com.olx.boilerplate.infrastructure.appConfig;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    private List<String> tenants;
    private Map<String, String> hostTenantMap;
    private boolean runningITMode;
}
