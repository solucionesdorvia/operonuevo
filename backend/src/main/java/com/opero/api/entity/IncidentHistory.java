package com.opero.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

/**
 * Entidad JPA para el historial de cambios de incidentes.
 *
 * ¿Qué hace esta entidad?
 * - Registra cada cambio que ocurre en un incidente (auditoría)
 * - Guarda qué cambió, quién lo cambió y cuándo
 * - Permite rastrear la evolución de un incidente a lo largo del tiempo
 *
 * Campos principales:
 * - incident: Referencia al incidente modificado
 * - changeType: Tipo de cambio (STATUS_CHANGE, WORKER_ASSIGNED, PRIORITY_CHANGE, etc.)
 * - oldValue: Valor anterior del campo modificado
 * - newValue: Valor nuevo del campo modificado
 * - changedBy: Usuario que realizó el cambio
 * - changedAt: Fecha y hora del cambio
 *
 * Ejemplos de registros:
 * - "Estado cambió de PENDING a ASSIGNED por usuario 3 a las 10:30"
 * - "Trabajador asignado: Ana Martínez (ID 4) por usuario 3 a las 10:35"
 * - "Prioridad cambió de MEDIUM a HIGH por usuario 1 a las 11:00"
 */
@Entity
@Table(name = "incident_history")
public class IncidentHistory {

    // ID auto-incremental
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Relación con el incidente (muchos registros de historial pertenecen a un incidente)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    private Incident incident;

    // Tipo de cambio realizado
    @Column(name = "change_type", nullable = false, length = 50)
    private String changeType;  // STATUS_CHANGE, WORKER_ASSIGNED, PRIORITY_CHANGE, DEPARTMENT_CHANGE, etc.

    // Valor anterior (puede ser null en caso de creación)
    @Column(name = "old_value", length = 255)
    private String oldValue;

    // Valor nuevo
    @Column(name = "new_value", length = 255)
    private String newValue;

    // Relación con el usuario que realizó el cambio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_user_id")
    private User changedBy;

    // Fecha y hora del cambio (se establece automáticamente)
    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    // Constructor vacío requerido por JPA
    public IncidentHistory() {
    }

    // Constructor completo
    public IncidentHistory(Incident incident, String changeType, String oldValue, String newValue, User changedBy) {
        this.incident = incident;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
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

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
