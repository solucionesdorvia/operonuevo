package com.opero.api.dto;

import com.opero.api.entity.IncidentPriority;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para cambiar la prioridad de un incidente.
 *
 * ¿Qué hace este DTO?
 * - Recibe la nueva prioridad de un incidente
 * - Se usa cuando un gerente necesita ajustar la prioridad según la urgencia
 * - Las prioridades son: LOW, MEDIUM, HIGH
 *
 * Usado por: PATCH /api/incidents/{id}/priority
 *
 * Criterios de prioridad (ejemplo):
 * - LOW: Puede esperar, no es urgente
 * - MEDIUM: Importante pero no crítico
 * - HIGH: Urgente, requiere atención inmediata
 */
@Schema(description = "Datos para cambiar la prioridad de un incidente")
public class UpdatePriorityRequest {

    // Nueva prioridad del incidente
    @Schema(description = "Nueva prioridad del incidente",
            example = "HIGH",
            required = true,
            allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private IncidentPriority priority;

    // Constructor vacío requerido por Jackson
    public UpdatePriorityRequest() {
    }

    // Constructor con parámetro
    public UpdatePriorityRequest(IncidentPriority priority) {
        this.priority = priority;
    }

    // Getters y Setters
    public IncidentPriority getPriority() {
        return priority;
    }

    public void setPriority(IncidentPriority priority) {
        this.priority = priority;
    }
}
