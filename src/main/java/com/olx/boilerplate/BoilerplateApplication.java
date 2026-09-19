package com.olx.boilerplate;

import com.olx.boilerplate.domain.port.AppLogger;
import com.olx.boilerplate.domain.port.AppLoggers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableRetry
@EnableScheduling
public class BoilerplateApplication {

    private static final AppLogger LOGGER = AppLoggers.getLogger(BoilerplateApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(BoilerplateApplication.class, args);
        LOGGER.info("Application started");
    }
}
