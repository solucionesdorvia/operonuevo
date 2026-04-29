package com.opero.api.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración de Spring Security.
 *
 * ¿Qué configura esta clase?
 * - Define qué endpoints son públicos (accesibles sin token)
 * - Define qué endpoints requieren autenticación
 * - Configura reglas de autorización por rol
 * - Configura el filtro JWT para validar tokens
 * - Configura BCrypt para hashear passwords
 * - Desactiva CSRF (ya que usamos JWT, no cookies)
 *
 * Reglas de negocio:
 * - Endpoints públicos: /api/auth/login, /api/auth/register, /api/ping, Swagger
 * - STUDENT/PROFESSOR: Crear incidentes, ver solo sus propios incidentes
 * - WORKER: Ver incidentes asignados a él, cambiar status
 * - MANAGER: Ver todos los incidentes de su departamento, asignar workers, gestión completa
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Permite usar @PreAuthorize en los controllers
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Configuración principal de seguridad.
     *
     * ¿Qué hace este método?
     * - Define las reglas de autorización para cada endpoint
     * - Configura el filtro JWT
     * - Desactiva CSRF (no necesario con JWT)
     * - Configura sesiones como STATELESS (sin estado, solo JWT)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Deshabilitar CSRF (Cross-Site Request Forgery)
            // No es necesario con JWT porque no usamos cookies
            .csrf(csrf -> csrf.disable())

            // Permitir H2 Console en frames (solo para desarrollo)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )

            // Configurar reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints PÚBLICOS (sin autenticación)
                .requestMatchers(
                    "/api/auth/login",       // Login
                    "/api/auth/register",    // Registro
                    "/api/ping",             // Health check
                    "/swagger-ui/**",        // Swagger UI
                    "/swagger-ui.html",      // Swagger HTML
                    "/v3/api-docs/**",       // OpenAPI docs
                    "/api-docs/**",          // API docs (custom path)
                    "/swagger-resources/**", // Swagger resources
                    "/webjars/**",           // Webjars (Swagger dependencies)
                    "/configuration/**",     // Swagger configuration
                    "/h2-console/**"         // H2 Console (solo para desarrollo)
                ).permitAll()

                // Endpoints de AUTENTICACIÓN - Requieren autenticación
                .requestMatchers("/api/auth/me").authenticated() // Ver datos del usuario autenticado
                .requestMatchers("/api/auth/logout").authenticated() // Logout

                // Endpoints de USUARIOS - Requieren autenticación
                .requestMatchers("/api/users/me").authenticated() // Ver/editar propio perfil
                .requestMatchers("/api/users/{id}").hasRole("MANAGER") // Ver otros usuarios: solo MANAGER
                .requestMatchers("/api/users").hasRole("MANAGER") // Listar usuarios: solo MANAGER

                // Endpoints de INCIDENTES - Reglas por rol
                // Crear incidente: STUDENT, PROFESSOR, MANAGER (no WORKER)
                .requestMatchers(HttpMethod.POST, "/api/incidents").hasAnyRole("STUDENT", "PROFESSOR", "MANAGER")
                // Listar incidentes: todos autenticados (filtrado por rol en service)
                .requestMatchers(HttpMethod.GET, "/api/incidents").authenticated()
                // Ver detalle de incidente: todos autenticados (filtrado por rol en service)
                .requestMatchers(HttpMethod.GET, "/api/incidents/{id}").authenticated()
                // Actualizar incidente: STUDENT, PROFESSOR, MANAGER (validación en service)
                .requestMatchers(HttpMethod.PUT, "/api/incidents/{id}").hasAnyRole("STUDENT", "PROFESSOR", "MANAGER")
                // Eliminar incidente: solo MANAGER
                .requestMatchers(HttpMethod.DELETE, "/api/incidents/{id}").hasRole("MANAGER")

                // Operaciones de gestión de incidentes: MANAGER y WORKER
                .requestMatchers("/api/incidents/{id}/status").hasAnyRole("MANAGER", "WORKER")
                .requestMatchers("/api/incidents/{id}/assign").hasRole("MANAGER")
                .requestMatchers("/api/incidents/{id}/priority").hasRole("MANAGER")
                .requestMatchers("/api/incidents/{id}/department").hasRole("MANAGER")

                // Endpoints de DEPARTAMENTOS
                .requestMatchers("/api/departments/**").authenticated()

                // Cualquier otro endpoint requiere autenticación
                .anyRequest().authenticated()
            )

            // Configurar sesiones como STATELESS (sin estado)
            // Con JWT no mantenemos sesiones en el servidor
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Configurar manejo de excepciones de autenticación
            .exceptionHandling(exception -> exception
                // Cuando no hay autenticación (sin token o token inválido) → 401 Unauthorized
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"No autenticado\",\"message\":\"" + authException.getMessage() + "\"}");
                })
                // Cuando hay autenticación pero no tiene permisos → 403 Forbidden
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Acceso denegado\",\"message\":\"" + accessDeniedException.getMessage() + "\"}");
                })
            )

            // Agregar el filtro JWT ANTES del filtro de autenticación de Spring Security
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Bean para hashear passwords con BCrypt.
     *
     * ¿Para qué sirve?
     * - BCrypt es un algoritmo de hashing seguro para passwords
     * - Incluye "salt" automático para prevenir ataques de rainbow tables
     * - Spring Security lo usa automáticamente cuando validamos passwords
     *
     * Uso:
     * - Al registrar: passwordEncoder.encode(passwordPlano)
     * - Al login: passwordEncoder.matches(passwordPlano, passwordHash)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Bean de AuthenticationManager.
     *
     * ¿Para qué sirve?
     * - Maneja el proceso de autenticación de Spring Security
     * - Lo necesitamos para poder inyectarlo en otros servicios si es necesario
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
