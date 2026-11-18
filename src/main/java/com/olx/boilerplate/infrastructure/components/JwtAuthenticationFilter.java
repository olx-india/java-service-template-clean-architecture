package com.olx.boilerplate.infrastructure.components;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Component
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true", matchIfMissing = false)
@ConditionalOnProperty(name = "security.mode", havingValue = "hmac", matchIfMissing = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private static final String DEFAULT_USER_ROLE = "ROLE_USER";
    private static final String INVALID_TOKEN_DETAIL = "Invalid or expired bearer token.";
    private static final String INVALID_TOKEN_TYPE = "https://api.example.com/problems/invalid-token";

    private final ObjectMapper objectMapper;

    @Value("${security.jwt.secret:change-me-in-production}")
    private String jwtSecret;

    public JwtAuthenticationFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                    throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith(BEARER_PREFIX)) {
            String token = authHeader.substring(BEARER_PREFIX_LENGTH);
            try {
                DecodedJWT decodedJWT = JWT.require(Algorithm.HMAC256(jwtSecret)).build().verify(token);
                String subject = decodedJWT.getSubject();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(subject, null,
                                                                                                             List.of(new SimpleGrantedAuthority(DEFAULT_USER_ROLE)));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JWTVerificationException e) {
                SecurityContextHolder.clearContext();
                writeUnauthorized(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, INVALID_TOKEN_DETAIL);
        problem.setTitle(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        problem.setType(URI.create(INVALID_TOKEN_TYPE));
        problem.setProperty("code", HttpStatus.UNAUTHORIZED.getReasonPhrase());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }
}
