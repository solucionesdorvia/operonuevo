## 📋 Instrucciones Generales

1. **Abrir Swagger UI:** http://localhost:8080/swagger-ui.html
2. **Entender la autorización en Swagger:**
   - 🔒 **SIN Autorizar**: NO se envía token → Endpoints protegidos devuelven 401
   - 🔓 **Autorizado**: Se envía token automáticamente en TODOS los requests
3. **Para autorizar:** Botón "Authorize" (arriba derecha) → Pegar `Bearer <token>` → "Authorize"
4. **Para cerrar sesión:** Botón "Authorize" → "Logout"
5. **Copiar tokens** de las respuestas de login/register para usar en "Authorize"
6. **Marcar con ✅** cada prueba completada exitosamente

## ⚠️ IMPORTANTE: Flujo de Autorización en Swagger

```
┌─────────────────────────────────────────────────────────────┐
│ INICIO: Candado cerrado 🔒 (NO autorizado)                  │
│ → Todos los endpoints protegidos devuelven 401              │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 1: Registrar/Login → Copiar TOKEN                      │
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ PASO 2: Click en "Authorize" → Pegar "Bearer TOKEN"         │
│ → Ahora todos los requests incluyen el token automáticamente│
└─────────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────────┐
│ Para cambiar de usuario:                                     │
│ 1. "Authorize" → "Logout"                                    │
│ 2. Login con otro usuario → Copiar nuevo TOKEN              │
│ 3. "Authorize" → Pegar nuevo token                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 🧪 SECCIÓN 1: Autenticación Básica

### ✅ Prueba 1.1: Registrar usuario STUDENT

**Endpoint:** `POST /api/auth/register`

**JSON Request:**
```json
{
  "fullName": "Juan Pérez",
  "emailUade": "juan.perez@uade.edu.ar",
  "password": "password123",
  "roleId": 1,
  "departmentId": null
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Respuesta contiene:
  - `token` (string largo)
  - `user.id` (número)
  - `user.roleId` = 1
  - `user.roleName` = "STUDENT"
  - `message` = "Registro exitoso"

**Acción:** Copiar el `token` para próximas pruebas

---

### ✅ Prueba 1.2: Registrar usuario PROFESSOR

**Endpoint:** `POST /api/auth/register`

**JSON Request:**
```json
{
  "fullName": "María García",
  "emailUade": "maria.garcia@uade.edu.ar",
  "password": "password456",
  "roleId": 2,
  "departmentId": null
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `user.roleName` = "PROFESSOR"

---

### ✅ Prueba 1.3: Registrar usuario MANAGER

**Endpoint:** `POST /api/auth/register`

**JSON Request:**
```json
{
  "fullName": "Carlos Rodríguez",
  "emailUade": "carlos.rodriguez@uade.edu.ar",
  "password": "password789",
  "roleId": 3,
  "departmentId": 1
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `user.roleName` = "MANAGER"
- ✅ `user.departmentId` = 1
- ✅ `user.departmentName` = "Mantenimiento"

---

### ✅ Prueba 1.4: Registrar usuario WORKER

**Endpoint:** `POST /api/auth/register`

**JSON Request:**
```json
{
  "fullName": "Ana Martínez",
  "emailUade": "ana.martinez@uade.edu.ar",
  "password": "password321",
  "roleId": 4,
  "departmentId": 1
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `user.roleName` = "WORKER"

---

### ✅ Prueba 1.5: Login exitoso

**Endpoint:** `POST /api/auth/login`

**JSON Request:**
```json
{
  "emailUade": "juan.perez@uade.edu.ar",
  "password": "password123"
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Respuesta contiene token válido
- ✅ `message` = "Login exitoso"

---

### ✅ Prueba 1.6: Login con email inexistente

**Endpoint:** `POST /api/auth/login`

**JSON Request:**
```json
{
  "emailUade": "noexiste@uade.edu.ar",
  "password": "password123"
}
```

**Resultado Esperado:**
- ✅ Status Code: **401 Unauthorized**
- ✅ Body contiene: "Email no encontrado"

---

### ✅ Prueba 1.7: Login con contraseña incorrecta

**Endpoint:** `POST /api/auth/login`

**JSON Request:**
```json
{
  "emailUade": "juan.perez@uade.edu.ar",
  "password": "passwordIncorrecta"
}
```

**Resultado Esperado:**
- ✅ Status Code: **401 Unauthorized**
- ✅ Body contiene: "Contraseña incorrecta"

---

### ✅ Prueba 1.8: Registro con email duplicado

**Endpoint:** `POST /api/auth/register`

**JSON Request:**
```json
{
  "fullName": "Usuario Duplicado",
  "emailUade": "juan.perez@uade.edu.ar",
  "password": "password999",
  "roleId": 1,
  "departmentId": null
}
```

**Resultado Esperado:**
- ✅ Status Code: **400 Bad Request**
- ✅ Body contiene: "El email ya está registrado"

---

## 🔒 SECCIÓN 2: Validación de Tokens

### ✅ Prueba 2.1: Acceder sin token (401)

**⚠️ IMPORTANTE:** NO hagas clic en "Authorize". El candado debe estar cerrado 🔒

**Endpoint:** `GET /api/auth/me`

**Pasos en Swagger:**
1. Verificar que el candado esté **cerrado** 🔒 (arriba a la derecha)
2. Si está abierto, hacer clic en "Authorize" → "Logout"
3. Ejecutar `GET /api/auth/me`

**Resultado Esperado:**
- ✅ Status Code: **401 Unauthorized**
- ✅ Body JSON:
```json
{
  "error": "No autenticado",
  "message": "Full authentication is required to access this resource"
}
```

---

### ✅ Prueba 2.2: Acceder con token inválido (401)

**Pasos en Swagger:**
1. Hacer clic en "Authorize" (candado)
2. En el campo "Value", pegar: `Bearer token-falso-invalido`
3. Hacer clic en "Authorize" y cerrar el modal
4. Ejecutar `GET /api/auth/me`

**Resultado Esperado:**
- ✅ Status Code: **401 Unauthorized**
- ✅ Body contiene error de autenticación

**Después de la prueba:**
- Hacer clic en "Authorize" → "Logout" (para limpiar)

---

### ✅ Prueba 2.3: Acceder con token válido (200)

**Preparación:**
1. Si aún no lo hiciste, ejecutar `POST /api/auth/login` con Juan Pérez
2. **Copiar el token** de la respuesta

**Pasos en Swagger:**
1. Hacer clic en "Authorize"
2. Pegar: `Bearer <token-completo-copiado>`
3. Hacer clic en "Authorize" y cerrar el modal
4. Verificar que el candado esté **abierto** 🔓
5. Ejecutar `GET /api/auth/me`

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Respuesta contiene:
  - `fullName` = "Juan Pérez"
  - `emailUade` = "juan.perez@uade.edu.ar"
  - `roleName` = "STUDENT"

**⚠️ A partir de ahora, TODOS los requests incluirán este token automáticamente**

---

### ✅ Prueba 2.4: Actualizar propio perfil con token válido

**Prerequisito:** Token de Juan Pérez autorizado en Swagger (candado 🔓)

**Endpoint:** `PUT /api/users/me`

**JSON Request:**
```json
{
  "fullName": "Juan Pérez Actualizado"
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `fullName` = "Juan Pérez Actualizado"

---

### ✅ Prueba 2.5: Usuario intenta cambiar su propio rol (400)

**Prerequisito:** Token de Juan Pérez autorizado en Swagger (candado 🔓)

**Endpoint:** `PUT /api/users/me`

**JSON Request:**
```json
{
  "fullName": "Juan Pérez",
  "roleId": 3
}
```

**Resultado Esperado:**
- ✅ Status Code: **400 Bad Request**
- ✅ Body: "Los usuarios no pueden cambiar su rol o departamento"

---

## 👥 SECCIÓN 3: Autorización por Roles - INCIDENTES

### Setup: Crear Incidentes de Prueba

Primero, como **Juan (STUDENT)**, crear 2 incidentes:

**Endpoint:** `POST /api/incidents`

**Headers:**
```
Authorization: Bearer <token-de-juan.perez>
```

**JSON Request (Incidente 1):**
```json
{
  "title": "Aire acondicionado no funciona",
  "description": "El AC del aula 301 no enciende",
  "locationDescription": "Aula 301, 3er piso",
  "photoUrl": null,
  "priority": "HIGH",
  "departmentId": 1,
  "reporterId": 1
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Incidente creado (anotar el ID)

**JSON Request (Incidente 2):**
```json
{
  "title": "Proyector sin imagen",
  "description": "El proyector del aula 205 no proyecta",
  "locationDescription": "Aula 205, 2do piso",
  "photoUrl": null,
  "priority": "MEDIUM",
  "departmentId": 2,
  "reporterId": 1
}
```

---

### ✅ Prueba 3.1: STUDENT ve solo sus incidentes

**Pasos en Swagger:**
1. Verificar que estás autorizado como **Juan Pérez** (STUDENT)
2. Si no: "Logout" → Login como Juan → Copiar token → "Authorize" con ese token
3. El candado debe estar 🔓

**Endpoint:** `GET /api/incidents`

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Array con exactamente **2 incidentes** (los que Juan creó)
- ✅ Todos tienen `reporter.emailUade` = "juan.perez@uade.edu.ar"

---

### ✅ Prueba 3.2: PROFESSOR ve solo sus incidentes

**Cambiar de usuario en Swagger:**
1. Hacer clic en "Authorize" → "Logout"
2. Ejecutar `POST /api/auth/login` con María García
3. Copiar el token de María
4. "Authorize" → Pegar el token de María
5. Candado debe estar 🔓

**Endpoint:** `POST /api/incidents`

**JSON Request:**
```json
{
  "title": "Internet lento",
  "description": "La conexión es muy lenta",
  "locationDescription": "Aula 101",
  "photoUrl": null,
  "priority": "MEDIUM",
  "departmentId": 2,
  "reporterId": 2
}
```

Luego listar incidentes:

**Endpoint:** `GET /api/incidents`

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Array con **1 incidente** (solo el de María)
- ✅ NO ve los incidentes de Juan

---

### ✅ Prueba 3.3: WORKER ve solo incidentes asignados

**Login como Ana (WORKER)**

Primero, como **Carlos (MANAGER)**, asignar incidente a Ana:

**Endpoint:** `PATCH /api/incidents/{id}/assign`

**Headers:**
```
Authorization: Bearer <token-de-carlos.rodriguez>
```

**JSON Request:**
```json
{
  "workerId": 4
}
```

Ahora, como **Ana (WORKER)**, listar incidentes:

**Endpoint:** `GET /api/incidents`

**Headers:**
```
Authorization: Bearer <token-de-ana.martinez>
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Array con **1 incidente** (el asignado a Ana)
- ✅ `worker.id` = 4

---

### ✅ Prueba 3.4: MANAGER ve todos los incidentes de su departamento

**Login como Carlos (MANAGER de Mantenimiento - Dept 1)**

**Endpoint:** `GET /api/incidents`

**Headers:**
```
Authorization: Bearer <token-de-carlos.rodriguez>
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Array con todos los incidentes del **Departamento 1**
- ✅ Todos tienen `department.id` = 1
- ✅ NO ve incidentes de otros departamentos

---

### ✅ Prueba 3.5: STUDENT intenta ver incidente de otro usuario (403)

**Login como Juan (STUDENT)**

**Endpoint:** `GET /api/incidents/{id-de-maria}`

**Headers:**
```
Authorization: Bearer <token-de-juan.perez>
```

**Resultado Esperado:**
- ✅ Status Code: **404 Not Found** o mensaje de error
- ✅ Body: "No tiene permiso para ver este incidente"

---

### ✅ Prueba 3.6: WORKER intenta crear incidente (403)

**Login como Ana (WORKER)**

**Endpoint:** `POST /api/incidents`

**Headers:**
```
Authorization: Bearer <token-de-ana.martinez>
```

**JSON Request:**
```json
{
  "title": "Test",
  "description": "Test",
  "locationDescription": "Test",
  "priority": "LOW",
  "departmentId": 1,
  "reporterId": 4
}
```

**Resultado Esperado:**
- ✅ Status Code: **403 Forbidden**
- ✅ Body JSON:
```json
{
  "error": "Acceso denegado",
  "message": "Access Denied"
}
```

---

## 🛠️ SECCIÓN 4: Operaciones de Gestión de Incidentes

### ✅ Prueba 4.1: WORKER cambia estado a IN_PROCESS

**Login como Ana (WORKER)**

**Endpoint:** `PATCH /api/incidents/{id-asignado-a-ana}/status`

**Headers:**
```
Authorization: Bearer <token-de-ana.martinez>
```

**JSON Request:**
```json
{
  "status": "IN_PROCESS"
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `status` = "IN_PROCESS"

---

### ✅ Prueba 4.2: WORKER cambia estado a FINISHED

**Endpoint:** `PATCH /api/incidents/{id-asignado-a-ana}/status`

**JSON Request:**
```json
{
  "status": "FINISHED"
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `status` = "FINISHED"

---

### ✅ Prueba 4.3: STUDENT intenta cambiar estado (403)

**Login como Juan (STUDENT)**

**Endpoint:** `PATCH /api/incidents/{id}/status`

**Headers:**
```
Authorization: Bearer <token-de-juan.perez>
```

**JSON Request:**
```json
{
  "status": "FINISHED"
}
```

**Resultado Esperado:**
- ✅ Status Code: **403 Forbidden**
- ✅ Mensaje de acceso denegado

---

### ✅ Prueba 4.4: MANAGER asigna worker a incidente

**Login como Carlos (MANAGER)**

**Endpoint:** `PATCH /api/incidents/{id}/assign`

**Headers:**
```
Authorization: Bearer <token-de-carlos.rodriguez>
```

**JSON Request:**
```json
{
  "workerId": 4
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `worker.id` = 4
- ✅ `status` = "ASSIGNED"

---

### ✅ Prueba 4.5: WORKER intenta asignar incidente (403)

**Login como Ana (WORKER)**

**Endpoint:** `PATCH /api/incidents/{id}/assign`

**Headers:**
```
Authorization: Bearer <token-de-ana.martinez>
```

**JSON Request:**
```json
{
  "workerId": 4
}
```

**Resultado Esperado:**
- ✅ Status Code: **403 Forbidden**

---

### ✅ Prueba 4.6: MANAGER cambia prioridad

**Login como Carlos (MANAGER)**

**Endpoint:** `PATCH /api/incidents/{id}/priority`

**Headers:**
```
Authorization: Bearer <token-de-carlos.rodriguez>
```

**JSON Request:**
```json
{
  "priority": "HIGH"
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `priority` = "HIGH"

---

### ✅ Prueba 4.7: STUDENT intenta cambiar prioridad (403)

**Login como Juan (STUDENT)**

**Endpoint:** `PATCH /api/incidents/{id}/priority`

**Headers:**
```
Authorization: Bearer <token-de-juan.perez>
```

**JSON Request:**
```json
{
  "priority": "LOW"
}
```

**Resultado Esperado:**
- ✅ Status Code: **403 Forbidden**

---

### ✅ Prueba 4.8: MANAGER deriva incidente a otro departamento

**Login como Carlos (MANAGER)**

**Endpoint:** `PATCH /api/incidents/{id}/department`

**Headers:**
```
Authorization: Bearer <token-de-carlos.rodriguez>
```

**JSON Request:**
```json
{
  "departmentId": 2
}
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ `department.id` = 2
- ✅ `status` = "PENDING_ASSIGNMENT"
- ✅ `worker` = null

---

## 👤 SECCIÓN 5: Gestión de Usuarios

### ✅ Prueba 5.1: MANAGER lista todos los usuarios

**Login como Carlos (MANAGER)**

**Endpoint:** `GET /api/users`

**Headers:**
```
Authorization: Bearer <token-de-carlos.rodriguez>
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Array con todos los usuarios registrados (4+)

---

### ✅ Prueba 5.2: STUDENT intenta listar usuarios (403)

**Login como Juan (STUDENT)**

**Endpoint:** `GET /api/users`

**Headers:**
```
Authorization: Bearer <token-de-juan.perez>
```

**Resultado Esperado:**
- ✅ Status Code: **403 Forbidden**

---

### ✅ Prueba 5.3: MANAGER ve usuario específico

**Login como Carlos (MANAGER)**

**Endpoint:** `GET /api/users/{id}`

**Headers:**
```
Authorization: Bearer <token-de-carlos.rodriguez>
```

**Resultado Esperado:**
- ✅ Status Code: **200 OK**
- ✅ Datos del usuario solicitado

---

### ✅ Prueba 5.4: STUDENT intenta ver otro usuario (403)

**Login como Juan (STUDENT)**

**Endpoint:** `GET /api/users/2`

**Headers:**
```
Authorization: Bearer <token-de-juan.perez>
```

**Resultado Esperado:**
- ✅ Status Code: **403 Forbidden**

---
