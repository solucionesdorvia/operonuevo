package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para actualizar información de un departamento.
 *
 * ¿Qué hace este DTO?
 * - Recibe los datos para actualizar un departamento existente
 * - Permite cambiar el nombre del departamento
 * - Permite cambiar el gerente (manager) del departamento
 * - Todos los campos son opcionales (actualización parcial)
 *
 * Usado por: PUT /api/departments/{id}
 *
 * Nota: Este DTO es diferente de UpdateDepartmentRequest, que se usa para
 * derivar incidentes a otro departamento (PATCH /api/incidents/{id}/department)
 */
@Schema(description = "Datos para actualizar un departamento existente")
public class DepartmentUpdateRequest {

    // Nuevo nombre del departamento
    @Schema(description = "Nombre del departamento",
            example = "Mantenimiento General",
            required = false)
    private String name;

    // ID del nuevo gerente (manager) del departamento
    @Schema(description = "ID del usuario que será el nuevo gerente del departamento",
            example = "3",
            required = false)
    private Integer managerId;

    // Constructor vacío requerido por Jackson
    public DepartmentUpdateRequest() {
    }

    // Constructor completo
    public DepartmentUpdateRequest(String name, Integer managerId) {
        this.name = name;
        this.managerId = managerId;
    }

    // Getters y Setters
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
}
