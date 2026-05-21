package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para asignar un trabajador a un incidente.
 *
 * ¿Qué hace este DTO?
 * - Recibe el ID del trabajador que se asignará al incidente
 * - Se usa cuando un gerente de departamento asigna un trabajador de su equipo
 * - El trabajador debe pertenecer al mismo departamento del incidente
 *
 * Usado por: PATCH /api/incidents/{id}/assign
 *
 * Flujo de uso:
 * 1. Un incidente llega al departamento (status: PENDING_ASSIGNMENT)
 * 2. El gerente del departamento ve el incidente
 * 3. El gerente asigna un trabajador de su equipo usando este endpoint
 * 4. El status cambia automáticamente a ASSIGNED
 */
@Schema(description = "Datos para asignar un trabajador a un incidente")
public class AssignWorkerRequest {

    // ID del trabajador (usuario con rol WORKER)
    @Schema(description = "ID del trabajador que se asignará al incidente",
            example = "4",
            required = true)
    private Integer workerId;

    // Constructor vacío requerido por Jackson
    public AssignWorkerRequest() {
    }

    // Constructor con parámetro
    public AssignWorkerRequest(Integer workerId) {
        this.workerId = workerId;
    }

    // Getters y Setters
    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }
}
