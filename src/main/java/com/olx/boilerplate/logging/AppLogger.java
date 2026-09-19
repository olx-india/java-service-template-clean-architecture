package com.olx.boilerplate.logging;

/**
 * Application logging facade. Prefer {@link AppLoggers} over SLF4J at call sites. Domain ports stay business-facing
 * ({@code EventPublisher}, repositories); logging is not one of them.
 */
public interface AppLogger {

    void debug(String message, Object... args);

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void error(String message, Object... args);

    void error(String message, Throwable throwable, Object... args);
}
