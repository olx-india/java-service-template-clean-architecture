package com.olx.boilerplate.infrastructure.components;

import com.olx.boilerplate.infrastructure.appConfig.AppConfig;
import com.olx.boilerplate.infrastructure.exceptions.InvalidInputException;
import com.olx.boilerplate.infrastructure.appConfig.tenant.TenantContextHolder;
import com.olx.boilerplate.infrastructure.appConfig.tenant.Tenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Order(1)
public class TenantFilter implements Filter {

    public static final String NO_TENANT_PRESENT = "No tenant present in the request.";
    public static final String X_DEFAULT_HOST = "X-Default-Host";
    public static final String X_DEFAULT_TENANT = "X-Default-Tenant";

    private final AppConfig appConfig;

    @Autowired
    public TenantFilter(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;

        final Optional<String> tenant = extractTenantFromRequest(request);
        if (tenant.isEmpty()) {
            throw new InvalidInputException(NO_TENANT_PRESENT);
        }

        tenant.map(Tenant::new).ifPresent(TenantContextHolder::set);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    private Optional<String> extractTenantFromRequest(HttpServletRequest request) {
        List<String> tenants = appConfig.getTenants();

        String header = request.getHeader(X_DEFAULT_TENANT);
        if (tenants.contains(header)) {
            return Optional.of(header);
        }

        String hostHeader = request.getHeader(X_DEFAULT_HOST);
        if (tenants.contains(hostHeader)) {
            return Optional.of(hostHeader);
        }

        Map<String, String> hostTenantMap = appConfig.getHostTenantMap();
        String tenant = hostTenantMap.get(hostHeader);
        if (tenant != null && tenants.contains(tenant)) {
            return Optional.of(tenant);
        }

        return Optional.empty();
    }

}
