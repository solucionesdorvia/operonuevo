package com.opero.api.controller;

import com.opero.api.dto.*;
import com.opero.api.entity.Incident;
import com.opero.api.entity.IncidentStatus;
import com.opero.api.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller (Controlador) de Gestión de Incidentes.
 *
 * ¿Qué hace este Controller?
 * - Expone los endpoints REST para gestionar incidentes
 * - Recibe las peticiones HTTP del frontend (app móvil)
 * - Delega la lógica de negocio al IncidentService
 * - Retorna respuestas HTTP con códigos de estado apropiados
 * - Documenta automáticamente la API con Swagger
 *
 * Endpoints expuestos:
 * - GET /api/incidents: Listar todos los incidentes
 * - POST /api/incidents: Crear nuevo incidente
 * - GET /api/incidents/{id}: Obtener un incidente específico
 * - PUT /api/incidents/{id}: Actualizar incidente completo
 * - DELETE /api/incidents/{id}: Eliminar incidente
 * - PATCH /api/incidents/{id}/status: Cambiar estado
 * - PATCH /api/incidents/{id}/assign: Asignar trabajador
 * - PATCH /api/incidents/{id}/priority: Cambiar prioridad
 * - PATCH /api/incidents/{id}/department: Derivar a otro departamento
 */
@RestController
@RequestMapping("/api/incidents")
@Tag(name = "Gestión de Incidentes", description = "APIs para la creación, consulta, actualización y eliminación de incidentes reportados por usuarios")
public class IncidentController {

    // Inyección del servicio de incidentes
    @Autowired
    private IncidentService incidentService;

    /**
     * GET /api/incidents - Listar todos los incidentes (con filtros opcionales)
     *
     * ¿Qué hace este endpoint?
     * - Obtiene todos los incidentes registrados en el sistema
     * - Permite filtrar por estado, reportero, trabajador y departamento
     * - Retorna una lista con información completa de cada incidente
     * - Útil para pantallas de inicio o vistas de administración
     *
     * @param status (Opcional) Filtrar por estado del incidente
     * @param reporterId (Opcional) Filtrar por ID del usuario que reportó
     * @param workerId (Opcional) Filtrar por ID del trabajador asignado
     * @param departmentId (Opcional) Filtrar por ID del departamento
     * @return Lista de IncidentResponse con todos los incidentes filtrados
     */
    @GetMapping
    @Operation(
        summary = "Listar todos los incidentes con filtros opcionales",
        description = "Obtiene una lista de incidentes registrados en el sistema. " +
                      "Permite aplicar filtros opcionales por estado, reportero, trabajador asignado y departamento. " +
                      "Retorna información detallada incluyendo reporter, worker y department. " +
                      "Si no se proporcionan filtros, retorna todos los incidentes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de incidentes obtenida exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<List<IncidentResponse>> getAllIncidents(
            @Parameter(description = "Filtrar por estado (PENDING, PENDING_ASSIGNMENT, ASSIGNED, IN_PROCESS, FINISHED)")
            @RequestParam(required = false) IncidentStatus status,
            @Parameter(description = "Filtrar por ID del usuario que reportó el incidente")
            @RequestParam(required = false) Integer reporterId,
            @Parameter(description = "Filtrar por ID del trabajador asignado al incidente")
            @RequestParam(required = false) Integer workerId,
            @Parameter(description = "Filtrar por ID del departamento responsable del incidente")
            @RequestParam(required = false) Integer departmentId
    ) {
        List<IncidentResponse> incidents = incidentService.getAllIncidents(status, reporterId, workerId, departmentId);
        return ResponseEntity.ok(incidents);
    }

    /**
     * POST /api/incidents - Crear nuevo incidente
     *
     * ¿Qué hace este endpoint?
     * - Crea un nuevo reporte de incidente en el sistema
     * - Usado por alumnos y profesores para reportar problemas
     * - Requiere título, descripción, ubicación, departamento y reporter
     *
     * @param incident Datos del nuevo incidente
     * @return Incident creado
     */
    @PostMapping
    @Operation(
        summary = "Crear un nuevo incidente",
        description = "Crea un nuevo reporte de incidente. Este endpoint es utilizado por alumnos y profesores " +
                      "para reportar problemas de infraestructura en la institución. " +
                      "Requiere título, descripción, ubicación, departamento asignado y el ID del usuario que reporta."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Incidente creado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Incident.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos de entrada inválidos (campos requeridos faltantes o formato incorrecto)",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<Incident> createIncident(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del incidente a crear. Debe incluir título, descripción, ubicación, departamento asignado y reporterId.",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Incident.class)
            )
        )
        @RequestBody Incident incident
    ) {
        // Por ahora usamos el repository directamente
        // TODO: Migrar a usar incidentService.createIncident()
        return ResponseEntity.ok(incident);
    }

    /**
     * GET /api/incidents/{id} - Obtener un incidente específico
     *
     * ¿Qué hace este endpoint?
     * - Busca un incidente específico por su ID
     * - Retorna toda la información del incidente incluyendo relaciones
     * - Usado para la pantalla de "Detalle de incidencia"
     *
     * @param id ID del incidente a buscar
     * @return IncidentResponse con la información del incidente
     */
    @GetMapping("/{id}")
    @Operation(
        summary = "Obtener un incidente específico",
        description = "Busca y retorna un incidente específico por su ID. " +
                      "Incluye toda la información del incidente: título, descripción, estado, prioridad, " +
                      "información del reporter, worker asignado y departamento. " +
                      "Usado en la pantalla de 'Detalle de incidencia' de la app móvil."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Incidente encontrado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente no encontrado con el ID especificado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> getIncidentById(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente a buscar",
            required = true,
            example = "1"
        )
        @PathVariable Integer id
    ) {
        try {
            IncidentResponse incident = incidentService.getIncidentById(id);
            return ResponseEntity.ok(incident);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PUT /api/incidents/{id} - Actualizar incidente completo
     *
     * ¿Qué hace este endpoint?
     * - Actualiza los datos básicos de un incidente existente
     * - Permite modificar título, descripción, ubicación y foto
     * - NO modifica status, prioridad, worker ni departamento (tienen endpoints específicos)
     *
     * @param id ID del incidente a actualizar
     * @param request Datos de actualización
     * @return IncidentResponse con el incidente actualizado
     */
    @PutMapping("/{id}")
    @Operation(
        summary = "Actualizar incidente completo",
        description = "Actualiza los datos básicos de un incidente existente. " +
                      "Permite modificar: título, descripción, ubicación y foto. " +
                      "Para cambiar status, prioridad, trabajador o departamento, usar los endpoints PATCH específicos."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Incidente actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente no encontrado con el ID especificado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> updateIncident(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente a actualizar",
            required = true,
            example = "1"
        )
        @PathVariable Integer id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos a actualizar del incidente. Todos los campos son opcionales.",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateIncidentRequest.class)
            )
        )
        @RequestBody UpdateIncidentRequest request
    ) {
        try {
            IncidentResponse updatedIncident = incidentService.updateIncident(id, request);
            return ResponseEntity.ok(updatedIncident);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * DELETE /api/incidents/{id} - Eliminar incidente
     *
     * ¿Qué hace este endpoint?
     * - Elimina un incidente del sistema de forma permanente
     * - Validación: solo gerentes o administradores deberían poder eliminar
     * - TODO: Agregar validación de permisos
     *
     * @param id ID del incidente a eliminar
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @Operation(
        summary = "Eliminar incidente",
        description = "Elimina un incidente del sistema de forma permanente. " +
                      "Esta operación no se puede deshacer. " +
                      "Típicamente solo gerentes o administradores deberían tener permiso para eliminar incidentes."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Incidente eliminado exitosamente",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente no encontrado con el ID especificado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> deleteIncident(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente a eliminar",
            required = true,
            example = "1"
        )
        @PathVariable Integer id
    ) {
        try {
            incidentService.deleteIncident(id);
            return ResponseEntity.ok("Incidente eliminado exitosamente");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PATCH /api/incidents/{id}/status - Cambiar estado del incidente
     *
     * ¿Qué hace este endpoint?
     * - Cambia el estado de un incidente
     * - Estados: PENDING, PENDING_ASSIGNMENT, ASSIGNED, IN_PROCESS, FINISHED
     * - Usado por trabajadores para actualizar el progreso
     *
     * @param id ID del incidente
     * @param request Nuevo estado
     * @return IncidentResponse con el incidente actualizado
     */
    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Cambiar estado del incidente",
        description = "Actualiza el estado de un incidente. " +
                      "Estados disponibles: PENDING, PENDING_ASSIGNMENT, ASSIGNED, IN_PROCESS, FINISHED. " +
                      "Usado por trabajadores para marcar cuando inician el trabajo (IN_PROCESS) o cuando lo finalizan (FINISHED)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Estado actualizado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente no encontrado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> updateStatus(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente",
            required = true,
            example = "1"
        )
        @PathVariable Integer id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nuevo estado del incidente",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateStatusRequest.class)
            )
        )
        @RequestBody UpdateStatusRequest request
    ) {
        try {
            IncidentResponse updatedIncident = incidentService.updateStatus(id, request);
            return ResponseEntity.ok(updatedIncident);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PATCH /api/incidents/{id}/assign - Asignar trabajador al incidente
     *
     * ¿Qué hace este endpoint?
     * - Asigna un trabajador específico al incidente
     * - Usado por gerentes para distribuir trabajo a su equipo
     * - Cambia automáticamente el status a ASSIGNED
     *
     * @param id ID del incidente
     * @param request ID del trabajador a asignar
     * @return IncidentResponse con el incidente actualizado
     */
    @PatchMapping("/{id}/assign")
    @Operation(
        summary = "Asignar trabajador al incidente",
        description = "Asigna un trabajador específico a un incidente. " +
                      "Usado por gerentes de departamento para asignar incidentes a miembros de su equipo. " +
                      "El status del incidente cambia automáticamente a ASSIGNED después de la asignación."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Trabajador asignado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente o trabajador no encontrado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> assignWorker(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente",
            required = true,
            example = "1"
        )
        @PathVariable Integer id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ID del trabajador a asignar al incidente",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AssignWorkerRequest.class)
            )
        )
        @RequestBody AssignWorkerRequest request
    ) {
        try {
            IncidentResponse updatedIncident = incidentService.assignWorker(id, request);
            return ResponseEntity.ok(updatedIncident);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PATCH /api/incidents/{id}/priority - Cambiar prioridad del incidente
     *
     * ¿Qué hace este endpoint?
     * - Cambia la prioridad de un incidente
     * - Prioridades: LOW, MEDIUM, HIGH
     * - Usado por gerentes para priorizar trabajo
     *
     * @param id ID del incidente
     * @param request Nueva prioridad
     * @return IncidentResponse con el incidente actualizado
     */
    @PatchMapping("/{id}/priority")
    @Operation(
        summary = "Cambiar prioridad del incidente",
        description = "Actualiza la prioridad de un incidente. " +
                      "Prioridades disponibles: LOW (baja), MEDIUM (media), HIGH (alta). " +
                      "Usado por gerentes para ajustar la urgencia según la criticidad del problema."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Prioridad actualizada exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente no encontrado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> updatePriority(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente",
            required = true,
            example = "1"
        )
        @PathVariable Integer id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Nueva prioridad del incidente",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdatePriorityRequest.class)
            )
        )
        @RequestBody UpdatePriorityRequest request
    ) {
        try {
            IncidentResponse updatedIncident = incidentService.updatePriority(id, request);
            return ResponseEntity.ok(updatedIncident);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * PATCH /api/incidents/{id}/department - Derivar incidente a otro departamento
     *
     * ¿Qué hace este endpoint?
     * - Deriva un incidente a un departamento diferente
     * - Desasigna el trabajador actual (vuelve a NULL)
     * - Cambia el status a PENDING_ASSIGNMENT
     * - Usado cuando un gerente determina que el incidente no corresponde a su área
     *
     * @param id ID del incidente
     * @param request ID del nuevo departamento
     * @return IncidentResponse con el incidente actualizado
     */
    @PatchMapping("/{id}/department")
    @Operation(
        summary = "Derivar incidente a otro departamento",
        description = "Deriva un incidente a un departamento diferente. " +
                      "Usado cuando un gerente determina que el incidente no corresponde a su área. " +
                      "Al derivar, el trabajador asignado se desvincula (vuelve a NULL) y el status cambia a PENDING_ASSIGNMENT. " +
                      "El nuevo departamento deberá asignar un trabajador de su equipo."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Incidente derivado exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente o departamento no encontrado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> updateDepartment(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente",
            required = true,
            example = "1"
        )
        @PathVariable Integer id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "ID del departamento al que se derivará el incidente",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UpdateDepartmentRequest.class)
            )
        )
        @RequestBody UpdateDepartmentRequest request
    ) {
        try {
            IncidentResponse updatedIncident = incidentService.updateDepartment(id, request);
            return ResponseEntity.ok(updatedIncident);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * GET /api/incidents/{id}/history - Obtener historial de cambios de un incidente
     *
     * ¿Qué hace este endpoint?
     * - Retorna todos los cambios que ha sufrido un incidente
     * - Muestra qué cambió, de qué valor a qué valor, quién lo cambió y cuándo
     * - Útil para auditoría y para mostrar un timeline del incidente
     * - Los cambios se ordenan por fecha descendente (más recientes primero)
     *
     * @param id ID del incidente
     * @return Lista de IncidentHistoryResponse con el historial de cambios
     */
    @GetMapping("/{id}/history")
    @Operation(
        summary = "Obtener historial de cambios de un incidente",
        description = "Retorna todos los cambios que ha sufrido un incidente. " +
                      "Incluye tipo de cambio, valor anterior, valor nuevo, usuario que realizó el cambio y fecha/hora. " +
                      "Los cambios se ordenan por fecha descendente (más recientes primero). " +
                      "Útil para auditoría y para mostrar un timeline del incidente en el frontend."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Historial obtenido exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = IncidentHistoryResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Incidente no encontrado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> getIncidentHistory(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "ID del incidente del cual se quiere obtener el historial",
            required = true,
            example = "1"
        )
        @PathVariable Integer id
    ) {
        try {
            List<IncidentHistoryResponse> history = incidentService.getIncidentHistory(id);
            return ResponseEntity.ok(history);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
