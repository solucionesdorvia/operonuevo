package com.opero.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utilidad para manejo de JWT (JSON Web Tokens).
 *
 * ¿Qué hace esta clase?
 * - Genera tokens JWT cuando el usuario hace login exitoso
 * - Valida tokens JWT en cada request protegido
 * - Extrae información del token (email, rol, etc.)
 * - Define tiempo de expiración de tokens (24 horas)
 *
 * Estructura de un JWT:
 * - Header: tipo de token y algoritmo de firma
 * - Payload: datos del usuario (email, roleId, roleName)
 * - Signature: firma digital para verificar autenticidad
 */
@Component
public class JwtUtil {

    // Secret key para firmar tokens (se lee de application.properties)
    @Value("${jwt.secret}")
    private String secret;

    // Tiempo de expiración: 24 horas en milisegundos
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    /**
     * Genera un nuevo token JWT para un usuario.
     *
     * ¿Qué hace este método?
     * - Recibe los datos del usuario (email, id, rol)
     * - Crea un token JWT firmado con el secret
     * - Define fecha de expiración (24 horas desde ahora)
     * - Incluye en el payload: email, userId, roleId, roleName
     *
     * @param email Email del usuario (subject del token)
     * @param userId ID del usuario
     * @param roleId ID del rol
     * @param roleName Nombre del rol (STUDENT, PROFESSOR, MANAGER, WORKER)
     * @return Token JWT como String
     */
    public String generateToken(String email, Integer userId, Integer roleId, String roleName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("roleId", roleId);
        claims.put("roleName", roleName);

        return Jwts.builder()
                .claims(claims)
                .subject(email) // El email es el "subject" del token
                .issuedAt(new Date()) // Fecha de emisión: ahora
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Expira en 24 horas
                .signWith(getSigningKey()) // Firma con HMAC-SHA256
                .compact();
    }

    /**
     * Extrae el email del usuario desde el token JWT.
     *
     * @param token Token JWT
     * @return Email del usuario (subject del token)
     */
    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Extrae el userId del token JWT.
     *
     * @param token Token JWT
     * @return ID del usuario
     */
    public Integer extractUserId(String token) {
        return extractAllClaims(token).get("userId", Integer.class);
    }

    /**
     * Extrae el roleId del token JWT.
     *
     * @param token Token JWT
     * @return ID del rol
     */
    public Integer extractRoleId(String token) {
        return extractAllClaims(token).get("roleId", Integer.class);
    }

    /**
     * Extrae el roleName del token JWT.
     *
     * @param token Token JWT
     * @return Nombre del rol
     */
    public String extractRoleName(String token) {
        return extractAllClaims(token).get("roleName", String.class);
    }

    /**
     * Extrae la fecha de expiración del token.
     *
     * @param token Token JWT
     * @return Fecha de expiración
     */
    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    /**
     * Obtiene la clave de firma a partir del secret.
     *
     * @return SecretKey para firmar/validar tokens
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extrae todos los claims (datos) del token JWT.
     *
     * ¿Qué hace este método?
     * - Parsea el token JWT
     * - Verifica la firma con el secret key
     * - Retorna todos los claims (payload del token)
     *
     * @param token Token JWT
     * @return Claims del token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si el token ha expirado.
     *
     * @param token Token JWT
     * @return true si expiró, false si aún es válido
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Valida un token JWT.
     *
     * ¿Qué valida?
     * - Que el email del token coincida con el email del usuario
     * - Que el token no haya expirado
     *
     * @param token Token JWT
     * @param email Email del usuario
     * @return true si el token es válido, false si no
     */
    public Boolean validateToken(String token, String email) {
        final String tokenEmail = extractEmail(token);
        return (tokenEmail.equals(email) && !isTokenExpired(token));
    }
}
