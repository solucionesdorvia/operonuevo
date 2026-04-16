package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para enviar información del usuario al frontend.
 *
 * ¿Qué hace este DTO?
 * - Representa la información del usuario SIN datos sensibles (no incluye password)
 * - Se usa en las respuestas de autenticación y en endpoints de consulta de usuarios
 * - Incluye información del rol y departamento para mostrar en la app móvil
 *
 * Usado por:
 * - POST /api/auth/login (respuesta)
 * - POST /api/auth/register (respuesta)
 * - GET /api/auth/me (respuesta)
 * - GET /api/users (respuesta)
 */
@Schema(description = "Información del usuario (sin datos sensibles como password)")
public class UserResponse {

    // ID único del usuario en la base de datos
    @Schema(description = "ID único del usuario", example = "1")
    private Integer id;

    // Nombre completo del usuario
    @Schema(description = "Nombre completo del usuario", example = "Juan Pérez")
    private String fullName;

    // Email institucional UADE
    @Schema(description = "Email institucional del usuario", example = "juan.perez@uade.edu.ar")
    private String emailUade;

    // Nombre del rol (STUDENT, PROFESSOR, MANAGER, WORKER)
    @Schema(description = "Nombre del rol del usuario", example = "STUDENT")
    private String roleName;

    // ID del rol (útil para el frontend al tomar decisiones)
    @Schema(description = "ID del rol", example = "1")
    private Integer roleId;

    // ID del departamento (puede ser null para alumnos y profesores)
    @Schema(description = "ID del departamento (si aplica)", example = "1")
    private Integer departmentId;

    // Nombre del departamento (puede ser null para alumnos y profesores)
    @Schema(description = "Nombre del departamento (si aplica)", example = "Mantenimiento")
    private String departmentName;

    // Fecha en que se creó la cuenta
    @Schema(description = "Fecha de creación de la cuenta")
    private LocalDateTime createdAt;

    // Constructor vacío requerido por Jackson
    public UserResponse() {
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailUade() {
        return emailUade;
    }

    public void setEmailUade(String emailUade) {
        this.emailUade = emailUade;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
