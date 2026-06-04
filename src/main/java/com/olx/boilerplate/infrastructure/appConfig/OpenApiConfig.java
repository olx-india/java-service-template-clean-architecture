package com.olx.boilerplate.infrastructure.appConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI boilerplateOpenApi() {
        return new OpenAPI().info(new Info()
                        .title("Boilerplate Service API")
                        .description("Clean Architecture Java service template")
                        .version("0.1.0"));
    }
}
