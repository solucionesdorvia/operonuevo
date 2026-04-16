package com.opero.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI operoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                .title("API Opero - Gestión de Incidentes")
                .description("Documentación automática de los endpoints para la aplicación móvil de Opero.")
                .version("1.0"));
    }
}