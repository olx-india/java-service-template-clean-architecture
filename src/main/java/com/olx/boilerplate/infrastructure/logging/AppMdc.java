package com.olx.boilerplate.infrastructure.logging;

import org.slf4j.MDC;

/**
 * Thin MDC facade so filters avoid importing SLF4J MDC directly outside this package.
 */
public final class AppMdc {

    private AppMdc() {}

    public static void put(String key, String value) {
        MDC.put(key, value);
    }

    public static void remove(String key) {
        MDC.remove(key);
    }

    public static void clear() {
        MDC.clear();
    }
}
