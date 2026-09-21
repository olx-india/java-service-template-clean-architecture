package com.olx.boilerplate.infrastructure.appConfig;

import com.olx.boilerplate.infrastructure.components.TenantFilter;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI boilerplateOpenApi() {
        final String bearerScheme = "bearerAuth";
        OpenAPI openAPI = new OpenAPI()
                        .info(new Info()
                                        .title("Boilerplate Service API")
                                        .description("Clean Architecture Java service template. APIs are versioned under /api/v1. "
                                                        + "Send header " + TenantFilter.X_DEFAULT_TENANT + " on API requests.")
                                        .version("0.1.0"))
                        .components(new Components()
                                        .addSecuritySchemes(bearerScheme, new SecurityScheme()
                                                        .type(SecurityScheme.Type.HTTP)
                                                        .scheme("bearer")
                                                        .bearerFormat("JWT"))
                                        .addParameters("X-Default-Tenant", new Parameter()
                                                        .in("header")
                                                        .name(TenantFilter.X_DEFAULT_TENANT)
                                                        .required(true)
                                                        .description("Tenant identifier (e.g. default)")
                                                        .schema(new StringSchema())))
                        .addSecurityItem(new SecurityRequirement().addList(bearerScheme));
        return openAPI;
    }
}
