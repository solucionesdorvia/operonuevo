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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
 * - POST /api/auth/logout: Cerrar sesión
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
                      "Los roles son: 1=USER, 2=MANAGER, 3=WORKER. " +
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
     * - Extrae el email del usuario desde el token JWT automáticamente
     * - Busca los datos del usuario en la base de datos
     * - Retorna la información del usuario sin datos sensibles (sin password)
     *
     * Nota: El usuario se extrae del contexto de Spring Security que fue
     * configurado por el JwtAuthenticationFilter. Ya no se requiere pasar
     * el email como parámetro.
     *
     * Header requerido: Authorization: Bearer <token-jwt>
     *
     * @return UserResponse con los datos del usuario
     */
    @GetMapping("/me")
    @Operation(
        summary = "Obtener datos del usuario autenticado",
        description = "Retorna la información del usuario actualmente autenticado. " +
                      "El email se extrae automáticamente del token JWT en el header Authorization. " +
                      "Header requerido: Authorization: Bearer <token-jwt>"
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
    public ResponseEntity<?> me() {
        try {
            // Obtener el usuario autenticado desde el contexto de Spring Security
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailUade = authentication.getName(); // El "name" es el email (username)

            // Delegar la lógica al servicio
            UserResponse response = authService.me(emailUade);
            // Retornar respuesta exitosa con código 200 OK
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Si falla, retornar error 404 Not Found con el mensaje de error
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * POST /api/auth/logout - Cerrar sesión
     *
     * ¿Qué hace este endpoint?
     * - Invalida la sesión actual del usuario
     * - Con JWT, el logout es principalmente del lado del cliente
     * - El cliente debe eliminar el token almacenado localmente
     * - Este endpoint retorna confirmación de logout exitoso
     *
     * ¿Cómo funciona el logout con JWT?
     * - JWT es stateless (sin estado en el servidor)
     * - El servidor NO guarda una lista de sesiones activas
     * - Por lo tanto, el logout real ocurre cuando el cliente elimina el token
     * - Este endpoint sirve para:
     *   1. Confirmar al cliente que puede borrar el token
     *   2. (Opcional) Registrar el evento de logout para auditoría
     *   3. (Opcional) Agregar el token a una blacklist (implementación avanzada)
     *
     * Implementación avanzada (opcional):
     * - Mantener una blacklist de tokens en Redis o base de datos
     * - Modificar JwtAuthenticationFilter para verificar blacklist
     * - Útil para invalidar tokens antes de que expiren naturalmente
     *
     * @return Mensaje de confirmación de logout exitoso
     */
    @PostMapping("/logout")
    @Operation(
        summary = "Cerrar sesión (logout)",
        description = "Cierra la sesión del usuario autenticado. " +
                      "Con JWT, el logout es principalmente del lado del cliente: " +
                      "el cliente debe eliminar el token almacenado (localStorage, AsyncStorage, etc.). " +
                      "Este endpoint confirma que el logout fue procesado correctamente. " +
                      "El token seguirá siendo técnicamente válido hasta que expire, a menos que se implemente una blacklist."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Logout exitoso - El cliente debe eliminar el token almacenado",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = String.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "No autenticado - Token inválido o ya expirado",
            content = @Content
        )
    })
    public ResponseEntity<?> logout() {
        try {
            // Obtener el usuario autenticado para logging/auditoría
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String emailUade = authentication.getName();

            // TODO (Opcional): Implementar blacklist de tokens
            // 1. Obtener el token JWT del header Authorization
            // 2. Agregar el token a una blacklist en Redis o base de datos
            // 3. Modificar JwtAuthenticationFilter para verificar la blacklist
            // Ejemplo:
            // String token = extractTokenFromRequest(request);
            // tokenBlacklistService.addToBlacklist(token);

            // TODO (Opcional): Registrar evento de logout para auditoría
            // auditService.logLogout(emailUade);

            // Limpiar el contexto de seguridad (opcional, se limpia automáticamente al finalizar el request)
            SecurityContextHolder.clearContext();

            // Retornar confirmación de logout exitoso
            return ResponseEntity.ok()
                    .body(new java.util.HashMap<String, String>() {{
                        put("message", "Logout exitoso. Por favor, elimina el token del almacenamiento local.");
                        put("email", emailUade);
                    }});
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error al cerrar sesión: " + e.getMessage());
        }
    }
}
