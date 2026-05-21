package com.opero.api.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Health Check", description = "Endpoints de monitoreo y verificación del estado de la API")
public class PingController {

    @GetMapping("/ping")
    @Operation(
        summary = "Verificar estado de la API",
        description = "Endpoint de health check para verificar que el servidor backend está activo y respondiendo correctamente. " +
                      "Útil para monitoreo, pruebas de conectividad y verificación del despliegue."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "API funcionando correctamente",
            content = @Content(
                mediaType = "text/plain",
                examples = @ExampleObject(value = "pong! 🏓")
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public String ping() {
        return "pong! 🏓";
    }
}
