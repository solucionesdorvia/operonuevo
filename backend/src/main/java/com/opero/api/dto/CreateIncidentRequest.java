package com.opero.api.dto;

import com.opero.api.entity.IncidentPriority;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO para crear un nuevo incidente.
 *
 * ¿Qué campos incluye?
 * - title: Título descriptivo del incidente
 * - description: Descripción detallada del problema
 * - locationDescription: Ubicación física donde ocurre el problema
 * - photoUrl: (Opcional) URL de la foto del incidente
 * - priority: Prioridad del incidente (LOW, MEDIUM, HIGH)
 * - departmentId: ID del departamento al que se asigna el incidente
 * - reporterId: ID del usuario que reporta el incidente
 *
 * Campos que NO se incluyen (se generan automáticamente):
 * - id: Se genera automáticamente en la base de datos
 * - status: Se inicializa como PENDING
 * - worker: Se inicializa como null (sin asignar)
 * - createdAt: Se genera automáticamente
 * - updatedAt: Se genera automáticamente
 */
@Schema(description = "Datos para crear un nuevo incidente")
public class CreateIncidentRequest {

    @Schema(description = "Título descriptivo del incidente", example = "Aire acondicionado no funciona", required = true)
    private String title;

    @Schema(description = "Descripción detallada del problema", example = "El AC del aula 301 no enciende", required = true)
    private String description;

    @Schema(description = "Ubicación física donde ocurre el problema", example = "Aula 301, 3er piso", required = true)
    private String locationDescription;

    @Schema(description = "URL de la foto del incidente (opcional)", example = "https://example.com/photo.jpg", required = false)
    private String photoUrl;

    @Schema(description = "Prioridad del incidente", example = "HIGH", required = true, allowableValues = {"LOW", "MEDIUM", "HIGH"})
    private IncidentPriority priority;

    @Schema(description = "ID del departamento al que se asigna el incidente", example = "1", required = true)
    private Integer departmentId;

    @Schema(description = "ID del usuario que reporta el incidente", example = "1", required = true)
    private Integer reporterId;

    // Constructores
    public CreateIncidentRequest() {
    }

    // Getters y Setters
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

    public IncidentPriority getPriority() {
        return priority;
    }

    public void setPriority(IncidentPriority priority) {
        this.priority = priority;
    }

    public Integer getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Integer departmentId) {
        this.departmentId = departmentId;
    }

    public Integer getReporterId() {
        return reporterId;
    }

    public void setReporterId(Integer reporterId) {
        this.reporterId = reporterId;
    }
}
