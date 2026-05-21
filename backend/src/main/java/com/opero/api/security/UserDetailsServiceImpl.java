package com.opero.api.security;

import com.opero.api.entity.User;
import com.opero.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implementación de UserDetailsService para Spring Security.
 *
 * ¿Qué hace esta clase?
 * - Spring Security la usa para cargar usuarios desde la base de datos
 * - Convierte nuestra entidad User a UserDetails de Spring Security
 * - Define los roles/authorities del usuario
 * - Se usa internamente en el proceso de autenticación
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carga un usuario por su email (username).
     *
     * ¿Qué hace este método?
     * - Spring Security llama a este método cuando necesita autenticar un usuario
     * - Busca el usuario por email en la base de datos
     * - Convierte la entidad User a UserDetails (formato que Spring Security entiende)
     * - Define los authorities (roles) del usuario
     *
     * @param email Email del usuario (en Spring Security se llama "username")
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Buscar usuario por email
        User user = userRepository.findByEmailUade(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // Convertir el rol a un GrantedAuthority de Spring Security
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        // Prefijo "ROLE_" es requerido por Spring Security
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRoleName()));

        // Retornar UserDetails con los datos del usuario
        return new org.springframework.security.core.userdetails.User(
                user.getEmailUade(),        // username (en nuestro caso, el email)
                user.getPasswordHash(),     // password (ya hasheado)
                true,                       // enabled (cuenta activa)
                true,                       // accountNonExpired
                true,                       // credentialsNonExpired
                true,                       // accountNonLocked
                authorities                 // roles/authorities
        );
    }
}
