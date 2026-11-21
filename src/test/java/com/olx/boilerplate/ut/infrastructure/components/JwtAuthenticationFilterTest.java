package com.olx.boilerplate.ut.infrastructure.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.olx.boilerplate.infrastructure.components.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    private static final String SECRET = "test-secret-that-is-long-enough-for-hmac";

    @Mock
    private FilterChain chain;

    private final ObjectMapper mapper = new JsonMapper();
    private final JwtAuthenticationFilter filter = newFilter();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final MockHttpServletResponse response = new MockHttpServletResponse();

    private JwtAuthenticationFilter newFilter() {
        JwtAuthenticationFilter f = new JwtAuthenticationFilter(mapper);
        ReflectionTestUtils.setField(f, "jwtSecret", SECRET);
        return f;
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private void bearer(String token) {
        request.addHeader(HttpHeaders.AUTHORIZATION, JwtAuthenticationFilter.BEARER_PREFIX + token);
    }

    private String signed(String subject, long expiresInMs, String secret) throws Exception {
        var builder = JWT.create().withSubject(subject);
        if (expiresInMs != 0)
            builder.withExpiresAt(new Date(System.currentTimeMillis() + expiresInMs));
        return builder.sign(Algorithm.HMAC256(secret));
    }

    @Test
    void authenticatesValidToken() throws Exception {
        bearer(signed("alice", 0, SECRET));
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
        assertEquals("alice", SecurityContextHolder.getContext().getAuthentication().getName());
        assertEquals(200, response.getStatus());
    }

    @Test
    void rejectsMalformedTokenWithProblemDetail() throws Exception {
        bearer("not-a-jwt");
        filter.doFilter(request, response, chain);
        assertRejected();
        String body = response.getContentAsString();
        assertTrue(body.contains("\"status\":401"), body);
        assertTrue(body.contains("Unauthorized"), body);
    }

    @Test
    void rejectsTokenWithWrongSignature() throws Exception {
        bearer(signed("alice", 0, "a-different-secret"));
        filter.doFilter(request, response, chain);
        assertRejected();
    }

    @Test
    void rejectsExpiredTokenAndClearsContext() throws Exception {
        bearer(signed("alice", -60_000, SECRET));
        SecurityContextHolder.getContext().setAuthentication(
                                                             new UsernamePasswordAuthenticationToken("stale", null));
        filter.doFilter(request, response, chain);
        assertRejected();
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    private void assertRejected() throws Exception {
        verify(chain, never()).doFilter(request, response);
        assertEquals(401, response.getStatus());
        assertEquals(MediaType.APPLICATION_PROBLEM_JSON_VALUE, response.getContentType());
    }
}
