package com.olx.boilerplate.infrastructure.logging;

import com.olx.boilerplate.logging.AppLoggers;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfiguration {

    @PostConstruct
    void registerBackend() {
        AppLoggers.setBackend(new Slf4jAppLoggerBackend());
    }
}
