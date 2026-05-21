package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) de respuesta para el historial de un incidente.
 *
 * ¿Qué hace este DTO?
 * - Retorna la información de un registro de cambio en el historial
 * - Incluye qué cambió, los valores anterior y nuevo, quién lo cambió y cuándo
 * - Se usa para mostrar el timeline de cambios de un incidente
 *
 * Usado por: GET /api/incidents/{id}/history
 *
 * Ejemplo de respuesta:
 * {
 *   "id": 1,
 *   "changeType": "STATUS_CHANGE",
 *   "oldValue": "PENDING",
 *   "newValue": "ASSIGNED",
 *   "changedByUserId": 3,
 *   "changedByUserName": "Carlos Rodríguez",
 *   "changedAt": "2024-01-15T10:30:00"
 * }
 */
@Schema(description = "Registro de cambio en el historial de un incidente")
public class IncidentHistoryResponse {

    // ID del registro de historial
    @Schema(description = "ID único del registro de historial", example = "1")
    private Integer id;

    // Tipo de cambio
    @Schema(description = "Tipo de cambio realizado", example = "STATUS_CHANGE")
    private String changeType;

    // Valor anterior
    @Schema(description = "Valor anterior del campo (puede ser null)", example = "PENDING")
    private String oldValue;

    // Valor nuevo
    @Schema(description = "Valor nuevo del campo", example = "ASSIGNED")
    private String newValue;

    // ID del usuario que realizó el cambio
    @Schema(description = "ID del usuario que realizó el cambio", example = "3")
    private Integer changedByUserId;

    // Nombre del usuario que realizó el cambio
    @Schema(description = "Nombre completo del usuario que realizó el cambio", example = "Carlos Rodríguez")
    private String changedByUserName;

    // Fecha y hora del cambio
    @Schema(description = "Fecha y hora del cambio", example = "2024-01-15T10:30:00")
    private LocalDateTime changedAt;

    // Constructor vacío
    public IncidentHistoryResponse() {
    }

    // Constructor completo
    public IncidentHistoryResponse(Integer id, String changeType, String oldValue, String newValue,
                                    Integer changedByUserId, String changedByUserName, LocalDateTime changedAt) {
        this.id = id;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedByUserId = changedByUserId;
        this.changedByUserName = changedByUserName;
        this.changedAt = changedAt;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChangeType() {
        return changeType;
    }

    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Integer getChangedByUserId() {
        return changedByUserId;
    }

    public void setChangedByUserId(Integer changedByUserId) {
        this.changedByUserId = changedByUserId;
    }

    public String getChangedByUserName() {
        return changedByUserName;
    }

    public void setChangedByUserName(String changedByUserName) {
        this.changedByUserName = changedByUserName;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
