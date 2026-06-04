package com.olx.boilerplate.infrastructure.appConfig;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnProperty(name = "spring.security.enabled", havingValue = "false", matchIfMissing = true)
public class NoSecurityConfig {

    @Bean
    public SecurityFilterChain permitAllSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
