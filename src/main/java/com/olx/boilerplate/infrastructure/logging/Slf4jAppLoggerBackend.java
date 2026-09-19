package com.olx.boilerplate.infrastructure.logging;

import com.olx.boilerplate.logging.AppLogger;
import com.olx.boilerplate.logging.AppLoggerBackend;
import org.slf4j.LoggerFactory;

/**
 * Default {@link AppLoggerBackend} backed by SLF4J.
 */
public class Slf4jAppLoggerBackend implements AppLoggerBackend {

    @Override
    public AppLogger getLogger(Class<?> type) {
        return new Slf4jAppLogger(LoggerFactory.getLogger(type));
    }

    @Override
    public AppLogger getLogger(String name) {
        return new Slf4jAppLogger(LoggerFactory.getLogger(name));
    }
}
