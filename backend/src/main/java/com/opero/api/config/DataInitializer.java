package com.opero.api.config;

import com.opero.api.entity.Role;
import com.opero.api.entity.Department;
import com.opero.api.repository.RoleRepository;
import com.opero.api.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos fundamentales del sistema.
 *
 * ¿Qué hace esta clase?
 * - Se ejecuta automáticamente al iniciar la aplicación (CommandLineRunner)
 * - Verifica si los roles existen en la base de datos
 * - Si no existen, los crea automáticamente
 * - Hace lo mismo con los departamentos básicos
 *
 * ¿Por qué usar código en lugar de SQL?
 * - Los roles y departamentos son parte fundamental del sistema, no datos de prueba
 * - El código es versionable y mantenible
 * - Se ejecuta independientemente de la base de datos (H2, PostgreSQL, etc.)
 * - Evita errores de SQL duplicados o IDs inconsistentes
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeDepartments();
    }

    /**
     * Inicializa los 4 roles del sistema si no existen.
     *
     * Roles según el DER:
     * 1. STUDENT - Alumnos que reportan incidentes
     * 2. PROFESSOR - Profesores que reportan incidentes
     * 3. MANAGER - Gerentes de departamento que gestionan incidentes
     * 4. WORKER - Trabajadores que resuelven incidentes
     */
    private void initializeRoles() {
        // Verificar si ya existen roles
        if (roleRepository.count() > 0) {
            System.out.println("✅ Roles ya inicializados (" + roleRepository.count() + " roles encontrados)");
            return;
        }

        System.out.println("🔧 Inicializando roles del sistema...");

        // Crear los 4 roles (sin setear ID, que sea auto-generado)
        Role student = new Role();
        student.setRoleName("STUDENT");
        roleRepository.save(student);

        Role professor = new Role();
        professor.setRoleName("PROFESSOR");
        roleRepository.save(professor);

        Role manager = new Role();
        manager.setRoleName("MANAGER");
        roleRepository.save(manager);

        Role worker = new Role();
        worker.setRoleName("WORKER");
        roleRepository.save(worker);

        System.out.println("✅ Roles inicializados: STUDENT, PROFESSOR, MANAGER, WORKER");
    }

    /**
     * Inicializa los departamentos básicos del sistema si no existen.
     *
     * Departamentos comunes en instituciones educativas:
     * - Mantenimiento
     * - Redes
     * - Infraestructura
     * - Limpieza
     *
     * Nota: Los departamentos se crean sin manager asignado.
     * Los managers se asignarán cuando los usuarios MANAGER se registren.
     */
    private void initializeDepartments() {
        // Verificar si ya existen departamentos
        if (departmentRepository.count() > 0) {
            System.out.println("✅ Departamentos ya inicializados (" + departmentRepository.count() + " departamentos encontrados)");
            return;
        }

        System.out.println("🔧 Inicializando departamentos del sistema...");

        // Crear los departamentos básicos
        Department mantenimiento = new Department();
        mantenimiento.setName("Mantenimiento");
        mantenimiento.setManager(null); // Sin manager asignado inicialmente
        departmentRepository.save(mantenimiento);

        Department redes = new Department();
        redes.setName("Redes");
        redes.setManager(null);
        departmentRepository.save(redes);

        Department infraestructura = new Department();
        infraestructura.setName("Infraestructura");
        infraestructura.setManager(null);
        departmentRepository.save(infraestructura);

        Department limpieza = new Department();
        limpieza.setName("Limpieza");
        limpieza.setManager(null);
        departmentRepository.save(limpieza);

        System.out.println("✅ Departamentos inicializados: Mantenimiento, Redes, Infraestructura, Limpieza");
    }
}
