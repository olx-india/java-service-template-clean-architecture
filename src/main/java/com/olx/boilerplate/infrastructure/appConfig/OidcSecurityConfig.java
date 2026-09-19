package com.olx.boilerplate.infrastructure.appConfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * OIDC / OAuth2 resource-server security. Enable with: {@code spring.security.enabled=true} and {@code security.mode=oidc} plus
 * {@code security.oauth2.jwk-set-uri}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "true")
@ConditionalOnProperty(name = "security.mode", havingValue = "oidc")
public class OidcSecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
            "/health",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/actuator/**"
    };

    @Bean
    public SecurityFilterChain oidcSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .authorizeHttpRequests(auth -> auth
                                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                        .anyRequest().authenticated())
                        .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
