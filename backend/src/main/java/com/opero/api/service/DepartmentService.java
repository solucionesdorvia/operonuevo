package com.opero.api.service;

import com.opero.api.dto.DepartmentResponse;
import com.opero.api.dto.DepartmentUpdateRequest;
import com.opero.api.entity.Department;
import com.opero.api.entity.User;
import com.opero.api.repository.DepartmentRepository;
import com.opero.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service (Servicio) de Gestión de Departamentos.
 *
 * ¿Qué hace este Service?
 * - Contiene toda la lógica de negocio relacionada con departamentos
 * - Maneja consultas y actualizaciones de departamentos
 * - Convierte entidades Department a DTOs DepartmentResponse
 * - Valida datos antes de realizar operaciones
 */
@Service
public class DepartmentService {

    // Inyección de dependencias de los repositories
    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Obtener todos los departamentos del sistema.
     *
     * ¿Qué hace este método?
     * - Obtiene todos los departamentos de la base de datos
     * - Convierte cada Department a DepartmentResponse
     * - Retorna una lista completa de departamentos con información del manager
     *
     * @return Lista de DepartmentResponse con todos los departamentos
     *
     * Usado por: GET /api/departments
     */
    public List<DepartmentResponse> getAllDepartments() {
        // Obtener todos los departamentos
        List<Department> departments = departmentRepository.findAll();

        // Convertir cada Department a DepartmentResponse usando streams
        return departments.stream()
                .map(this::convertToDepartmentResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener un departamento por su ID.
     *
     * ¿Qué hace este método?
     * - Busca un departamento específico en la base de datos por su ID
     * - Convierte la entidad Department a DepartmentResponse DTO
     * - Lanza excepción si el departamento no existe
     *
     * @param id ID del departamento a buscar
     * @return DepartmentResponse con la información del departamento
     * @throws RuntimeException si el departamento no existe
     *
     * Usado por: GET /api/departments/{id}
     */
    public DepartmentResponse getDepartmentById(Integer id) {
        // Buscar departamento por ID
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + id));

        // Convertir a DTO y retornar
        return convertToDepartmentResponse(department);
    }

    /**
     * Actualizar un departamento existente.
     *
     * ¿Qué hace este método?
     * - Busca el departamento existente por ID
     * - Actualiza los campos proporcionados (todos opcionales):
     *   - name: nuevo nombre del departamento
     *   - managerId: nuevo gerente del departamento
     * - Valida que el nuevo gerente exista (si se cambia)
     * - Guarda los cambios en la base de datos
     *
     * @param id ID del departamento a actualizar
     * @param request Datos de actualización
     * @return DepartmentResponse con el departamento actualizado
     * @throws RuntimeException si el departamento o el manager no existen
     *
     * Usado por: PUT /api/departments/{id}
     *
     * Nota sobre seguridad:
     * - Solo administradores o gerentes deberían poder actualizar departamentos
     */
    public DepartmentResponse updateDepartment(Integer id, DepartmentUpdateRequest request) {
        // 1. Buscar el departamento existente
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + id));

        // 2. Actualizar nombre si se proporcionó
        if (request.getName() != null) {
            department.setName(request.getName());
        }

        // 3. Actualizar manager si se proporcionó
        if (request.getManagerId() != null) {
            User newManager = userRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + request.getManagerId()));

            // TODO: Validar que el usuario tenga rol MANAGER
            department.setManager(newManager);
        }

        // 4. Guardar los cambios
        Department updatedDepartment = departmentRepository.save(department);

        // 5. Convertir a DTO y retornar
        return convertToDepartmentResponse(updatedDepartment);
    }

    /**
     * Método auxiliar: Convierte una entidad Department a un DTO DepartmentResponse.
     *
     * ¿Qué hace este método?
     * - Toma una entidad Department (con toda la información de BD)
     * - Crea un DepartmentResponse DTO para enviar al frontend
     * - Copia los campos necesarios
     * - Extrae información del manager si existe
     *
     * @param department Entidad Department de la base de datos
     * @return DepartmentResponse DTO para enviar al frontend
     */
    private DepartmentResponse convertToDepartmentResponse(Department department) {
        DepartmentResponse response = new DepartmentResponse();

        // Copiar campos básicos
        response.setId(department.getId());
        response.setName(department.getName());

        // Información del manager (puede ser null)
        User manager = department.getManager();
        if (manager != null) {
            response.setManagerId(manager.getId());
            response.setManagerName(manager.getFullName());
            response.setManagerEmail(manager.getEmailUade());
        }

        return response;
    }
}
