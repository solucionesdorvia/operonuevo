package com.opero.api.config;

import com.opero.api.entity.Role;
import com.opero.api.entity.Department;
import com.opero.api.repository.RoleRepository;
import com.opero.api.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Inicializador de datos fundamentales del sistema.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public void run(String... args) throws Exception {
        initializeRoles();
        initializeDepartments();
    }

    private void initializeRoles() {
        if (roleRepository.count() > 0) {
            log.info("Roles ya inicializados ({} roles encontrados)", roleRepository.count());
            return;
        }

        log.info("Inicializando roles del sistema...");

        Role user = new Role();
        user.setRoleName("USER");
        roleRepository.save(user);

        Role manager = new Role();
        manager.setRoleName("MANAGER");
        roleRepository.save(manager);

        Role worker = new Role();
        worker.setRoleName("WORKER");
        roleRepository.save(worker);

        log.info("Roles inicializados: USER, MANAGER, WORKER");
    }

    private void initializeDepartments() {
        if (departmentRepository.count() > 0) {
            log.info("Departamentos ya inicializados ({} departamentos encontrados)", departmentRepository.count());
            return;
        }

        log.info("Inicializando departamentos del sistema...");

        Department mantenimiento = new Department();
        mantenimiento.setName("Mantenimiento");
        mantenimiento.setManager(null);
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

        log.info("Departamentos inicializados: Mantenimiento, Redes, Infraestructura, Limpieza");
    }
}
