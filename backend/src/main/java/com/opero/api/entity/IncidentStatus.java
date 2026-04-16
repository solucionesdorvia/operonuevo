package com.opero.api.entity;

/**
 * Enum de estados de un incidente.
 *
 * Estados disponibles:
 * - PENDING: Incidente reportado, esperando revisión
 * - PENDING_ASSIGNMENT: Incidente recibido por el departamento, esperando asignación de trabajador
 * - ASSIGNED: Incidente asignado a un trabajador específico
 * - IN_PROCESS: Trabajador comenzó el trabajo
 * - FINISHED: Trabajo completado
 */
public enum IncidentStatus {
    PENDING,
    PENDING_ASSIGNMENT,
    ASSIGNED,  // Cambiado de ASIGNADO a ASSIGNED (consistencia en inglés)
    IN_PROCESS,
    FINISHED
}