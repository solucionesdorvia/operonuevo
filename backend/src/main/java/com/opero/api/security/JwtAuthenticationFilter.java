package com.opero.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro de autenticación JWT.
 *
 * ¿Qué hace este filtro?
 * - Se ejecuta en CADA request HTTP antes de llegar al controller
 * - Extrae el token JWT del header Authorization
 * - Valida el token
 * - Si es válido, carga el usuario en el contexto de Spring Security
 * - Si no es válido o no existe, deja que Spring Security maneje el error (401)
 *
 * Formato esperado del header:
 * Authorization: Bearer <token-jwt-aqui>
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    /**
     * Método principal del filtro que se ejecuta en cada request.
     *
     * ¿Qué hace?
     * 1. Extrae el header Authorization
     * 2. Si existe y empieza con "Bearer ", extrae el token
     * 3. Valida el token y extrae el email
     * 4. Carga el usuario desde la base de datos
     * 5. Valida que el token sea válido para ese usuario
     * 6. Configura la autenticación en Spring Security
     * 7. Pasa el request al siguiente filtro/controller
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip JWT validation for public endpoints.
        // OJO: /api/departments solo es publico para GET (lo usa el Register sin token).
        // POST/PUT necesitan pasar por el filtro para que @PreAuthorize('hasRole(MANAGER)')
        // funcione — sin esto cualquier endpoint protegido de departments tira 401.
        String path = request.getRequestURI();
        String method = request.getMethod();
        boolean isPublicDepartmentRead = path.startsWith("/api/departments") && "GET".equals(method);
        if (path.startsWith("/api/files/") ||
            isPublicDepartmentRead ||
            path.equals("/api/auth/login") ||
            path.equals("/api/auth/register") ||
            path.equals("/api/ping")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 1. Obtener el header Authorization
            String authorizationHeader = request.getHeader("Authorization");

            String email = null;
            String jwt = null;

            // 2. Verificar que el header existe y tiene el formato correcto "Bearer <token>"
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // Extraer el token (quitando el prefijo "Bearer ")
                jwt = authorizationHeader.substring(7);
                // Extraer el email del token
                email = jwtUtil.extractEmail(jwt);
            }

            // 3. Si tenemos email Y no hay autenticación previa en el contexto de Spring Security
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // 4. Cargar los detalles del usuario desde la base de datos
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // 5. Validar el token
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                    // 6. Crear objeto de autenticación de Spring Security
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,        // Principal (usuario autenticado)
                                    null,               // Credentials (ya validamos con JWT, no necesitamos password)
                                    userDetails.getAuthorities() // Authorities (roles)
                            );

                    // Agregar detalles de la request (IP, sesión, etc.)
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 7. Configurar la autenticación en el contexto de Spring Security
                    // A partir de este punto, el usuario está autenticado para este request
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
        } catch (Exception e) {
            // Si hay algún error (token inválido, expirado, etc.), simplemente continuar
            // Spring Security manejará la falta de autenticación retornando 401
            logger.error("Error en autenticación JWT: " + e.getMessage());
        }

        // 8. Continuar con la cadena de filtros (pasar al siguiente filtro o al controller)
        filterChain.doFilter(request, response);
    }
}
