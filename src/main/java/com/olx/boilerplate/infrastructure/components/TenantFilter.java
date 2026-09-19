package com.olx.boilerplate.infrastructure.components;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.olx.boilerplate.infrastructure.appConfig.AppConfig;
import com.olx.boilerplate.infrastructure.appConfig.tenant.Tenant;
import com.olx.boilerplate.infrastructure.appConfig.tenant.TenantContextHolder;
import com.olx.boilerplate.infrastructure.exceptions.ErrorResponse;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

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
    private final ObjectMapper objectMapper;

    @Autowired
    public TenantFilter(AppConfig appConfig, ObjectMapper objectMapper) {
        this.appConfig = appConfig;
        this.objectMapper = objectMapper;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
                    throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (isPublicPath(request.getRequestURI())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            final Optional<String> tenant = extractTenantFromRequest(request);
            if (tenant.isEmpty()) {
                writeBadRequest(response, NO_TENANT_PRESENT);
                return;
            }

            tenant.map(Tenant::new).ifPresent(TenantContextHolder::set);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            TenantContextHolder.remove();
        }
    }

    private boolean isPublicPath(String uri) {
        return uri != null && (uri.startsWith("/health") || uri.startsWith("/actuator") || uri.startsWith("/swagger")
                        || uri.startsWith("/v3/api-docs"));
    }

    private void writeBadRequest(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        var error = new ErrorResponse.Error();
        error.setCode(HttpStatus.BAD_REQUEST.getReasonPhrase());
        error.setMessage(message);

        var body = new ErrorResponse();
        body.setError(error);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private Optional<String> extractTenantFromRequest(HttpServletRequest request) {
        List<String> tenants = appConfig.getTenants();

        String header = request.getHeader(X_DEFAULT_TENANT);
        if (header != null && tenants.contains(header)) {
            return Optional.of(header);
        }

        String hostHeader = request.getHeader(X_DEFAULT_HOST);
        if (hostHeader != null && tenants.contains(hostHeader)) {
            return Optional.of(hostHeader);
        }

        Map<String, String> hostTenantMap = appConfig.getHostTenantMap();
        if (hostHeader != null && hostTenantMap != null) {
            String tenant = hostTenantMap.get(hostHeader);
            if (tenant != null && tenants.contains(tenant)) {
                return Optional.of(tenant);
            }
        }

        return Optional.empty();
    }

}
