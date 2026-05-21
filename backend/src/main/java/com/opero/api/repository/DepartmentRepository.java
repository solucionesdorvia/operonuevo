package com.opero.api.repository;

import com.opero.api.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository para la entidad Department.
 *
 * ¿Qué hace este Repository?
 * - Proporciona métodos para realizar operaciones CRUD sobre la tabla 'departments'
 * - Extiende JpaRepository que ya incluye métodos como: save, findAll, findById, delete, etc.
 *
 * Métodos automáticos heredados de JpaRepository:
 * - save(Department department): Guarda o actualiza un departamento
 * - findById(Integer id): Busca un departamento por su ID
 * - findAll(): Obtiene todos los departamentos
 * - deleteById(Integer id): Elimina un departamento por su ID
 * - existsById(Integer id): Verifica si existe un departamento con ese ID
 *
 * Usado por:
 * - POST /api/auth/register (para validar que el departmentId existe)
 * - GET /api/departments (para listar departamentos)
 * - PUT /api/departments/{id} (para actualizar departamentos)
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
    // Por ahora no necesitamos métodos personalizados
    // JpaRepository ya proporciona todos los métodos CRUD básicos
}
