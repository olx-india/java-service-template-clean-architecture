package com.olx.boilerplate.infrastructure.appConfig.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("http-config")
public class HttpConfig {

    private Integer maxIdleConnectionsPerRoute;
    private Integer maxConnectionKeepAliveDurationInMins;
    private Integer connectTimeout;
    private Integer readTimeout;
}
