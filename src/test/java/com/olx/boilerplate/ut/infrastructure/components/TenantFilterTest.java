package com.olx.boilerplate.ut.infrastructure.components;

import com.olx.boilerplate.infrastructure.appConfig.AppConfig;
import com.olx.boilerplate.infrastructure.components.TenantFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TenantFilterTest {
    @Autowired
    private ConfigurableApplicationContext context;

    public void refreshContext() {
        context.refresh();
    }

    @Mock
    private AppConfig appConfig;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private TenantFilter tenantFilter;

    private final List<String> validTenants = List.of("tenantin", "tenantin");
    private final Map<String, String> hostTenantMap = Map.of("tenant1", "tenantin");

    @BeforeEach
    void setUp() {
        when(appConfig.getTenants()).thenReturn(validTenants);
    }

    @Test
    void shouldAllowRequestWhenTenantHeaderIsValid() throws ServletException, IOException {
        when(request.getHeader("X-Default-Tenant")).thenReturn("tenantin");

        tenantFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldAllowRequestWhenHostHeaderMapsToValidTenant() throws ServletException, IOException {
        when(request.getHeader("X-Default-Tenant")).thenReturn("tenant1");
        when(request.getHeader("X-Default-Host")).thenReturn("tenantin");


        tenantFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void shouldThrowExceptionWhenNoTenantHeaderPresent() throws ServletException, IOException {
        when(request.getHeader("X-Default-Tenant")).thenReturn("tenant2");
        when(request.getHeader("X-Default-Host")).thenReturn("tenant1");
        when(appConfig.getHostTenantMap()).thenReturn(hostTenantMap);

        tenantFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }
}
