# Product Backlog - Syncria

## Priorizacion (MoSCoW)

### Must Have (MVP)
- [x] US-001: Registro de usuario
- [x] US-002: Login/Logout
- [x] US-003: Crear cliente
- [x] US-004: Listar clientes
- [x] US-005: Editar cliente
- [x] US-006: Eliminar cliente (logico)
- [x] US-007: Registrar mascota
- [x] US-008: Ver mascotas del cliente
- [x] US-009: Crear cita
- [x] US-010: Ver calendario
- [x] US-011: Editar/cancelar cita
- [x] US-012: Ver dashboard

### Should Have (Post-MVP cercano)
- [ ] US-013: Busqueda avanzada de clientes
- [ ] US-014: Filtros por etiquetas
- [ ] US-015: Historial de interacciones
- [ ] US-016: Recordatorios por email
- [ ] US-017: Perfil de usuario
- [ ] US-018: Cambio de contrasena

### Could Have (Futuro)
- [ ] US-019: Integracion con calendario (Google)
- [ ] US-020: Exportar datos (CSV)
- [ ] US-021: Notificaciones push
- [ ] US-022: Chat con asistente IA
- [ ] US-023: Dashboard con graficos
- [ ] US-024: Reportes basicos

### Won't Have (No planeado)
- App movil nativa
- Integracion WhatsApp
- Multi-idioma
- Marketplace de integraciones
- API publica para terceros

---

## Historias de Usuario Detalladas

### Epica 1: Autenticacion

#### US-001: Registro de Usuario
**ID**: US-001
**Prioridad**: Must Have
**Estimacion**: 5 puntos
**Sprint**: 2
**Estado**: Completado

**Descripcion**:
COMO veterquiero
QUIERO registrarme con mi email y contrasena
PARA poder acceder al sistema

**Criterios de Aceptacion**:
1. El formulario valida email valido
2. La contrasena debe tener minimo 8 caracteres
3. Se muestra error si el email ya existe
4. Se redirige al dashboard despues del registro
5. Los datos se guardan con contrasena encriptada (bcrypt)

---

#### US-002: Login/Logout
**ID**: US-002
**Prioridad**: Must Have
**Estimacion**: 3 puntos
**Sprint**: 2
**Estado**: Completado

**Descripcion**:
COMO usuario registrado
QUIERO iniciar sesion con mi email y contrasena
PARA acceder a mi cuenta

**Criterios de Aceptacion**:
1. Se valida email y contrasena
2. Se muestra error claro si credenciales son incorrectas
3. Se genera token JWT
4. Se redirige al dashboard
5. El token expira en 24 horas
6. Logout limpia el token del localStorage

---

### Epica 2: Clientes

#### US-003: Crear Cliente
**ID**: US-003
**Prioridad**: Must Have
**Estimacion**: 5 puntos
**Sprint**: 3
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO registrar un nuevo cliente (dueno de mascota)
PARA mantener un registro de mis clientes

**Criterios de Aceptacion**:
1. Campos obligatorios: nombre, email, telefono
2. El email debe ser unico por cliente
3. Se muestra mensaje de exito
4. Se redirige a la lista de clientes
5. Se valida formato de email
6. Se valida formato de telefono

---

#### US-004: Listar Clientes
**ID**: US-004
**Prioridad**: Must Have
**Estimacion**: 5 puntos
**Sprint**: 3
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO ver la lista de todos mis clientes
PARA encontrar rapidamente un cliente

**Criterios de Aceptacion**:
1. Se muestra tabla con nombre, email, telefono
2. Paginacion de 20 elementos
3. Busqueda por nombre en tiempo real
4. Ordenamiento por nombre o fecha de creacion
5. Loading state mientras se cargan datos
6. Mensaje si no hay clientes

---

#### US-005: Editar Cliente
**ID**: US-005
**Prioridad**: Must Have
**Estimacion**: 3 puntos
**Sprint**: 3
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO editar la informacion de un cliente
PARA mantener los datos actualizados

**Criterios de Aceptacion**:
1. Se carga el formulario con datos actuales
2. Se validan los campos obligatorios
3. Se muestra mensaje de exito
4. Los cambios se reflejan inmediatamente
5. Se puede cancelar la edicion

---

#### US-006: Eliminar Cliente
**ID**: US-006
**Prioridad**: Must Have
**Estimacion**: 2 puntos
**Sprint**: 3
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO eliminar un cliente
PARA mantener mi lista limpia

**Criterios de Aceptacion**:
1. Se muestra confirmacion antes de eliminar
2. La eliminacion es logica (soft delete)
3. Se oculta de la lista
4. No se pierden datos historicos
5. Se puede deshacer (futuro)

---

### Epica 3: Mascotas

#### US-007: Registrar Mascota
**ID**: US-007
**Prioridad**: Must Have
**Estimacion**: 5 puntos
**Sprint**: 4
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO registrar una mascota vinculada a un dueno
PARA tener el historial completo

**Criterios de Aceptacion**:
1. Campos: nombre, especie, raza, fecha nacimiento, sexo
2. Se vincula a un dueno existente
3. Se puede subir foto (opcional)
4. Se muestra en el perfil del dueno
5. Se validan campos obligatorios

---

#### US-008: Ver Mascotas del Cliente
**ID**: US-008
**Prioridad**: Must Have
**Estimacion**: 3 puntos
**Sprint**: 4
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO ver todas las mascotas de un cliente
PARA tener la informacion completa del hogar

**Criterios de Aceptacion**:
1. Se muestra lista de mascotas del cliente
2. Cada mascota muestra nombre, especie, raza
3. Se puede hacer click para ver detalles
4. Boton para agregar nueva mascota
5. Loading state mientras se cargan

---

### Epica 4: Citas

#### US-009: Crear Cita
**ID**: US-009
**Prioridad**: Must Have
**Estimacion**: 8 puntos
**Sprint**: 5
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO programar una cita para una mascota
PARA organizar mi agenda

**Criterios de Aceptacion**:
1. Campos: fecha, hora, mascota, motivo
2. Se valida que no haya conflicto de horario
3. Se envia confirmacion al cliente (futuro)
4. Se muestra en el calendario
5. Estado inicial: "Programada"

---

#### US-010: Ver Calendario
**ID**: US-010
**Prioridad**: Must Have
**Estimacion**: 8 puntos
**Sprint**: 5
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO ver mis citas en un calendario
PARA organizar mi dia

**Criterios de Aceptacion**:
1. Vista diaria/semanal/mensual
2. Las citas se muestran con color por estado
3. Se puede hacer click en una cita para ver detalles
4. Navegacion entre fechas
5. Leyenda de colores por estado

---

#### US-011: Editar/Cancelar Cita
**ID**: US-011
**Prioridad**: Must Have
**Estimacion**: 3 puntos
**Sprint**: 5
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO modificar o cancelar una cita
PARA mantener mi agenda actualizada

**Criterios de Aceptacion**:
1. Se puede cambiar fecha, hora, motivo
2. Se puede cancelar con confirmacion
3. Se registra el motivo de cancelacion
4. Los cambios se reflejan en el calendario

---

### Epica 5: Dashboard

#### US-012: Ver Dashboard
**ID**: US-012
**Prioridad**: Must Have
**Estimacion**: 5 puntos
**Sprint**: 6
**Estado**: Completado

**Descripcion**:
COMO veterinario
QUIERO ver un resumen de mi negocio
PARA tener visibilidad de mis operaciones

**Criterios de Aceptacion**:
1. Citas de hoy
2. Proximas 5 citas
3. Total de clientes
4. Total de mascotas
5. Accesos rapidos (nueva cita, nuevo cliente)
6. Loading state mientras se cargan datos

---

## Estimacion de Puntos por Sprint

| Sprint | Puntos | Historias | Estado |
|--------|--------|-----------|--------|
| 1 | 8 | Infraestructura | Completado |
| 2 | 8 | US-001, US-002 | Completado |
| 2.1 | ~12 | Hardening | Completado |
| 3 | 15 | US-003, US-004, US-005, US-006 | Completado |
| 4 | 8 | US-007, US-008 | Completado |
| 5 | 19 | US-009, US-010, US-011 | Completado |
| 6 | 5 | US-012 | Completado |
| **Total** | **75** | **12 historias** | **75 completados** |

## Definicion de Done (DoD)

Una historia de usuario esta completa cuando:

- [x] Codigo implementado y funcionando
- [x] Tests unitarios escritos (cobertura > 80% backend, > 70% frontend)
- [ ] Tests de integracion para endpoints criticos
- [x] Code review aprobada
- [ ] Documentacion de API actualizada
- [x] Sin bugs conocidos criticos
- [ ] Deploy a staging exitoso
- [x] Aceptada por el product owner

---

*Product Backlog - Syncria v1.0.0*
*Ultima actualizacion: 2026-08-23*
