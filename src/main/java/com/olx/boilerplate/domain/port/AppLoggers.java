package com.olx.boilerplate.domain.port;

import java.util.Objects;

/**
 * Static entry point for application logging. SLF4J (or another backend) is registered via {@link #setBackend(AppLoggerBackend)}.
 */
public final class AppLoggers {

    private static final String DEFAULT_BACKEND =
                    "com.olx.boilerplate.infrastructure.logging.Slf4jAppLoggerBackend";

    private static volatile AppLoggerBackend backend;

    private AppLoggers() {}

    public static void setBackend(AppLoggerBackend newBackend) {
        backend = Objects.requireNonNull(newBackend, "backend");
    }

    public static AppLogger getLogger(Class<?> type) {
        return backend().getLogger(type);
    }

    public static AppLogger getLogger(String name) {
        return backend().getLogger(name);
    }

    private static AppLoggerBackend backend() {
        AppLoggerBackend current = backend;
        if (current == null) {
            synchronized (AppLoggers.class) {
                current = backend;
                if (current == null) {
                    current = loadDefaultBackend();
                    backend = current;
                }
            }
        }
        return current;
    }

    private static AppLoggerBackend loadDefaultBackend() {
        try {
            Class<?> clazz = Class.forName(DEFAULT_BACKEND);
            return (AppLoggerBackend) clazz.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("No AppLogger backend registered and default load failed: "
                            + DEFAULT_BACKEND, e);
        }
    }
}
