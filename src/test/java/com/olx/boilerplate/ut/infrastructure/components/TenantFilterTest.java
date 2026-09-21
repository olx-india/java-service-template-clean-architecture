package com.olx.boilerplate.ut.infrastructure.components;

import tools.jackson.databind.json.JsonMapper;
import com.olx.boilerplate.infrastructure.appConfig.AppConfig;
import com.olx.boilerplate.infrastructure.appConfig.tenant.TenantContextHolder;
import com.olx.boilerplate.infrastructure.components.TenantFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantFilterTest {

    @Mock
    private AppConfig appConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private TenantFilter tenantFilter;

    private final List<String> validTenants = List.of("tenantin");
    private final Map<String, String> hostTenantMap = Map.of("host1", "tenantin");

    @BeforeEach
    void setUp() {
        tenantFilter = new TenantFilter(appConfig, new JsonMapper());
        when(appConfig.getTenants()).thenReturn(validTenants);
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.remove();
    }

    @Test
    void shouldAllowRequestWhenTenantHeaderIsValid() throws ServletException, IOException {
        when(request.getHeader("X-Default-Tenant")).thenReturn("tenantin");

        tenantFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldAllowRequestWhenHostHeaderMapsToValidTenant() throws ServletException, IOException {
        when(request.getHeader("X-Default-Tenant")).thenReturn(null);
        when(request.getHeader("X-Default-Host")).thenReturn("host1");
        when(appConfig.getHostTenantMap()).thenReturn(hostTenantMap);

        tenantFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldRejectRequestWhenNoTenantPresent() throws ServletException, IOException {
        when(request.getHeader("X-Default-Tenant")).thenReturn(null);
        when(request.getHeader("X-Default-Host")).thenReturn(null);
        when(appConfig.getHostTenantMap()).thenReturn(Map.of());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        when(response.getOutputStream()).thenReturn(new jakarta.servlet.ServletOutputStream() {

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {}

            @Override
            public void write(int b) {
                output.write(b);
            }
        });

        tenantFilter.doFilter(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(400);
        assertTrue(output.toString().contains(TenantFilter.NO_TENANT_PRESENT));
    }
}
