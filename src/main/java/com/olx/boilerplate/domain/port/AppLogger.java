package com.olx.boilerplate.domain.port;

/**
 * Application logging port. Call sites must use {@link AppLoggers} — never SLF4J directly.
 */
public interface AppLogger {

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void error(String message, Throwable throwable, Object... args);
}
