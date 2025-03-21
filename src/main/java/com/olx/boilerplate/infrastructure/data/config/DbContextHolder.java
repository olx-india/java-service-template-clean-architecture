package com.olx.boilerplate.infrastructure.data.config;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DbContextHolder {

    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static String get() {
        return threadLocal.get();
    }

    public static void set(String value) {
        threadLocal.set(value);
    }

    public static void remove() {
        threadLocal.remove();
    }

}
