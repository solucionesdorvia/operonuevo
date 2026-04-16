package com.opero.api.controller;

import com.opero.api.dto.AuthResponse;
import com.opero.api.dto.LoginRequest;
import com.opero.api.dto.RegisterRequest;
import com.opero.api.dto.UserResponse;
import com.opero.api.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller (Controlador) de Autenticación.
 *
 * ¿Qué hace este Controller?
 * - Expone los endpoints REST relacionados con autenticación
 * - Recibe las peticiones HTTP del frontend (app móvil)
 * - Delega la lógica de negocio al AuthService
 * - Retorna respuestas HTTP con códigos de estado apropiados
 * - Documenta automáticamente la API con Swagger
 *
 * Endpoints expuestos:
 * - POST /api/auth/login: Iniciar sesión
 * - POST /api/auth/register: Registrar nuevo usuario
 * - GET /api/auth/me: Obtener datos del usuario autenticado
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "APIs para login, registro y gestión de sesión de usuarios")
public class AuthController {

    // Inyección del servicio de autenticación
    @Autowired
    private AuthService authService;

    /**
     * POST /api/auth/login - Iniciar sesión
     *
     * ¿Qué hace este endpoint?
     * - Recibe las credenciales del usuario (email y password)
     * - Valida las credenciales contra la base de datos
     * - Si son correctas, devuelve un token y los datos del usuario
     * - Si son incorrectas, devuelve un error 401 Unauthorized
     *
     * @param request LoginRequest con emailUade y password
     * @return AuthResponse con token, datos del usuario y mensaje
     */
    @PostMapping("/login")
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica a un usuario existente con su email institucional y contraseña. " +
                      "Si las credenciales son correctas, retorna un token de autenticación (simulado por ahora) " +
                      "y la información del usuario. El token debe ser guardado en el frontend para futuras peticiones."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Login exitoso - Credenciales válidas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Email o password faltantes",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Credenciales incorrectas - Email no existe o password incorrecto",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> login(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Credenciales de inicio de sesión (email institucional y contraseña)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = LoginRequest.class)
            )
        )
        @RequestBody LoginRequest request
    ) {
        try {
            // Delegar la lógica al servicio
            AuthResponse response = authService.login(request);
            // Retornar respuesta exitosa con código 200 OK
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Si falla, retornar error 401 Unauthorized con el mensaje de error
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * POST /api/auth/register - Registrar nuevo usuario
     *
     * ¿Qué hace este endpoint?
     * - Recibe los datos del nuevo usuario (nombre, email, password, rol, departamento)
     * - Valida que el email no esté ya registrado
     * - Valida que el rol y departamento existan
     * - Crea el nuevo usuario en la base de datos
     * - Retorna un token y los datos del usuario creado
     *
     * @param request RegisterRequest con todos los datos del nuevo usuario
     * @return AuthResponse con token, datos del usuario y mensaje
     */
    @PostMapping("/register")
    @Operation(
        summary = "Registrar nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema. " +
                      "Requiere nombre completo, email institucional (debe ser único), contraseña, rol y opcionalmente departamento. " +
                      "Los roles son: 1=STUDENT, 2=PROFESSOR, 3=MANAGER, 4=WORKER. " +
                      "El departmentId es obligatorio solo para MANAGER y WORKER."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Registro exitoso - Usuario creado correctamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Datos inválidos - Email duplicado, rol inexistente, o departamento inexistente",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> register(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del nuevo usuario a registrar (nombre, email, password, roleId, departmentId opcional)",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = RegisterRequest.class)
            )
        )
        @RequestBody RegisterRequest request
    ) {
        try {
            // Delegar la lógica al servicio
            AuthResponse response = authService.register(request);
            // Retornar respuesta exitosa con código 200 OK
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Si falla, retornar error 400 Bad Request con el mensaje de error
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * GET /api/auth/me - Obtener datos del usuario autenticado
     *
     * ¿Qué hace este endpoint?
     * - Recibe el email del usuario autenticado como parámetro de query
     * - Busca los datos del usuario en la base de datos
     * - Retorna la información del usuario sin datos sensibles (sin password)
     *
     * Nota: En una implementación real con JWT, el email vendría del token
     * decodificado en lugar de un parámetro de query.
     *
     * @param emailUade Email del usuario autenticado (query parameter)
     * @return UserResponse con los datos del usuario
     */
    @GetMapping("/me")
    @Operation(
        summary = "Obtener datos del usuario autenticado",
        description = "Retorna la información del usuario actualmente autenticado. " +
                      "Por ahora recibe el email como parámetro de query. " +
                      "En una implementación real con JWT, el email se extraería del token en el header Authorization."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Usuario encontrado - Datos retornados exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token inválido o expirado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Usuario no encontrado",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500",
            description = "Error interno del servidor",
            content = @Content
        )
    })
    public ResponseEntity<?> me(
        @io.swagger.v3.oas.annotations.Parameter(
            description = "Email institucional del usuario autenticado (ej: juan.perez@uade.edu.ar)",
            required = true,
            example = "juan.perez@uade.edu.ar"
        )
        @RequestParam String emailUade
    ) {
        try {
            // Delegar la lógica al servicio
            UserResponse response = authService.me(emailUade);
            // Retornar respuesta exitosa con código 200 OK
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Si falla, retornar error 404 Not Found con el mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
