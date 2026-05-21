package com.opero.api.dto;

import com.opero.api.entity.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para cambiar el estado de un incidente.
 *
 * ¿Qué hace este DTO?
 * - Recibe el nuevo estado de un incidente
 * - Se usa en el endpoint PATCH para cambiar solo el status (sin modificar otros campos)
 * - Los estados posibles son: PENDING, PENDING_ASSIGNMENT, ASSIGNED, IN_PROCESS, FINISHED
 *
 * Usado por: PATCH /api/incidents/{id}/status
 *
 * Flujo típico de estados:
 * 1. PENDING (reportado por alumno/profesor)
 * 2. PENDING_ASSIGNMENT (recibido por gerente, esperando asignación)
 * 3. ASSIGNED (asignado a un trabajador)
 * 4. IN_PROCESS (trabajador comenzó el trabajo)
 * 5. FINISHED (trabajo completado)
 */
@Schema(description = "Datos para cambiar el estado de un incidente")
public class UpdateStatusRequest {

    // Nuevo estado del incidente
    @Schema(description = "Nuevo estado del incidente",
            example = "IN_PROCESS",
            required = true,
            allowableValues = {"PENDING", "PENDING_ASSIGNMENT", "ASSIGNED", "IN_PROCESS", "FINISHED"})
    private IncidentStatus status;

    // Constructor vacío requerido por Jackson
    public UpdateStatusRequest() {
    }

    // Constructor con parámetro
    public UpdateStatusRequest(IncidentStatus status) {
        this.status = status;
    }

    // Getters y Setters
    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }
}
