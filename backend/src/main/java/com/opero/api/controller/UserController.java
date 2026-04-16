package com.opero.api.controller;

import com.opero.api.dto.UpdateUserRequest;
import com.opero.api.dto.UserResponse;
import com.opero.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller (Controlador) de Gestión de Usuarios.
 *
 * ¿Qué hace este Controller?
 * - Maneja todos los endpoints relacionados con usuarios
 * - Permite a usuarios ver y actualizar su propio perfil
 * - Permite a administradores gestionar todos los usuarios
 * - Delega la lógica de negocio al UserService
 *
 * Endpoints disponibles:
 * - GET /api/users/me - Obtener perfil del usuario autenticado
 * - PUT /api/users/me - Actualizar perfil del usuario autenticado
 * - GET /api/users - Listar todos los usuarios (con filtro opcional por departamento)
 * - GET /api/users/{id} - Obtener usuario específico
 * - PUT /api/users/{id} - Actualizar cualquier usuario (admin)
 * - DELETE /api/users/{id} - Eliminar usuario (admin)
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints para gestión de usuarios")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Obtener el perfil del usuario autenticado.
     *
     * ¿Qué hace este endpoint?
     * - Retorna la información del usuario que está actualmente autenticado
     * - TODO: Obtener el userId desde el token JWT (por ahora se simula)
     * - No requiere parámetros porque usa la sesión del usuario
     *
     * Usado por: Perfil de usuario en el frontend
     *
     * @return UserResponse con la información del usuario autenticado
     */
    @GetMapping("/me")
    @Operation(
            summary = "Obtener perfil del usuario autenticado",
            description = "Retorna la información del usuario que está actualmente autenticado. " +
                    "TODO: El userId debería extraerse del token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido exitosamente"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponse> getMyProfile() {
        // TODO: Extraer userId del token JWT en lugar de hardcodear
        // Por ahora simulamos que el usuario autenticado es el ID 1
        Integer authenticatedUserId = 1;

        UserResponse user = userService.getUserById(authenticatedUserId);
        return ResponseEntity.ok(user);
    }

    /**
     * Actualizar el perfil del usuario autenticado.
     *
     * ¿Qué hace este endpoint?
     * - Permite al usuario actualizar su propio perfil
     * - Solo puede modificar: nombre, email, password
     * - NO puede modificar: roleId ni departmentId (solo admins)
     * - TODO: Validar que el usuario no intente cambiar roleId/departmentId
     *
     * Usado por: Edición de perfil en el frontend
     *
     * @param request Datos de actualización
     * @return UserResponse con el perfil actualizado
     */
    @PutMapping("/me")
    @Operation(
            summary = "Actualizar perfil del usuario autenticado",
            description = "Permite al usuario actualizar su propio perfil (nombre, email, password). " +
                    "Los usuarios normales NO pueden cambiar su rol o departamento."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ej: email duplicado)"),
            @ApiResponse(responseCode = "401", description = "No autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    public ResponseEntity<UserResponse> updateMyProfile(@RequestBody UpdateUserRequest request) {
        // TODO: Extraer userId del token JWT
        Integer authenticatedUserId = 1;

        // TODO: Validar que request no tenga roleId ni departmentId
        // Los usuarios normales no deberían poder cambiar estos campos

        UserResponse updatedUser = userService.updateUser(authenticatedUserId, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Listar todos los usuarios (con filtro opcional por departamento).
     *
     * ¿Qué hace este endpoint?
     * - Sin filtros: retorna todos los usuarios del sistema
     * - Con departmentId: retorna solo usuarios de ese departamento
     * - Útil para que gerentes vean su equipo de trabajo
     *
     * Usado por:
     * - Pantalla de administración de usuarios
     * - Vista de equipo para gerentes
     *
     * @param departmentId (Opcional) ID del departamento para filtrar
     * @return Lista de UserResponse
     */
    @GetMapping
    @Operation(
            summary = "Listar todos los usuarios",
            description = "Retorna todos los usuarios del sistema. " +
                    "Opcionalmente se puede filtrar por departamento usando el parámetro departmentId."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<UserResponse>> getAllUsers(
            @Parameter(description = "ID del departamento para filtrar usuarios (opcional)")
            @RequestParam(required = false) Integer departmentId
    ) {
        List<UserResponse> users;

        if (departmentId != null) {
            // Filtrar por departamento
            users = userService.getUsersByDepartment(departmentId);
        } else {
            // Obtener todos
            users = userService.getAllUsers();
        }

        return ResponseEntity.ok(users);
    }

    /**
     * Obtener un usuario específico por ID.
     *
     * ¿Qué hace este endpoint?
     * - Busca un usuario por su ID
     * - Retorna toda su información (excepto password)
     * - Lanza 404 si el usuario no existe
     *
     * Usado por:
     * - Vista de detalle de usuario
     * - Administración de usuarios
     *
     * @param id ID del usuario a buscar
     * @return UserResponse con la información del usuario
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Retorna la información completa de un usuario específico por su ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UserResponse> getUserById(
            @Parameter(description = "ID del usuario a buscar", required = true)
            @PathVariable Integer id
    ) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Actualizar cualquier usuario (solo administradores).
     *
     * ¿Qué hace este endpoint?
     * - Permite actualizar cualquier campo de cualquier usuario
     * - Incluye roleId y departmentId (privilegios de admin)
     * - Todos los campos son opcionales (actualización parcial)
     * - TODO: Validar que solo administradores puedan usar este endpoint
     *
     * Usado por: Pantalla de administración de usuarios
     *
     * @param id ID del usuario a actualizar
     * @param request Datos de actualización
     * @return UserResponse con el usuario actualizado
     */
    @PutMapping("/{id}")
    @Operation(
            summary = "Actualizar cualquier usuario (admin)",
            description = "Permite a administradores actualizar cualquier campo de cualquier usuario, " +
                    "incluyendo rol y departamento. Todos los campos son opcionales."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos (ej: email duplicado, rol/departamento no existe)"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID del usuario a actualizar", required = true)
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request
    ) {
        // TODO: Validar que el usuario autenticado sea administrador
        UserResponse updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Eliminar un usuario (solo administradores).
     *
     * ¿Qué hace este endpoint?
     * - Elimina un usuario del sistema
     * - TODO: Validar que solo administradores puedan usar este endpoint
     * - TODO: Considerar implementar soft delete en lugar de eliminación física
     * - TODO: Validar que el usuario no tenga incidentes asociados
     *
     * Usado por: Pantalla de administración de usuarios
     *
     * @param id ID del usuario a eliminar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "Eliminar usuario (admin)",
            description = "Elimina un usuario del sistema. Solo administradores pueden usar este endpoint. " +
                    "ADVERTENCIA: Esta es una eliminación física. Considere implementar soft delete."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID del usuario a eliminar", required = true)
            @PathVariable Integer id
    ) {
        // TODO: Validar que el usuario autenticado sea administrador
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
