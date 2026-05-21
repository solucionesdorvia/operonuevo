package com.opero.api.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

/**
 * Utilidades para obtener información del usuario autenticado.
 *
 * ¿Para qué sirve esta clase?
 * - Facilita obtener el email y rol del usuario autenticado
 * - Evita duplicar código en múltiples controllers/services
 * - Centraliza el acceso al contexto de Spring Security
 */
public class SecurityUtil {

    /**
     * Obtiene el email del usuario actualmente autenticado.
     *
     * @return Email del usuario autenticado
     * @throws IllegalStateException si no hay usuario autenticado
     */
    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        return authentication.getName(); // El "name" es el email (username)
    }

    /**
     * Obtiene el rol del usuario actualmente autenticado.
     *
     * @return Nombre del rol (ej: "USER", "MANAGER", "WORKER")
     * @throws IllegalStateException si no hay usuario autenticado
     */
    public static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuario no autenticado");
        }

        // Obtener el primer authority (rol) del usuario
        // Los roles tienen prefijo "ROLE_" en Spring Security, lo quitamos
        return authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .orElseThrow(() -> new IllegalStateException("Usuario sin rol asignado"));
    }

    /**
     * Verifica si el usuario actual tiene un rol específico.
     *
     * @param roleName Nombre del rol a verificar (sin prefijo "ROLE_")
     * @return true si el usuario tiene ese rol, false si no
     */
    public static boolean hasRole(String roleName) {
        try {
            String currentRole = getCurrentUserRole();
            return currentRole.equals(roleName);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    /**
     * Verifica si el usuario actual tiene alguno de los roles especificados.
     *
     * @param roleNames Nombres de roles a verificar
     * @return true si el usuario tiene al menos uno de esos roles, false si no
     */
    public static boolean hasAnyRole(String... roleNames) {
        String currentRole = getCurrentUserRole();
        for (String role : roleNames) {
            if (currentRole.equals(role)) {
                return true;
            }
        }
        return false;
    }
}
