package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para derivar un incidente a otro departamento.
 *
 * ¿Qué hace este DTO?
 * - Recibe el ID del departamento al que se derivará el incidente
 * - Se usa cuando un gerente determina que el incidente no corresponde a su departamento
 * - Al cambiar el departamento, el trabajador asignado se desvincula (vuelve a NULL)
 *
 * Usado por: PATCH /api/incidents/{id}/department
 *
 * Flujo de uso:
 * 1. Un incidente llega al departamento A
 * 2. El gerente de A revisa y determina que corresponde al departamento B
 * 3. El gerente deriva el incidente al departamento B usando este endpoint
 * 4. El incidente pasa al departamento B (worker_id vuelve a NULL)
 * 5. El gerente de B ahora puede asignar un trabajador de su equipo
 */
@Schema(description = "Datos para derivar un incidente a otro departamento")
public class UpdateDepartmentRequest {

    // ID del nuevo departamento
    @Schema(description = "ID del departamento al que se derivará el incidente",
            example = "2",
            required = true)
    private Integer departmentId;

    // Constructor vacío requerido por Jackson
    public UpdateDepartmentRequest() {
    }

    // Constructor con parámetro
    public UpdateDepartmentRequest(Integer departmentId) {
        this.departmentId = departmentId;
    }

    // Getters y Setters
    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }
}
