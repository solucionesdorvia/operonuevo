package com.opero.api.repository;

import com.opero.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para la entidad User.
 *
 * ¿Qué hace este Repository?
 * - Proporciona métodos para realizar operaciones CRUD sobre la tabla 'users'
 * - Extiende JpaRepository que ya incluye métodos como: save, findAll, findById, delete, etc.
 * - Define métodos personalizados de consulta usando Spring Data JPA Query Methods
 *
 * Métodos automáticos heredados de JpaRepository:
 * - save(User user): Guarda o actualiza un usuario
 * - findById(Integer id): Busca un usuario por su ID
 * - findAll(): Obtiene todos los usuarios
 * - deleteById(Integer id): Elimina un usuario por su ID
 * - existsById(Integer id): Verifica si existe un usuario con ese ID
 *
 * Métodos personalizados:
 * - findByEmailUade: Busca un usuario por su email institucional
 * - existsByEmailUade: Verifica si ya existe un usuario con ese email
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Busca un usuario por su email institucional.
     *
     * @param emailUade Email institucional del usuario (ej: "juan.perez@uade.edu.ar")
     * @return Optional con el usuario si existe, Optional.empty() si no existe
     *
     * Usado por:
     * - POST /api/auth/login (para autenticar al usuario)
     * - GET /api/auth/me (para obtener el usuario actual)
     *
     * Nota: Spring Data JPA genera automáticamente la query:
     * SELECT * FROM users WHERE email_uade = ?
     */
    Optional<User> findByEmailUade(String emailUade);

    /**
     * Verifica si ya existe un usuario con ese email institucional.
     *
     * @param emailUade Email institucional a verificar
     * @return true si existe, false si no existe
     *
     * Usado por:
     * - POST /api/auth/register (para validar que el email no esté duplicado)
     *
     * Nota: Spring Data JPA genera automáticamente la query:
     * SELECT COUNT(*) > 0 FROM users WHERE email_uade = ?
     */
    boolean existsByEmailUade(String emailUade);
}
