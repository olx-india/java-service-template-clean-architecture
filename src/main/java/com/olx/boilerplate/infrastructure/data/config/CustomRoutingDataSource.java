package com.olx.boilerplate.infrastructure.data.config;

import com.olx.boilerplate.infrastructure.appConfig.tenant.TenantContextHolder;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class CustomRoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return fetchTenantIdFrom(DbContextHolder.get());
    }

    private static @NotNull String fetchTenantIdFrom(String tenantId) {
        return TenantContextHolder.getSitecodeKey() + tenantId;
    }

}
