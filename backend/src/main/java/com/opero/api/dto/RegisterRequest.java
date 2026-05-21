package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para el registro de nuevos usuarios.
 *
 * ¿Qué hace este DTO?
 * - Recibe los datos de registro desde el frontend (app móvil)
 * - Contiene toda la información necesaria para crear una nueva cuenta de usuario
 * - El password será hasheado antes de guardarse en la base de datos
 * - El roleId determina el tipo de usuario (alumno, profesor, gerente, trabajador)
 * - El departmentId es opcional y solo se usa para trabajadores y gerentes
 *
 * Usado por: POST /api/auth/register
 */
@Schema(description = "Datos requeridos para registrar un nuevo usuario")
public class RegisterRequest {

    // Nombre completo del usuario (ej: "Juan Pérez")
    @Schema(description = "Nombre completo del usuario",
            example = "Juan Pérez",
            required = true)
    private String fullName;

    // Email institucional UADE (debe ser único en el sistema)
    @Schema(description = "Email institucional del usuario (debe ser único)",
            example = "juan.perez@uade.edu.ar",
            required = true)
    private String emailUade;

    // Contraseña en texto plano (se hasheará en el servicio antes de guardar)
    @Schema(description = "Contraseña del usuario (será hasheada antes de almacenar)",
            example = "miPassword123",
            required = true)
    private String password;

    // ID del rol: 1=USER, 2=MANAGER, 3=WORKER
    @Schema(description = "ID del rol asignado al usuario (1=USER, 2=MANAGER, 3=WORKER)",
            example = "1",
            required = true)
    private Integer roleId;

    // ID del departamento (solo para MANAGER y WORKER, null para USER)
    @Schema(description = "ID del departamento (opcional, solo para trabajadores y gerentes)",
            example = "1",
            required = false)
    private Integer departmentId;

    // Constructor vacío requerido por Jackson (para deserializar JSON)
    public RegisterRequest() {
    }

    // Constructor completo (útil para crear objetos en tests)
    public RegisterRequest(String fullName, String emailUade, String password, Integer roleId, Integer departmentId) {
        this.fullName = fullName;
        this.emailUade = emailUade;
        this.password = password;
        this.roleId = roleId;
        this.departmentId = departmentId;
    }

    // Getters y Setters‹
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
