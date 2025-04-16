package com.reservemate.reserve_mate_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityScheme.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    servers = {
        @Server(url = "https://api.sportmate.site", description = "개발 서버"),
        @Server(url = "http://localhost:8080", description = "로컬 서버")
    })
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI reserveMateApi() {
        Info info = new Info()
            .version("1.0.0")
            .title("Reserve-mate API")
            .description("Reserve-mate API 명세서");

        SecurityScheme accessToken = new SecurityScheme()
            .type(Type.APIKEY)
            .bearerFormat("JWT")
            .in(SecurityScheme.In.HEADER)
            .name("access");

        SecurityRequirement securityRequirement = new SecurityRequirement().addList("access");

        return new OpenAPI()
            .info(info)
            .addSecurityItem(securityRequirement)
            .components(new Components().addSecuritySchemes("access", accessToken));
    }
}
