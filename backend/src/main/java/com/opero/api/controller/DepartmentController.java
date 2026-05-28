package com.opero.api.controller;

import com.opero.api.dto.DepartmentResponse;
import com.opero.api.dto.DepartmentUpdateRequest;
import com.opero.api.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller (Controlador) de Gestión de Departamentos.
 *
 * ¿Qué hace este Controller?
 * - Maneja todos los endpoints relacionados con departamentos
 * - Permite listar, consultar y actualizar departamentos
 * - Delega la lógica de negocio al DepartmentService
 *
 * Endpoints disponibles:
 * - GET /api/departments - Listar todos los departamentos
 * - GET /api/departments/{id} - Obtener departamento específico
 * - PUT /api/departments/{id} - Actualizar departamento (nombre y/o manager)
 */
@RestController
@RequestMapping("/api/departments")
@Tag(name = "Department Management", description = "Endpoints para gestión de departamentos")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * Listar todos los departamentos.
     *
     * ¿Qué hace este endpoint?
     * - Retorna todos los departamentos del sistema
     * - Incluye información del gerente (manager) de cada departamento
     * - Útil para mostrar listados de departamentos en el frontend
     *
     * Usado por:
     * - Selector de departamentos al crear/editar usuarios
     * - Selector de departamentos al reportar incidentes
     * - Vista de administración de departamentos
     *
     * @return Lista de DepartmentResponse
     */
    @GetMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Listar todos los departamentos",
            description = "Retorna todos los departamentos del sistema con información del gerente asignado."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo MANAGER"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Obtener un departamento específico por ID.
     *
     * ¿Qué hace este endpoint?
     * - Busca un departamento por su ID
     * - Retorna toda su información incluyendo el gerente
     * - Lanza 404 si el departamento no existe
     *
     * Usado por:
     * - Vista de detalle de departamento
     * - Consulta de información específica de un departamento
     *
     * @param id ID del departamento a buscar
     * @return DepartmentResponse con la información del departamento
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Obtener departamento por ID",
            description = "Retorna la información completa de un departamento específico por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento encontrado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo MANAGER"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DepartmentResponse> getDepartmentById(
            @Parameter(description = "ID del departamento a buscar", required = true)
            @PathVariable Integer id
    ) {
        DepartmentResponse department = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(department);
    }

    /**
     * Actualizar un departamento.
     *
     * ¿Qué hace este endpoint?
     * - Permite actualizar el nombre del departamento
     * - Permite cambiar el gerente (manager) del departamento
     * - Todos los campos son opcionales (actualización parcial)
     * - Solo MANAGER puede usar este endpoint
     *
     * Usado por:
     * - Pantalla de administración de departamentos
     * - Reasignación de gerentes
     *
     * @param id ID del departamento a actualizar
     * @param request Datos de actualización
     * @return DepartmentResponse con el departamento actualizado
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Actualizar departamento",
            description = "Permite actualizar el nombre y/o gerente de un departamento. " +
                    "Todos los campos son opcionales."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ej: manager no existe)"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo MANAGER"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DepartmentResponse> updateDepartment(
            @Parameter(description = "ID del departamento a actualizar", required = true)
            @PathVariable Integer id,
            @RequestBody DepartmentUpdateRequest request
    ) {
        DepartmentResponse updatedDepartment = departmentService.updateDepartment(id, request);
        return ResponseEntity.ok(updatedDepartment);
    }
}
