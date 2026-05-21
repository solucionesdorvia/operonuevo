package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para el inicio de sesión.
 *
 * ¿Qué hace este DTO?
 * - Recibe las credenciales de login desde el frontend (email y contraseña)
 * - Se usa para autenticar usuarios existentes en el sistema
 * - El password se compara con el hash almacenado en la base de datos
 *
 * Usado por: POST /api/auth/login
 */
@Schema(description = "Datos requeridos para iniciar sesión")
public class LoginRequest {

    // Email institucional UADE del usuario
    @Schema(description = "Email institucional del usuario (ej: usuario@uade.edu.ar)",
            example = "juan.perez@uade.edu.ar",
            required = true)
    private String emailUade;

    // Contraseña en texto plano (se comparará con el hash en BD)
    @Schema(description = "Contraseña del usuario",
            example = "miPassword123",
            required = true)
    private String password;

    // Constructor vacío requerido por Jackson (para deserializar JSON)
    public LoginRequest() {
    }

    // Constructor completo (útil para crear objetos en tests)
    public LoginRequest(String emailUade, String password) {
        this.emailUade = emailUade;
        this.password = password;
    }

    // Getters y Setters
    public String getEmailUade() {
        return emailUade;
    }

    public void setEmailUade(String emailUade) {
        this.emailUade = emailUade;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
