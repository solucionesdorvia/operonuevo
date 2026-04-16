package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para la respuesta de autenticación.
 *
 * ¿Qué hace este DTO?
 * - Responde con los datos de autenticación exitosa (login o register)
 * - Incluye un token de autenticación (por ahora simulado, JWT en el futuro)
 * - Incluye la información del usuario autenticado
 * - Incluye un mensaje de confirmación
 *
 * Usado por:
 * - POST /api/auth/login (respuesta exitosa)
 * - POST /api/auth/register (respuesta exitosa)
 *
 * Nota: El token es simulado por ahora. En una implementación real
 * se debería generar un JWT (JSON Web Token) con la librería jjwt o similar.
 */
@Schema(description = "Respuesta de autenticación exitosa (login o register)")
public class AuthResponse {

    // Token de autenticación (simulado por ahora, implementar JWT en el futuro)
    // El frontend guardará este token para futuras peticiones
    @Schema(description = "Token de autenticación (simulado por ahora, implementar JWT en el futuro)",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    // Información completa del usuario autenticado (sin password)
    @Schema(description = "Información del usuario autenticado")
    private UserResponse user;

    // Mensaje de confirmación para el usuario
    @Schema(description = "Mensaje de éxito", example = "Login exitoso")
    private String message;

    // Constructor vacío requerido por Jackson
    public AuthResponse() {
    }

    // Constructor completo (útil para crear respuestas en el servicio)
    public AuthResponse(String token, UserResponse user, String message) {
        this.token = token;
        this.user = user;
        this.message = message;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
