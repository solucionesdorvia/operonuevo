-- =====================================================
-- Script de datos iniciales para Opero
-- =====================================================
-- Este script se ejecuta automáticamente al iniciar la aplicación
-- cuando se usa H2 en memoria con spring.jpa.hibernate.ddl-auto=create-drop
-- =====================================================

-- =====================================================
-- 1. INSERTAR ROLES
-- =====================================================
-- Estos son los 4 roles del sistema según el DER
INSERT INTO roles (id, role_name) VALUES (1, 'STUDENT');
INSERT INTO roles (id, role_name) VALUES (2, 'PROFESSOR');
INSERT INTO roles (id, role_name) VALUES (3, 'MANAGER');
INSERT INTO roles (id, role_name) VALUES (4, 'WORKER');

-- =====================================================
-- 2. INSERTAR DEPARTAMENTOS
-- =====================================================
-- Departamentos de la institución que gestionan incidentes
-- manager_id se dejará NULL por ahora (se asignará después de crear usuarios)
INSERT INTO departments (id, name, manager_id) VALUES (1, 'Mantenimiento', NULL);
INSERT INTO departments (id, name, manager_id) VALUES (2, 'Redes', NULL);
INSERT INTO departments (id, name, manager_id) VALUES (3, 'Infraestructura', NULL);
INSERT INTO departments (id, name, manager_id) VALUES (4, 'Limpieza', NULL);

-- =====================================================
-- 3. INSERTAR USUARIOS DE PRUEBA
-- =====================================================
-- Estos usuarios son para facilitar el testing
-- NOTA: Los passwords están en texto plano (NO HACER EN PRODUCCIÓN)
-- TODO: Implementar BCrypt para hashear passwords
-- IMPORTANTE: No especificamos el ID para que la base de datos lo genere automáticamente

-- Usuario 1: Alumno
INSERT INTO users (full_name, email_uade, password_hash, role_id, department_id, created_at, updated_at)
VALUES ('Juan Pérez', 'juan.perez@uade.edu.ar', 'password123', 1, NULL, NOW(), NOW());

-- Usuario 2: Profesor
INSERT INTO users (full_name, email_uade, password_hash, role_id, department_id, created_at, updated_at)
VALUES ('María García', 'maria.garcia@uade.edu.ar', 'password123', 2, NULL, NOW(), NOW());

-- Usuario 3: Gerente de Mantenimiento
INSERT INTO users (full_name, email_uade, password_hash, role_id, department_id, created_at, updated_at)
VALUES ('Carlos Rodríguez', 'carlos.rodriguez@uade.edu.ar', 'password123', 3, 1, NOW(), NOW());

-- Usuario 4: Trabajador de Mantenimiento
INSERT INTO users (full_name, email_uade, password_hash, role_id, department_id, created_at, updated_at)
VALUES ('Ana Martínez', 'ana.martinez@uade.edu.ar', 'password123', 4, 1, NOW(), NOW());

-- Usuario 5: Trabajador de Redes
INSERT INTO users (full_name, email_uade, password_hash, role_id, department_id, created_at, updated_at)
VALUES ('Pedro López', 'pedro.lopez@uade.edu.ar', 'password123', 4, 2, NOW(), NOW());

-- =====================================================
-- 4. ACTUALIZAR MANAGERS DE DEPARTAMENTOS
-- =====================================================
-- Asignar gerentes a los departamentos
-- Nota: Carlos será el ID 3 si nadie más se registró antes
UPDATE departments SET manager_id = 3 WHERE id = 1; -- Carlos es gerente de Mantenimiento
UPDATE departments SET manager_id = 3 WHERE id = 2; -- Carlos también gestiona Redes (ejemplo)

-- =====================================================
-- 5. INSERTAR INCIDENTES DE PRUEBA
-- =====================================================
-- Estos incidentes son para facilitar el testing de los endpoints
-- Nota: No especificamos el ID para que la base de datos lo genere automáticamente

-- Incidente 1: Reportado por Juan (alumno), asignado a Ana (trabajadora), departamento Mantenimiento, EN PROCESO
INSERT INTO incidents (title, description, location_description, photo_url, status, priority, reporter_id, worker_id, department_id, created_at, updated_at)
VALUES (
    'Aire acondicionado no funciona',
    'El aire acondicionado del aula 301 no enciende. Se intentó usar el control remoto pero no responde.',
    'Aula 301, 3er piso, Edificio A',
    NULL,
    'IN_PROCESS',
    'HIGH',
    1,  -- Juan Pérez (alumno)
    4,  -- Ana Martínez (trabajadora de Mantenimiento)
    1,  -- Departamento Mantenimiento
    NOW(),
    NOW()
);

-- Incidente 2: Reportado por María (profesora), sin asignar, departamento Redes, PENDIENTE DE ASIGNACIÓN
INSERT INTO incidents (title, description, location_description, photo_url, status, priority, reporter_id, worker_id, department_id, created_at, updated_at)
VALUES (
    'Internet lento en aula 205',
    'La conexión a Internet en el aula 205 es extremadamente lenta. Los alumnos no pueden acceder a las plataformas educativas.',
    'Aula 205, 2do piso, Edificio B',
    NULL,
    'PENDING_ASSIGNMENT',
    'MEDIUM',
    2,  -- María García (profesora)
    NULL,  -- Sin asignar aún
    2,  -- Departamento Redes
    NOW(),
    NOW()
);

-- Incidente 3: Reportado por Juan (alumno), asignado a Pedro (trabajador de Redes), departamento Redes, ASIGNADO
INSERT INTO incidents (title, description, location_description, photo_url, status, priority, reporter_id, worker_id, department_id, created_at, updated_at)
VALUES (
    'Proyector no proyecta imagen',
    'El proyector del aula 101 se enciende pero no proyecta imagen. La luz indicadora está en verde.',
    'Aula 101, 1er piso, Edificio A',
    NULL,
    'ASSIGNED',
    'MEDIUM',
    1,  -- Juan Pérez (alumno)
    5,  -- Pedro López (trabajador de Redes)
    2,  -- Departamento Redes
    NOW(),
    NOW()
);

-- Incidente 4: Reportado por María (profesora), sin asignar, departamento Infraestructura, PENDIENTE
INSERT INTO incidents (title, description, location_description, photo_url, status, priority, reporter_id, worker_id, department_id, created_at, updated_at)
VALUES (
    'Gotera en el techo',
    'Hay una gotera en el pasillo que genera charcos de agua. Es peligroso para los alumnos.',
    'Pasillo principal, 2do piso, Edificio C',
    NULL,
    'PENDING',
    'HIGH',
    2,  -- María García (profesora)
    NULL,  -- Sin asignar aún
    3,  -- Departamento Infraestructura
    NOW(),
    NOW()
);

-- Incidente 5: Reportado por Juan (alumno), asignado a Ana, departamento Mantenimiento, FINALIZADO
INSERT INTO incidents (title, description, location_description, photo_url, status, priority, reporter_id, worker_id, department_id, created_at, updated_at)
VALUES (
    'Pizarra rota',
    'La pizarra del aula 404 tiene una rajadura y no se puede escribir en la mitad derecha.',
    'Aula 404, 4to piso, Edificio A',
    NULL,
    'FINISHED',
    'LOW',
    1,  -- Juan Pérez (alumno)
    4,  -- Ana Martínez (trabajadora de Mantenimiento)
    1,  -- Departamento Mantenimiento
    NOW(),
    NOW()
);

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================
