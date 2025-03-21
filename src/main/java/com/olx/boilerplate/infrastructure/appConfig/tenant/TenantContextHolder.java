package com.olx.boilerplate.infrastructure.appConfig.tenant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TenantContextHolder {

    private static final ThreadLocal<Tenant> threadLocal = new ThreadLocal<>();

    public static Tenant get() {
        return threadLocal.get();
    }

    public static String getSitecodeKey() {
        return threadLocal.get() == null ? null : threadLocal.get().key();
    }

    public static void set(Tenant value) {
        threadLocal.set(value);
    }

    public static void remove() {
        threadLocal.remove();
    }
}
