package com.olx.boilerplate.logging;

/**
 * Pluggable backend for {@link AppLoggers}. Default implementation lives in infrastructure.
 */
public interface AppLoggerBackend {

    AppLogger getLogger(Class<?> type);

    AppLogger getLogger(String name);
}
