package com.olx.boilerplate.infrastructure.components;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(0)
public class CorrelationIdFilter extends OncePerRequestFilter {

    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String MDC_TRACE_ID = "tid";
    public static final String MDC_CORRELATION_ID = "cid";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
        String traceId = headerOrGenerate(request, TRACE_ID_HEADER);
        String correlationId = headerOrGenerate(request, CORRELATION_ID_HEADER);
        try {
            MDC.put(MDC_TRACE_ID, traceId);
            MDC.put(MDC_CORRELATION_ID, correlationId);
            response.setHeader(TRACE_ID_HEADER, traceId);
            response.setHeader(CORRELATION_ID_HEADER, correlationId);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_TRACE_ID);
            MDC.remove(MDC_CORRELATION_ID);
        }
    }

    private String headerOrGenerate(HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        return value != null && !value.isBlank() ? value : UUID.randomUUID().toString();
    }
}
