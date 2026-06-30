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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @Operation(
            summary = "Listar todos los departamentos",
            description = "Retorna todos los departamentos del sistema. Endpoint público para registro e incidentes."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<DepartmentResponse>> getAllDepartments() {
        List<DepartmentResponse> departments = departmentService.getAllDepartments();
        return ResponseEntity.ok(departments);
    }

    /**
     * Crear un nuevo departamento.
     *
     * Solo MANAGER puede usar este endpoint. Recibe { name, managerId? } y
     * crea el departamento. Si se pasa managerId, valida que el usuario exista
     * y lo asigna como manager.
     *
     * Usado por: la pantalla "Departamentos" del rol Manager.
     */
    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Crear un nuevo departamento",
            description = "Crea un departamento con nombre (obligatorio) y manager (opcional). " +
                    "Solo MANAGER puede llamar este endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento creado"),
            @ApiResponse(responseCode = "400", description = "Datos invalidos"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo MANAGER"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<DepartmentResponse> createDepartment(@RequestBody DepartmentUpdateRequest request) {
        DepartmentResponse created = departmentService.createDepartment(request);
        return ResponseEntity.ok(created);
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
    @Operation(
            summary = "Obtener departamento por ID",
            description = "Retorna la información completa de un departamento. Endpoint público."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento encontrado"),
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

    /**
     * Eliminar un departamento.
     *
     * ¿Qué hace este endpoint?
     * - Elimina un departamento por su ID
     * - Solo MANAGER puede usar este endpoint
     * - Valida que no haya incidencias asociadas al departamento
     *
     * @param id ID del departamento a eliminar
     * @return Mensaje de confirmación
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @Operation(
            summary = "Eliminar departamento",
            description = "Elimina un departamento del sistema. Solo MANAGER puede usar este endpoint."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Departamento eliminado exitosamente"),
            @ApiResponse(responseCode = "400", description = "El departamento tiene incidencias asociadas"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Solo MANAGER"),
            @ApiResponse(responseCode = "404", description = "Departamento no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> deleteDepartment(
            @Parameter(description = "ID del departamento a eliminar", required = true)
            @PathVariable Integer id
    ) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().body(new java.util.HashMap<String, String>() {{
            put("message", "Departamento eliminado exitosamente");
        }});
    }
}
