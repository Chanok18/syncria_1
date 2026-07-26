# Product Backlog - Syncria

## Priorización (MoSCoW)

### Must Have (MVP)
- [ ] US-001: Registro de usuario
- [ ] US-002: Login/Logout
- [ ] US-003: Crear cliente
- [ ] US-004: Listar clientes
- [ ] US-005: Editar cliente
- [ ] US-006: Eliminar cliente (lógico)
- [ ] US-007: Registrar mascota
- [ ] US-008: Ver mascotas del cliente
- [ ] US-009: Crear cita
- [ ] US-010: Ver calendario
- [ ] US-011: Editar/cancelar cita
- [ ] US-012: Ver dashboard

### Should Have (Post-MVP cercano)
- [ ] US-013: Búsqueda avanzada de clientes
- [ ] US-014: Filtros por etiquetas
- [ ] US-015: Historial de interacciones
- [ ] US-016: Recordatorios por email
- [ ] US-017: Perfil de usuario
- [ ] US-018: Cambio de contraseña

### Could Have (Futuro)
- [ ] US-019: Integración con calendario (Google)
- [ ] US-020: Exportar datos (CSV)
- [ ] US-021: Notificaciones push
- [ ] US-022: Chat con asistente IA
- [ ] US-023: Dashboard con gráficos
- [ ] US-024: Reportes básicos

### Won't Have (No planeado)
- [ ] App móvil nativa
- [ ] Integración WhatsApp
- [ ] Multi-idioma
- [ ] Marketplace de integraciones
- [ ] API pública para terceros

---

## Historias de Usuario Detalladas

### Épica 1: Autenticación

#### US-001: Registro de Usuario
**ID**: US-001
**Prioridad**: Must Have
**Estimación**: 5 puntos
**Sprint**: 1

**Descripción**:
COMO veterquiero
QUIERO registrarme con mi email y contraseña
PARA poder acceder al sistema

**Criterios de Aceptación**:
1. El formulario valida email válido
2. La contraseña debe tener mínimo 8 caracteres
3. Se muestra error si el email ya existe
4. Se redirige al dashboard después del registro
5. Los datos se guardan con contraseña encriptada (bcrypt)

**Tarea técnica**:
- Crear endpoint POST /api/v1/auth/register
- Implementar validación de email único
- Encriptar contraseña con bcrypt
- Generar JWT después del registro
- Crear componente de formulario en React

---

#### US-002: Login/Logout
**ID**: US-002
**Prioridad**: Must Have
**Estimación**: 3 puntos
**Sprint**: 1

**Descripción**:
COMO usuario registrado
QUIERO iniciar sesión con mi email y contraseña
PARA acceder a mi cuenta

**Criterios de Aceptación**:
1. Se valida email y contraseña
2. Se muestra error claro si credenciales son incorrectas
3. Se genera token JWT
4. Se redirige al dashboard
5. El token expira en 24 horas
6. Logout limpia el token del localStorage

**Tarea técnica**:
- Crear endpoint POST /api/v1/auth/login
- Implementar validación de credenciales
- Generar JWT con expiration
- Crear interceptor de autenticación en Axios
- Implementar logout en Zustand store

---

### Épica 2: Clientes

#### US-003: Crear Cliente
**ID**: US-003
**Prioridad**: Must Have
**Estimación**: 5 puntos
**Sprint**: 2

**Descripción**:
COMO veterinario
QUIERO registrar un nuevo cliente (dueño de mascota)
PARA mantener un registro de mis clientes

**Criterios de Aceptación**:
1. Campos obligatorios: nombre, email, teléfono
2. El email debe ser único por cliente
3. Se muestra mensaje de éxito
4. Se redirige a la lista de clientes
5. Se valida formato de email
6. Se valida formato de teléfono

**Tarea técnica**:
- Crear endpoint POST /api/v1/clients
- Implementar validaciones de negocio
- Crear DTOs de request/response
- Crear componente de formulario en React
- Implementar manejo de errores

---

#### US-004: Listar Clientes
**ID**: US-004
**Prioridad**: Must Have
**Estimación**: 5 puntos
**Sprint**: 2

**Descripción**:
COMO veterinario
QUIERO ver la lista de todos mis clientes
PARA encontrar rápidamente un cliente

**Criterios de Aceptación**:
1. Se muestra tabla con nombre, email, teléfono
2. Paginación de 20 elementos
3. Búsqueda por nombre en tiempo real
4. Ordenamiento por nombre o fecha de creación
5. Loading state mientras se cargan datos
6. Mensaje si no hay clientes

**Tarea técnica**:
- Crear endpoint GET /api/v1/clients con paginación
- Implementar búsqueda con query parameter
- Crear componente de tabla con paginación
- Implementar debounce en búsqueda
- Crear Zustand store para clientes

---

#### US-005: Editar Cliente
**ID**: US-005
**Prioridad**: Must Have
**Estimación**: 3 puntos
**Sprint**: 2

**Descripción**:
COMO veterinario
QUIERO editar la información de un cliente
PARA mantener los datos actualizados

**Criterios de Aceptación**:
1. Se carga el formulario con datos actuales
2. Se validan los campos obligatorios
3. Se muestra mensaje de éxito
4. Los cambios se reflejan inmediatamente
5. Se puede cancelar la edición

**Tarea técnica**:
- Crear endpoint PUT /api/v1/clients/{id}
- Implementar carga de datos iniciales
- Reutilizar componente de formulario
- Implementar optimistic updates

---

#### US-006: Eliminar Cliente
**ID**: US-006
**Prioridad**: Must Have
**Estimación**: 2 puntos
**Sprint**: 2

**Descripción**:
COMO veterinario
QUIERO eliminar un cliente
PARA mantener mi lista limpia

**Criterios de Aceptación**:
1. Se muestra confirmación antes de eliminar
2. La eliminación es lógica (soft delete)
3. Se oculta de la lista
4. No se pierden datos históricos
5. Se puede deshacer (futuro)

**Tarea técnica**:
- Crear endpoint DELETE /api/v1/clients/{id}
- Implementar soft delete en entity
- Crear componente de confirmación
- Actualizar lista después de eliminar

---

### Épica 3: Mascotas

#### US-007: Registrar Mascota
**ID**: US-007
**Prioridad**: Must Have
**Estimación**: 5 puntos
**Sprint**: 3

**Descripción**:
COMO veterinario
QUIERO registrar una mascota vinculada a un dueño
PARA tener el historial completo

**Criterios de Aceptación**:
1. Campos: nombre, especie, raza, fecha nacimiento, sexo
2. Se vincula a un dueño existente
3. Se puede subir foto (opcional)
4. Se muestra en el perfil del dueño
5. Se validan campos obligatorios

**Tarea técnica**:
- Crear endpoint POST /api/v1/pets
- Implementar upload de archivos (fotos)
- Crear componente de formulario
- Vincular con módulo de clientes

---

#### US-008: Ver Mascotas del Cliente
**ID**: US-008
**Prioridad**: Must Have
**Estimación**: 3 puntos
**Sprint**: 3

**Descripción**:
COMO veterinario
QUIERO ver todas las mascotas de un cliente
PARA tener la información completa del hogar

**Criterios de Aceptación**:
1. Se muestra lista de mascotas del cliente
2. Cada mascota muestra nombre, especie, raza
3. Se puede hacer click para ver detalles
4. Botón para agregar nueva mascota
5. Loading state mientras se cargan

**Tarea técnica**:
- Crear endpoint GET /api/v1/clients/{id}/pets
- Crear componente de lista de mascotas
- Implementar navegación a detalles

---

### Épica 4: Citas

#### US-009: Crear Cita
**ID**: US-009
**Prioridad**: Must Have
**Estimación**: 8 puntos
**Sprint**: 4

**Descripción**:
COMO veterinario
QUIERO programar una cita para una mascota
PARA organizar mi agenda

**Criterios de Aceptación**:
1. Campos: fecha, hora, mascota, motivo
2. Se valida que no haya conflicto de horario
3. Se muestra en el calendario
4. Se vincula a mascota y cliente existente
5. Estado inicial: "Programada"

**Tarea técnica**:
- Crear endpoint POST /api/v1/appointments
- Implementar validación de conflictos de horario
- Crear componente de formulario con date picker
- Integrar con calendario visual

---

#### US-010: Ver Calendario
**ID**: US-010
**Prioridad**: Must Have
**Estimación**: 8 puntos
**Sprint**: 4

**Descripción**:
COMO veterinario
QUIERO ver mis citas en un calendario
PARA organizar mi día

**Criterios de Aceptación**:
1. Vista diaria/semanal/mensual
2. Las citas se muestran con color por estado
3. Se puede hacer click en una cita para ver detalles
4. Navegación entre fechas
5. Leyenda de colores por estado

**Tarea técnica**:
- Integrar librería de calendario (react-big-calendar o similar)
- Crear componente de calendario
- Implementar navegación y vistas
- Cargar citas desde API

---

#### US-011: Editar/Cancelar Cita
**ID**: US-011
**Prioridad**: Must Have
**Estimación**: 3 puntos
**Sprint**: 4

**Descripción**:
COMO veterinario
QUIERO modificar o cancelar una cita
PARA mantener mi agenda actualizada

**Criterios de Aceptación**:
1. Se puede cambiar fecha, hora, motivo
2. Se puede cancelar con confirmación
3. Se registra el motivo de cancelación
4. Los cambios se reflejan en el calendario

**Tarea técnica**:
- Crear endpoints PUT y DELETE para citas
- Crear componente de edición
- Implementar confirmación de cancelación
- Actualizar calendario después de cambios

---

### Épica 5: Dashboard

#### US-012: Ver Dashboard
**ID**: US-012
**Prioridad**: Must Have
**Estimación**: 5 puntos
**Sprint**: 5

**Descripción**:
COMO veterinario
QUIERO ver un resumen de mi negocio
PARA tener visibilidad de mis operaciones

**Criterios de Aceptación**:
1. Citas de hoy
2. Próximas 5 citas
3. Total de clientes
4. Total de mascotas
5. Accesos rápidos (nueva cita, nuevo cliente)
6. Loading state mientras se cargan datos

**Tarea técnica**:
- Crear endpoint GET /api/v1/dashboard
- Implementar queries optimizadas
- Crear componente de dashboard
- Implementar cards con métricas

---

## Estimación de Puntos por Sprint

| Sprint | Puntos | Historias |
|--------|--------|-----------|
| 1 | 8 | US-001, US-002 |
| 2 | 15 | US-003, US-004, US-005, US-006 |
| 3 | 8 | US-007, US-008 |
| 4 | 19 | US-009, US-010, US-011 |
| 5 | 5 | US-012 |
| **Total** | **55** | **12 historias** |

## Definición de Done (DoD)

Una historia de usuario está completa cuando:

- [ ] Código implementado y funcionando
- [ ] Tests unitarios escritos (cobertura > 80% backend, > 70% frontend)
- [ ] Tests de integración para endpoints críticos
- [ ] Code review aprobada
- [ ] Documentación de API actualizada
- [ ] Sin bugs conocidos críticos
- [ ] Deploy a staging exitoso
- [ ] Aceptada por el product owner

---

*Product Backlog - Syncria v1.0*
*Última actualización: 2026*
