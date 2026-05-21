package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) de respuesta para un departamento.
 *
 * ¿Qué hace este DTO?
 * - Retorna la información completa de un departamento
 * - Incluye los datos del gerente (manager) si está asignado
 * - Se usa como respuesta de los endpoints de departamentos
 *
 * Usado por:
 * - GET /api/departments
 * - GET /api/departments/{id}
 * - PUT /api/departments/{id}
 *
 * Diferencias con la entidad Department:
 * - No incluye las listas de empleados ni incidentes (solo IDs básicos)
 * - Incluye información legible del manager (nombre y email)
 */
@Schema(description = "Respuesta con información de un departamento")
public class DepartmentResponse {

    // ID del departamento
    @Schema(description = "ID único del departamento", example = "1")
    private Integer id;

    // Nombre del departamento
    @Schema(description = "Nombre del departamento", example = "Mantenimiento")
    private String name;

    // ID del gerente (puede ser null)
    @Schema(description = "ID del gerente del departamento (puede ser null si no está asignado)", example = "3")
    private Integer managerId;

    // Nombre del gerente (puede ser null)
    @Schema(description = "Nombre completo del gerente (puede ser null si no está asignado)", example = "Carlos Rodríguez")
    private String managerName;

    // Email del gerente (puede ser null)
    @Schema(description = "Email del gerente (puede ser null si no está asignado)", example = "carlos.rodriguez@uade.edu.ar")
    private String managerEmail;

    // Constructor vacío
    public DepartmentResponse() {
    }

    // Constructor completo
    public DepartmentResponse(Integer id, String name, Integer managerId, String managerName, String managerEmail) {
        this.id = id;
        this.name = name;
        this.managerId = managerId;
        this.managerName = managerName;
        this.managerEmail = managerEmail;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerEmail() {
        return managerEmail;
    }

    public void setManagerEmail(String managerEmail) {
        this.managerEmail = managerEmail;
    }
}
