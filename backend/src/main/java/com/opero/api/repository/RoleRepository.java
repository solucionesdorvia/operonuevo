package com.opero.api.repository;

import com.opero.api.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository para la entidad Role.
 *
 * ¿Qué hace este Repository?
 * - Proporciona métodos para realizar operaciones CRUD sobre la tabla 'roles'
 * - Extiende JpaRepository que ya incluye métodos como: save, findAll, findById, delete, etc.
 * - Define métodos personalizados de consulta usando Spring Data JPA Query Methods
 *
 * Métodos automáticos heredados de JpaRepository:
 * - save(Role role): Guarda o actualiza un rol
 * - findById(Integer id): Busca un rol por su IDz
 * - findAll(): Obtiene todos los roles
 * - deleteById(Integer id): Elimina un rol por su ID
 * - existsById(Integer id): Verifica si existe un rol con ese ID
 *
 * Métodos personalizados:
 * - findByRoleName: Busca un rol por su nombre (USER, MANAGER, WORKER)
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * Busca un rol por su nombre.
     *
     * @param roleName Nombre del rol (ej: "USER", "MANAGER", "WORKER")
     * @return Optional con el rol si existe, Optional.empty() si no existe
     *
     * Usado por:
     * - POST /api/auth/register (para asignar el rol al nuevo usuario)
     * - Utilidades del sistema para validar roles
     *
     * Nota: Spring Data JPA genera automáticamente la query:
     * SELECT * FROM roles WHERE role_name = ?
     */
    Optional<Role> findByRoleName(String roleName);
}
