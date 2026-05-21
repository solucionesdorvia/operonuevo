package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para actualizar información de un usuario.
 *
 * ¿Qué hace este DTO?
 * - Recibe los datos para actualizar un usuario existente
 * - Se usa tanto para actualizar el propio perfil (PUT /api/users/me)
 *   como para que administradores actualicen otros usuarios (PUT /api/users/{id})
 * - Todos los campos son opcionales (actualización parcial)
 * - roleId y departmentId solo deberían poder ser modificados por administradores
 *
 * Usado por:
 * - PUT /api/users/me (usuario actualiza su propio perfil)
 * - PUT /api/users/{id} (administrador actualiza cualquier usuario)
 */
@Schema(description = "Datos para actualizar un usuario existente")
public class UpdateUserRequest {

    // Nuevo nombre completo del usuario
    @Schema(description = "Nombre completo del usuario",
            example = "Juan Pérez Actualizado",
            required = false)
    private String fullName;

    // Nuevo email institucional (debe ser único)
    @Schema(description = "Email institucional del usuario (debe ser único)",
            example = "juan.perez.nuevo@uade.edu.ar",
            required = false)
    private String emailUade;

    // Nueva contraseña (será hasheada antes de guardar)
    @Schema(description = "Nueva contraseña del usuario (será hasheada antes de guardar)",
            example = "nuevaPassword123",
            required = false)
    private String password;

    // Nuevo rol (solo para administradores)
    @Schema(description = "ID del nuevo rol (solo administradores pueden modificar esto)",
            example = "2",
            required = false)
    private Integer roleId;

    // Nuevo departamento (solo para administradores)
    @Schema(description = "ID del nuevo departamento (solo administradores pueden modificar esto)",
            example = "1",
            required = false)
    private Integer departmentId;

    // Constructor vacío requerido por Jackson
    public UpdateUserRequest() {
    }

    // Constructor completo
    public UpdateUserRequest(String fullName, String emailUade, String password, Integer roleId, Integer departmentId) {
        this.fullName = fullName;
        this.emailUade = emailUade;
        this.password = password;
        this.roleId = roleId;
        this.departmentId = departmentId;
    }

    // Getters y Setters
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
