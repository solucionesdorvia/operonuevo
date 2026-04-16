package com.opero.api.service;

import com.opero.api.dto.UpdateUserRequest;
import com.opero.api.dto.UserResponse;
import com.opero.api.entity.Department;
import com.opero.api.entity.Role;
import com.opero.api.entity.User;
import com.opero.api.repository.DepartmentRepository;
import com.opero.api.repository.RoleRepository;
import com.opero.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service (Servicio) de Gestión de Usuarios.
 *
 * ¿Qué hace este Service?
 * - Contiene toda la lógica de negocio relacionada con usuarios
 * - Maneja consultas, actualizaciones y eliminación de usuarios
 * - Convierte entidades User a DTOs UserResponse
 * - Valida datos antes de realizar operaciones
 */
@Service
public class UserService {

    // Inyección de dependencias de los repositories
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    /**
     * Obtener un usuario por su ID.
     *
     * ¿Qué hace este método?
     * - Busca un usuario específico en la base de datos por su ID
     * - Convierte la entidad User a UserResponse DTO
     * - Lanza excepción si el usuario no existe
     *
     * @param id ID del usuario a buscar
     * @return UserResponse con la información del usuario
     * @throws RuntimeException si el usuario no existe
     *
     * Usado por: GET /api/users/{id}
     */
    public UserResponse getUserById(Integer id) {
        // Buscar usuario por ID
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Convertir a DTO y retornar
        return convertToUserResponse(user);
    }

    /**
     * Obtener todos los usuarios del sistema.
     *
     * ¿Qué hace este método?
     * - Obtiene todos los usuarios de la base de datos
     * - Convierte cada User a UserResponse
     * - Retorna una lista completa de usuarios
     *
     * @return Lista de UserResponse con todos los usuarios
     *
     * Usado por: GET /api/users
     */
    public List<UserResponse> getAllUsers() {
        // Obtener todos los usuarios
        List<User> users = userRepository.findAll();

        // Convertir cada User a UserResponse usando streams
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Obtener usuarios filtrados por departamento.
     *
     * ¿Qué hace este método?
     * - Busca todos los usuarios que pertenecen a un departamento específico
     * - Útil para que gerentes vean su equipo de trabajo
     * - Convierte cada User a UserResponse
     *
     * @param departmentId ID del departamento
     * @return Lista de UserResponse con los usuarios del departamento
     *
     * Usado por: GET /api/users?departmentId={DEPARTMENT_ID}
     *
     * Nota: Este método filtra por departamento. Los usuarios sin departamento (alumnos/profesores)
     * no aparecerán en este filtro.
     */
    public List<UserResponse> getUsersByDepartment(Integer departmentId) {
        // Obtener todos los usuarios
        List<User> allUsers = userRepository.findAll();

        // Filtrar por departamento y convertir a DTO
        return allUsers.stream()
                .filter(user -> user.getDepartment() != null &&
                               user.getDepartment().getId().equals(departmentId))
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Actualizar un usuario existente.
     *
     * ¿Qué hace este método?
     * - Busca el usuario existente por ID
     * - Actualiza los campos proporcionados (todos opcionales)
     * - Valida que el nuevo email no esté duplicado (si se cambia)
     * - Valida que el nuevo rol y departamento existan (si se cambian)
     * - Guarda los cambios en la base de datos
     *
     * @param id ID del usuario a actualizar
     * @param request Datos de actualización
     * @return UserResponse con el usuario actualizado
     * @throws RuntimeException si el usuario, rol o departamento no existen, o si el email está duplicado
     *
     * Usado por:
     * - PUT /api/users/me (usuario actualiza su propio perfil)
     * - PUT /api/users/{id} (administrador actualiza cualquier usuario)
     *
     * Nota sobre seguridad:
     * - El controller debe validar que un usuario normal solo pueda actualizar su propio perfil
     * - Solo administradores deberían poder cambiar roleId y departmentId de otros usuarios
     */
    public UserResponse updateUser(Integer id, UpdateUserRequest request) {
        // 1. Buscar el usuario existente
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. Actualizar nombre si se proporcionó
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // 3. Actualizar email si se proporcionó (validar que no esté duplicado)
        if (request.getEmailUade() != null && !request.getEmailUade().equals(user.getEmailUade())) {
            // Verificar que el nuevo email no esté en uso
            if (userRepository.existsByEmailUade(request.getEmailUade())) {
                throw new RuntimeException("El email ya está registrado");
            }
            user.setEmailUade(request.getEmailUade());
        }

        // 4. Actualizar password si se proporcionó (TODO: hashear con BCrypt)
        if (request.getPassword() != null) {
            user.setPasswordHash(request.getPassword()); // Por ahora texto plano
        }

        // 5. Actualizar rol si se proporcionó
        if (request.getRoleId() != null) {
            Role newRole = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + request.getRoleId()));
            user.setRole(newRole);
        }

        // 6. Actualizar departamento si se proporcionó
        if (request.getDepartmentId() != null) {
            Department newDepartment = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + request.getDepartmentId()));
            user.setDepartment(newDepartment);
        }

        // 7. Guardar los cambios (updatedAt se actualiza automáticamente con @PreUpdate)
        User updatedUser = userRepository.save(user);

        // 8. Convertir a DTO y retornar
        return convertToUserResponse(updatedUser);
    }

    /**
     * Eliminar un usuario del sistema.
     *
     * ¿Qué hace este método?
     * - Verifica que el usuario existe
     * - Valida que el usuario no tenga incidentes asociados (reportados o asignados)
     * - Elimina el usuario de la base de datos
     * - Lanza excepción si el usuario no existe o tiene incidentes asociados
     *
     * @param id ID del usuario a eliminar
     * @throws RuntimeException si el usuario no existe o tiene incidentes asociados
     *
     * Usado por: DELETE /api/users/{id}
     *
     * Nota sobre seguridad:
     * - Solo administradores deberían poder eliminar usuarios
     * - Considerar implementar "soft delete" (marcar como inactivo en lugar de eliminar)
     *
     * Validaciones implementadas:
     * - No se puede eliminar un usuario que reportó incidentes
     * - No se puede eliminar un usuario que tiene incidentes asignados
     */
    public void deleteUser(Integer id) {
        // 1. Buscar el usuario (esto también verifica que existe)
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // 2. Validar que el usuario no tenga incidentes reportados
        if (!user.getReportedIncidents().isEmpty()) {
            throw new RuntimeException(
                    "No se puede eliminar el usuario porque tiene " +
                    user.getReportedIncidents().size() +
                    " incidente(s) reportado(s). Primero debe reasignar o eliminar esos incidentes."
            );
        }

        // 3. Validar que el usuario no tenga incidentes asignados
        if (!user.getAssignedIncidents().isEmpty()) {
            throw new RuntimeException(
                    "No se puede eliminar el usuario porque tiene " +
                    user.getAssignedIncidents().size() +
                    " incidente(s) asignado(s). Primero debe reasignar esos incidentes a otro trabajador."
            );
        }

        // 4. Si pasa todas las validaciones, eliminar el usuario
        userRepository.deleteById(id);
    }

    /**
     * Método auxiliar: Convierte una entidad User a un DTO UserResponse.
     *
     * ¿Qué hace este método?
     * - Toma una entidad User (con toda la información de BD)
     * - Crea un UserResponse DTO (sin información sensible como password)
     * - Copia solo los campos necesarios para enviar al frontend
     * - Extrae información del rol y departamento
     *
     * @param user Entidad User de la base de datos
     * @return UserResponse DTO para enviar al frontend
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();

        // Copiar campos básicos
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmailUade(user.getEmailUade());
        response.setCreatedAt(user.getCreatedAt());

        // Información del rol (siempre existe)
        response.setRoleId(user.getRole().getId());
        response.setRoleName(user.getRole().getRoleName());

        // Información del departamento (puede ser null para alumnos/profesores)
        if (user.getDepartment() != null) {
            response.setDepartmentId(user.getDepartment().getId());
            response.setDepartmentName(user.getDepartment().getName());
        }

        return response;
    }
}
