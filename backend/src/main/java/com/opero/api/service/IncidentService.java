package com.opero.api.service;

import com.opero.api.dto.*;
import com.opero.api.entity.*;
import com.opero.api.repository.DepartmentRepository;
import com.opero.api.repository.IncidentRepository;
import com.opero.api.repository.IncidentHistoryRepository;
import com.opero.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service (Servicio) de Gestión de Incidentes.
 *
 * ¿Qué hace este Service?
 * - Contiene toda la lógica de negocio relacionada con incidentes
 * - Maneja CRUD completo: crear, leer, actualizar, eliminar
 * - Maneja operaciones específicas: cambiar status, asignar trabajador, cambiar prioridad, derivar
 * - Convierte entidades JPA a DTOs para enviar al frontend
 * - Valida datos antes de realizar operaciones
 */
@Service
public class IncidentService {

    // Inyección de dependencias de los repositories
    @Autowired
    private IncidentRepository incidentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private IncidentHistoryRepository incidentHistoryRepository;

    /**
     * Obtener un incidente por su ID.
     *
     * ¿Qué hace este método?
     * - Busca un incidente específico en la base de datos por su ID
     * - Convierte la entidad Incident a IncidentResponse DTO
     * - Lanza excepción si el incidente no existe
     *
     * @param id ID del incidente a buscar
     * @return IncidentResponse con toda la información del incidente
     * @throws RuntimeException si el incidente no existe
     *
     * Usado por: GET /api/incidents/{id}
     */
    public IncidentResponse getIncidentById(Integer id) {
        // Buscar incidente por ID
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado con ID: " + id));

        // Convertir a DTO y retornar
        return convertToIncidentResponse(incident);
    }

    /**
     * Obtener todos los incidentes (con filtros opcionales).
     *
     * ¿Qué hace este método?
     * - Obtiene todos los incidentes de la base de datos
     * - Aplica filtros opcionales si se proporcionan
     * - Convierte cada Incident a IncidentResponse
     *
     * @param status (Opcional) Filtrar por estado del incidente
     * @param reporterId (Opcional) Filtrar por ID del usuario que reportó
     * @param workerId (Opcional) Filtrar por ID del trabajador asignado
     * @param departmentId (Opcional) Filtrar por ID del departamento
     * @return Lista de IncidentResponse con los incidentes filtrados
     *
     * Usado por: GET /api/incidents
     *
     * Ejemplos de uso:
     * - GET /api/incidents → Todos los incidentes
     * - GET /api/incidents?status=PENDING → Solo incidentes pendientes
     * - GET /api/incidents?reporterId=1 → Solo incidentes reportados por el usuario 1
     * - GET /api/incidents?workerId=4 → Solo incidentes asignados al trabajador 4
     * - GET /api/incidents?departmentId=1 → Solo incidentes del departamento 1
     * - GET /api/incidents?status=IN_PROCESS&departmentId=1 → Combinación de filtros
     */
    public List<IncidentResponse> getAllIncidents(IncidentStatus status, Integer reporterId,
                                                   Integer workerId, Integer departmentId) {
        // Obtener todos los incidentes
        List<Incident> incidents = incidentRepository.findAll();

        // Aplicar filtros usando streams
        return incidents.stream()
                // Filtrar por status si se proporcionó
                .filter(incident -> status == null || incident.getStatus() == status)
                // Filtrar por reporterId si se proporcionó
                .filter(incident -> reporterId == null || incident.getReporter().getId().equals(reporterId))
                // Filtrar por workerId si se proporcionó
                .filter(incident -> workerId == null ||
                        (incident.getWorker() != null && incident.getWorker().getId().equals(workerId)))
                // Filtrar por departmentId si se proporcionó
                .filter(incident -> departmentId == null || incident.getDepartment().getId().equals(departmentId))
                // Convertir a DTO
                .map(this::convertToIncidentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar un incidente completo (PUT).
     *
     * ¿Qué hace este método?
     * - Busca el incidente existente por ID
     * - Actualiza los campos proporcionados (título, descripción, ubicación, foto)
     * - NO modifica status, prioridad, trabajador ni departamento (tienen endpoints específicos)
     * - Guarda los cambios en la base de datos
     *
     * @param id ID del incidente a actualizar
     * @param request Datos de actualización
     * @return IncidentResponse con el incidente actualizado
     * @throws RuntimeException si el incidente no existe
     *
     * Usado por: PUT /api/incidents/{id}
     */
    public IncidentResponse updateIncident(Integer id, UpdateIncidentRequest request) {
        // 1. Buscar el incidente existente
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado con ID: " + id));

        // 2. Actualizar solo los campos proporcionados (si no son null)
        if (request.getTitle() != null) {
            incident.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            incident.setDescription(request.getDescription());
        }
        if (request.getLocationDescription() != null) {
            incident.setLocationDescription(request.getLocationDescription());
        }
        if (request.getPhotoUrl() != null) {
            incident.setPhotoUrl(request.getPhotoUrl());
        }

        // 3. Guardar los cambios (updatedAt se actualiza automáticamente con @PreUpdate)
        Incident updatedIncident = incidentRepository.save(incident);

        // 4. Convertir a DTO y retornar
        return convertToIncidentResponse(updatedIncident);
    }

    /**
     * Eliminar un incidente.
     *
     * ¿Qué hace este método?
     * - Verifica que el incidente existe
     * - Elimina el incidente de la base de datos
     * - Lanza excepción si el incidente no existe
     *
     * @param id ID del incidente a eliminar
     * @throws RuntimeException si el incidente no existe
     *
     * Usado por: DELETE /api/incidents/{id}
     */
    public void deleteIncident(Integer id) {
        // 1. Verificar que el incidente existe
        if (!incidentRepository.existsById(id)) {
            throw new RuntimeException("Incidente no encontrado con ID: " + id);
        }

        // 2. Eliminar el incidente
        incidentRepository.deleteById(id);
    }

    /**
     * Cambiar el estado de un incidente.
     *
     * ¿Qué hace este método?
     * - Busca el incidente por ID
     * - Actualiza el campo status con el nuevo estado
     * - Guarda los cambios en la base de datos
     *
     * @param id ID del incidente
     * @param request Nuevo estado
     * @return IncidentResponse con el incidente actualizado
     * @throws RuntimeException si el incidente no existe
     *
     * Usado por: PATCH /api/incidents/{id}/status
     */
    public IncidentResponse updateStatus(Integer id, UpdateStatusRequest request) {
        // 1. Buscar el incidente
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado con ID: " + id));

        // 2. Guardar el valor anterior para el historial
        String oldStatus = incident.getStatus().toString();

        // 3. Actualizar el status
        incident.setStatus(request.getStatus());

        // 4. Guardar
        Incident updatedIncident = incidentRepository.save(incident);

        // 5. Registrar cambio en el historial
        // TODO: Obtener userId desde el token JWT en lugar de hardcodear
        recordHistory(updatedIncident, "STATUS_CHANGE", oldStatus, request.getStatus().toString(), 3);

        // 6. Retornar
        return convertToIncidentResponse(updatedIncident);
    }

    /**
     * Asignar un trabajador a un incidente.
     *
     * ¿Qué hace este método?
     * - Busca el incidente por ID
     * - Busca el trabajador (usuario) por ID
     * - Valida que el trabajador existe
     * - Asigna el trabajador al incidente
     * - Opcionalmente cambia el status a ASSIGNED
     *
     * @param id ID del incidente
     * @param request ID del trabajador a asignar
     * @return IncidentResponse con el incidente actualizado
     * @throws RuntimeException si el incidente o el trabajador no existen
     *
     * Usado por: PATCH /api/incidents/{id}/assign
     */
    public IncidentResponse assignWorker(Integer id, AssignWorkerRequest request) {
        // 1. Buscar el incidente
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado con ID: " + id));

        // 2. Buscar el trabajador por ID
        User worker = userRepository.findById(request.getWorkerId())
                .orElseThrow(() -> new RuntimeException("Trabajador no encontrado con ID: " + request.getWorkerId()));

        // 3. Guardar el valor anterior para el historial
        String oldWorker = incident.getWorker() != null ? incident.getWorker().getFullName() : null;

        // 4. Asignar el trabajador al incidente
        incident.setWorker(worker);

        // 5. Opcional: Cambiar el status a ASSIGNED automáticamente
        incident.setStatus(IncidentStatus.ASSIGNED);

        // 6. Guardar
        Incident updatedIncident = incidentRepository.save(incident);

        // 7. Registrar cambio en el historial
        // TODO: Obtener userId desde el token JWT en lugar de hardcodear
        recordHistory(updatedIncident, "WORKER_ASSIGNED", oldWorker, worker.getFullName(), 3);

        // 8. Retornar
        return convertToIncidentResponse(updatedIncident);
    }

    /**
     * Cambiar la prioridad de un incidente.
     *
     * ¿Qué hace este método?
     * - Busca el incidente por ID
     * - Actualiza el campo priority con la nueva prioridad
     * - Guarda los cambios en la base de datos
     *
     * @param id ID del incidente
     * @param request Nueva prioridad
     * @return IncidentResponse con el incidente actualizado
     * @throws RuntimeException si el incidente no existe
     *
     * Usado por: PATCH /api/incidents/{id}/priority
     */
    public IncidentResponse updatePriority(Integer id, UpdatePriorityRequest request) {
        // 1. Buscar el incidente
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado con ID: " + id));

        // 2. Guardar el valor anterior para el historial
        String oldPriority = incident.getPriority().toString();

        // 3. Actualizar la prioridad
        incident.setPriority(request.getPriority());

        // 4. Guardar
        Incident updatedIncident = incidentRepository.save(incident);

        // 5. Registrar cambio en el historial
        // TODO: Obtener userId desde el token JWT en lugar de hardcodear
        recordHistory(updatedIncident, "PRIORITY_CHANGE", oldPriority, request.getPriority().toString(), 3);

        // 6. Retornar
        return convertToIncidentResponse(updatedIncident);
    }

    /**
     * Derivar un incidente a otro departamento.
     *
     * ¿Qué hace este método?
     * - Busca el incidente por ID
     * - Busca el nuevo departamento por ID
     * - Cambia el departamento del incidente
     * - Desasigna el trabajador (worker = null) porque el nuevo departamento debe asignar uno de su equipo
     * - Opcionalmente cambia el status a PENDING_ASSIGNMENT
     *
     * @param id ID del incidente
     * @param request ID del nuevo departamento
     * @return IncidentResponse con el incidente actualizado
     * @throws RuntimeException si el incidente o el departamento no existen
     *
     * Usado por: PATCH /api/incidents/{id}/department
     */
    public IncidentResponse updateDepartment(Integer id, UpdateDepartmentRequest request) {
        // 1. Buscar el incidente
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incidente no encontrado con ID: " + id));

        // 2. Buscar el nuevo departamento
        Department newDepartment = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + request.getDepartmentId()));

        // 3. Guardar el valor anterior para el historial
        String oldDepartment = incident.getDepartment().getName();

        // 4. Cambiar el departamento
        incident.setDepartment(newDepartment);

        // 5. Desasignar el trabajador (el nuevo departamento debe asignar uno de su equipo)
        incident.setWorker(null);

        // 6. Cambiar el status a PENDING_ASSIGNMENT
        incident.setStatus(IncidentStatus.PENDING_ASSIGNMENT);

        // 7. Guardar
        Incident updatedIncident = incidentRepository.save(incident);

        // 8. Registrar cambio en el historial
        // TODO: Obtener userId desde el token JWT en lugar de hardcodear
        recordHistory(updatedIncident, "DEPARTMENT_CHANGE", oldDepartment, newDepartment.getName(), 3);

        // 9. Retornar
        return convertToIncidentResponse(updatedIncident);
    }

    /**
     * Método auxiliar: Convierte una entidad Incident a un DTO IncidentResponse.
     *
     * ¿Qué hace este método?
     * - Toma una entidad Incident (con toda la información de BD)
     * - Crea un IncidentResponse DTO con información legible para el frontend
     * - Extrae información de las relaciones (reporter, worker, department)
     * - Maneja casos donde worker puede ser null (incidente sin asignar)
     *
     * @param incident Entidad Incident de la base de datos
     * @return IncidentResponse DTO para enviar al frontend
     */
    private IncidentResponse convertToIncidentResponse(Incident incident) {
        IncidentResponse response = new IncidentResponse();

        // Copiar campos básicos del incidente
        response.setId(incident.getId());
        response.setTitle(incident.getTitle());
        response.setDescription(incident.getDescription());
        response.setLocationDescription(incident.getLocationDescription());
        response.setPhotoUrl(incident.getPhotoUrl());
        response.setStatus(incident.getStatus());
        response.setPriority(incident.getPriority());
        response.setCreatedAt(incident.getCreatedAt());
        response.setUpdatedAt(incident.getUpdatedAt());

        // Información del reporter (siempre existe)
        User reporter = incident.getReporter();
        response.setReporterId(reporter.getId());
        response.setReporterName(reporter.getFullName());
        response.setReporterEmail(reporter.getEmailUade());

        // Información del worker (puede ser null si no está asignado)
        User worker = incident.getWorker();
        if (worker != null) {
            response.setWorkerId(worker.getId());
            response.setWorkerName(worker.getFullName());
        }

        // Información del departamento (siempre existe)
        Department department = incident.getDepartment();
        response.setDepartmentId(department.getId());
        response.setDepartmentName(department.getName());

        return response;
    }

    /**
     * Obtener el historial de cambios de un incidente.
     *
     * ¿Qué hace este método?
     * - Busca todos los cambios registrados de un incidente específico
     * - Los ordena por fecha descendente (más recientes primero)
     * - Convierte cada IncidentHistory a IncidentHistoryResponse
     *
     * @param incidentId ID del incidente
     * @return Lista de IncidentHistoryResponse con el historial de cambios
     * @throws RuntimeException si el incidente no existe
     *
     * Usado por: GET /api/incidents/{id}/history
     */
    public List<IncidentHistoryResponse> getIncidentHistory(Integer incidentId) {
        // 1. Verificar que el incidente existe
        if (!incidentRepository.existsById(incidentId)) {
            throw new RuntimeException("Incidente no encontrado con ID: " + incidentId);
        }

        // 2. Obtener el historial ordenado por fecha descendente
        List<IncidentHistory> history = incidentHistoryRepository.findByIncidentIdOrderByChangedAtDesc(incidentId);

        // 3. Convertir a DTO
        return history.stream()
                .map(this::convertToIncidentHistoryResponse)
                .collect(Collectors.toList());
    }

    /**
     * Método auxiliar: Registra un cambio en el historial de un incidente.
     *
     * ¿Qué hace este método?
     * - Crea un nuevo registro de IncidentHistory
     * - Guarda el tipo de cambio, valores anterior y nuevo, y quién lo cambió
     * - Utilizado internamente por los métodos de actualización
     *
     * @param incident Incidente modificado
     * @param changeType Tipo de cambio (STATUS_CHANGE, WORKER_ASSIGNED, etc.)
     * @param oldValue Valor anterior (puede ser null)
     * @param newValue Valor nuevo
     * @param changedByUserId ID del usuario que realizó el cambio (TODO: obtener del token JWT)
     */
    private void recordHistory(Incident incident, String changeType, String oldValue, String newValue, Integer changedByUserId) {
        // Crear registro de historial
        IncidentHistory historyEntry = new IncidentHistory();
        historyEntry.setIncident(incident);
        historyEntry.setChangeType(changeType);
        historyEntry.setOldValue(oldValue);
        historyEntry.setNewValue(newValue);

        // TODO: Obtener changedByUserId desde el token JWT en lugar de recibirlo como parámetro
        if (changedByUserId != null) {
            User changedBy = userRepository.findById(changedByUserId).orElse(null);
            historyEntry.setChangedBy(changedBy);
        }

        // Guardar en la base de datos
        incidentHistoryRepository.save(historyEntry);
    }

    /**
     * Método auxiliar: Convierte una entidad IncidentHistory a un DTO IncidentHistoryResponse.
     *
     * ¿Qué hace este método?
     * - Toma una entidad IncidentHistory
     * - Crea un IncidentHistoryResponse DTO para enviar al frontend
     * - Extrae información del usuario que realizó el cambio
     *
     * @param history Entidad IncidentHistory de la base de datos
     * @return IncidentHistoryResponse DTO para enviar al frontend
     */
    private IncidentHistoryResponse convertToIncidentHistoryResponse(IncidentHistory history) {
        IncidentHistoryResponse response = new IncidentHistoryResponse();

        // Copiar campos básicos
        response.setId(history.getId());
        response.setChangeType(history.getChangeType());
        response.setOldValue(history.getOldValue());
        response.setNewValue(history.getNewValue());
        response.setChangedAt(history.getChangedAt());

        // Información del usuario que realizó el cambio (puede ser null)
        User changedBy = history.getChangedBy();
        if (changedBy != null) {
            response.setChangedByUserId(changedBy.getId());
            response.setChangedByUserName(changedBy.getFullName());
        }

        return response;
    }
}
