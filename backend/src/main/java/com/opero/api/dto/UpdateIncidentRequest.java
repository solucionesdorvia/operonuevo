package com.opero.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO (Data Transfer Object) para actualizar un incidente existente.
 *
 * ¿Qué hace este DTO?
 * - Recibe los datos para actualizar un incidente completo (PUT)
 * - Permite modificar título, descripción, ubicación y foto
 * - NO se usa para cambiar status, prioridad, trabajador o departamento (tienen endpoints específicos)
 *
 * Usado por: PUT /api/incidents/{id}
 */
@Schema(description = "Datos para actualizar un incidente existente")
public class UpdateIncidentRequest {

    // Nuevo título del incidente
    @Schema(description = "Título del incidente",
            example = "Aire acondicionado no funciona",
            required = false)
    private String title;

    // Nueva descripción del incidente
    @Schema(description = "Descripción detallada del incidente",
            example = "El aire acondicionado del aula 301 no enciende",
            required = false)
    private String description;

    // Nueva ubicación del incidente
    @Schema(description = "Descripción de la ubicación del incidente",
            example = "Aula 301, 3er piso, Edificio A",
            required = false)
    private String locationDescription;

    // Nueva URL de la foto (opcional)
    @Schema(description = "URL de la foto del incidente (opcional)",
            example = "https://storage.example.com/incident-photo.jpg",
            required = false)
    private String photoUrl;

    // Constructor vacío requerido por Jackson
    public UpdateIncidentRequest() {
    }

    // Constructor completo
    public UpdateIncidentRequest(String title, String description, String locationDescription, String photoUrl) {
        this.title = title;
        this.description = description;
        this.locationDescription = locationDescription;
        this.photoUrl = photoUrl;
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
}
