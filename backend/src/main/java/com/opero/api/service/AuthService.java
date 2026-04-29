package com.opero.api.service;

import com.opero.api.dto.AuthResponse;
import com.opero.api.dto.LoginRequest;
import com.opero.api.dto.RegisterRequest;
import com.opero.api.dto.UserResponse;
import com.opero.api.entity.Department;
import com.opero.api.entity.Role;
import com.opero.api.entity.User;
import com.opero.api.repository.DepartmentRepository;
import com.opero.api.repository.RoleRepository;
import com.opero.api.repository.UserRepository;
import com.opero.api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service (Servicio) de Autenticación.
 *
 * ¿Qué hace este Service?
 * - Contiene toda la lógica de negocio relacionada con autenticación
 * - Maneja login, registro y obtención de usuario autenticado
 * - Valida datos, hashea passwords, convierte entidades a DTOs
 * - Interactúa con los Repositories para acceder a la base de datos
 *
 * Seguridad implementada:
 * - BCrypt: Las contraseñas se hashean con BCrypt antes de guardarlas
 * - JWT: Se generan tokens JWT reales para autenticación
 * - Validación: Se comparan hashes en lugar de texto plano
 */
@Service
public class AuthService {

    // Inyección de dependencias de los repositories
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // Inyección de utilidades de seguridad
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Login: Autentica un usuario existente.
     *
     * ¿Qué hace este método?
     * - Busca el usuario por email en la base de datos
     * - Verifica que la contraseña coincida usando BCrypt
     * - Si es correcto, devuelve un token JWT real y los datos del usuario
     * - Si falla, lanza una excepción con mensaje de error
     *
     * @param request Datos de login (email y password)
     * @return AuthResponse con token JWT, datos del usuario y mensaje
     * @throws RuntimeException si el email no existe o la contraseña es incorrecta
     *
     * Usado por: POST /api/auth/login
     */
    public AuthResponse login(LoginRequest request) {
        // 1. Buscar usuario por email
        Optional<User> userOptional = userRepository.findByEmailUade(request.getEmailUade());

        // 2. Validar que el usuario existe
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Email no encontrado");
        }

        User user = userOptional.get();

        // 3. Validar la contraseña usando BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 4. Generar token JWT real con los datos del usuario
        String token = jwtUtil.generateToken(
            user.getEmailUade(),
            user.getId(),
            user.getRole().getId(),
            user.getRole().getRoleName()
        );

        // 5. Convertir User entidad a UserResponse DTO
        UserResponse userResponse = convertToUserResponse(user);

        // 6. Retornar respuesta exitosa con token JWT
        return new AuthResponse(token, userResponse, "Login exitoso");
    }

    /**
     * Register: Registra un nuevo usuario en el sistema.
     *
     * ¿Qué hace este método?
     * - Valida que el email no esté ya registrado
     * - Valida que el rol exista
     * - Valida que el departamento exista (si se proporciona)
     * - Hashea la contraseña con BCrypt antes de guardarla
     * - Crea el nuevo usuario en la base de datos
     * - Devuelve un token JWT real y los datos del nuevo usuario
     *
     * @param request Datos de registro (fullName, email, password, roleId, departmentId)
     * @return AuthResponse con token JWT, datos del usuario y mensaje
     * @throws RuntimeException si el email ya existe, el rol no existe, o el departamento no existe
     *
     * Usado por: POST /api/auth/register
     */
    public AuthResponse register(RegisterRequest request) {
        // 1. Validar que el email no esté duplicado
        if (userRepository.existsByEmailUade(request.getEmailUade())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // 2. Buscar el rol por ID y validar que existe
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + request.getRoleId()));

        // 3. Buscar el departamento si se proporcionó (opcional)
        Department department = null;
        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new RuntimeException("Departamento no encontrado con ID: " + request.getDepartmentId()));
        }

        // 4. Crear la entidad User con los datos del request
        User newUser = new User();
        newUser.setFullName(request.getFullName());
        newUser.setEmailUade(request.getEmailUade());
        // Hashear la contraseña con BCrypt antes de guardarla
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(role);
        newUser.setDepartment(department);

        // 5. Guardar el usuario en la base de datos
        User savedUser = userRepository.save(newUser);

        // 6. Generar token JWT real con los datos del usuario
        String token = jwtUtil.generateToken(
            savedUser.getEmailUade(),
            savedUser.getId(),
            savedUser.getRole().getId(),
            savedUser.getRole().getRoleName()
        );

        // 7. Convertir User entidad a UserResponse DTO
        UserResponse userResponse = convertToUserResponse(savedUser);

        // 8. Retornar respuesta exitosa con token JWT
        return new AuthResponse(token, userResponse, "Registro exitoso");
    }

    /**
     * Me: Obtiene la información del usuario autenticado.
     *
     * ¿Qué hace este método?
     * - Recibe el email del usuario autenticado (en una implementación real, vendría del JWT)
     * - Busca el usuario en la base de datos
     * - Devuelve sus datos sin información sensible
     *
     * @param emailUade Email del usuario autenticado
     * @return UserResponse con los datos del usuario
     * @throws RuntimeException si el usuario no existe
     *
     * Usado por: GET /api/auth/me
     *
     * Nota: En una implementación real con JWT, el email vendría del token decodificado
     */
    public UserResponse me(String emailUade) {
        // 1. Buscar usuario por email
        User user = userRepository.findByEmailUade(emailUade)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Convertir y retornar
        return convertToUserResponse(user);
    }

    /**
     * Método auxiliar: Convierte una entidad User a un DTO UserResponse.
     *
     * ¿Qué hace este método?
     * - Toma una entidad User (con toda la información de BD)
     * - Crea un UserResponse DTO (sin información sensible como password)
     * - Copia solo los campos necesarios para enviar al frontend
     *
     * @param user Entidad User de la base de datos
     * @return UserResponse DTO para enviar al frontend
     */
    private UserResponse convertToUserResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmailUade(user.getEmailUade());
        response.setRoleId(user.getRole().getId());
        response.setRoleName(user.getRole().getRoleName());
        response.setCreatedAt(user.getCreatedAt());

        // Agregar información del departamento si existe
        if (user.getDepartment() != null) {
            response.setDepartmentId(user.getDepartment().getId());
            response.setDepartmentName(user.getDepartment().getName());
        }

        return response;
    }
}
