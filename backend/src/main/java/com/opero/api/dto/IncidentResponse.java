package com.opero.api.dto;

import com.opero.api.entity.IncidentPriority;
import com.opero.api.entity.IncidentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para enviar información completa de un incidente.
 *
 * ¿Qué hace este DTO?
 * - Representa un incidente con toda su información (sin datos sensibles)
 * - Incluye información legible del reporter, worker y department (no solo IDs)
 * - Se usa en las respuestas de GET para mostrar incidentes en la app móvil
 *
 * Usado por:
 * - GET /api/incidents/{id} (obtener un incidente específico)
 * - GET /api/incidents (listar todos los incidentes)
 * - PUT /api/incidents/{id} (respuesta después de actualizar)
 * - PATCH /api/incidents/{id}/* (respuesta después de modificar)
 */
@Schema(description = "Información completa de un incidente")
public class IncidentResponse {

    // ID del incidente
    @Schema(description = "ID único del incidente", example = "1")
    private Integer id;

    // Título del incidente
    @Schema(description = "Título del incidente", example = "Aire acondicionado no funciona")
    private String title;

    // Descripción del incidente
    @Schema(description = "Descripción detallada del incidente", example = "El aire acondicionado del aula 301 no enciende")
    private String description;

    // Ubicación del incidente
    @Schema(description = "Descripción de la ubicación", example = "Aula 301, 3er piso")
    private String locationDescription;

    // URL de la foto (opcional)
    @Schema(description = "URL de la foto del incidente", example = "https://storage.example.com/photo.jpg")
    private String photoUrl;

    // Estado del incidente
    @Schema(description = "Estado actual del incidente", example = "IN_PROCESS")
    private IncidentStatus status;

    // Prioridad del incidente
    @Schema(description = "Prioridad del incidente", example = "HIGH")
    private IncidentPriority priority;

    // Información del usuario que reportó (solo campos relevantes)
    @Schema(description = "ID del usuario que reportó el incidente", example = "1")
    private Integer reporterId;

    @Schema(description = "Nombre completo del usuario que reportó", example = "Juan Pérez")
    private String reporterName;

    @Schema(description = "Email del usuario que reportó", example = "juan.perez@uade.edu.ar")
    private String reporterEmail;

    // Información del trabajador asignado (puede ser null)
    @Schema(description = "ID del trabajador asignado (null si no hay asignación)", example = "4")
    private Integer workerId;

    @Schema(description = "Nombre completo del trabajador asignado", example = "Ana Martínez")
    private String workerName;

    // Información del departamento
    @Schema(description = "ID del departamento responsable", example = "1")
    private Integer departmentId;

    @Schema(description = "Nombre del departamento responsable", example = "Mantenimiento")
    private String departmentName;

    // Fechas de auditoría
    @Schema(description = "Fecha de creación del incidente")
    private LocalDateTime createdAt;

    @Schema(description = "Fecha de última actualización del incidente")
    private LocalDateTime updatedAt;

    // Constructor vacío requerido por Jackson
    public IncidentResponse() {
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public IncidentStatus getStatus() {
        return status;
    }

    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    public IncidentPriority getPriority() {
        return priority;
    }

    public void setPriority(IncidentPriority priority) {
        this.priority = priority;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public Integer getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Integer workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
