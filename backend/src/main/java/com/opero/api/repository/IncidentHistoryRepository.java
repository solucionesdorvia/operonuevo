package com.opero.api.repository;

import com.opero.api.entity.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository (Repositorio) para IncidentHistory.
 *
 * ¿Qué hace este Repository?
 * - Permite acceder a los registros de historial de incidentes en la base de datos
 * - Proporciona métodos para consultar el historial de un incidente específico
 * - Extiende JpaRepository para heredar operaciones CRUD básicas
 *
 * Métodos personalizados:
 * - findByIncidentIdOrderByChangedAtDesc: Obtiene el historial de un incidente ordenado por fecha (más reciente primero)
 */
@Repository
public interface IncidentHistoryRepository extends JpaRepository<IncidentHistory, Integer> {

    /**
     * Obtener el historial de cambios de un incidente específico.
     *
     * ¿Qué hace este método?
     * - Busca todos los registros de historial de un incidente
     * - Los ordena por fecha descendente (más recientes primero)
     * - Retorna una lista vacía si el incidente no tiene historial
     *
     * @param incidentId ID del incidente
     * @return Lista de IncidentHistory ordenada por fecha (más reciente primero)
     *
     * Usado por: GET /api/incidents/{id}/history
     */
    List<IncidentHistory> findByIncidentIdOrderByChangedAtDesc(Integer incidentId);
}
