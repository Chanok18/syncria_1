# Definición del MVP - Syncria

## Resumen Ejecutivo

El MVP (Minimum Viable Product) de Syncria se enfoca en la industria veterinaria como vertical inicial, proporcionando las funcionalidades esenciales para gestionar clientes (dueños de mascotas), mascotas, citas y comunicación básica.

## Objetivo del MVP

**Validar la hipótesis fundamental**: "Los veterinarios están dispuestos a usar una plataforma CRM dedicada para mejorar la gestión de sus clientes y mascotas, resultando en mejor retención y satisfacción."

## Alcance del MVP

### Módulos Incluidos

#### 1. Módulo de Autenticación
- Registro de usuario (veterinario/administrador)
- Login con email y contraseña
- JWT para sesiones
- Recuperación de contraseña (básico)

#### 2. Módulo de Company/Workspace
- Creación de perfil de clínica
- Configuración básica (nombre, dirección, teléfono)
- Invitación de miembros del equipo (futuro)

#### 3. Módulo de Clientes (Dueños)
- CRUD completo de clientes
- Búsqueda por nombre, email, teléfono
- Historial de interacciones (notas)
- Etiquetas/tags para segmentación

#### 4. Módulo de Mascotas
- Registro de mascotas vinculadas a clientes
- Información básica (nombre, especie, raza, edad)
- Historial médico simple
- Fotos de mascotas

#### 5. Módulo de Citas
- Crear, editar, cancelar citas
- Calendario visual
- Estados de cita (programada, completada, cancelada)
- Recordatorios básicos (email)

#### 6. Módulo de Dashboard
- Resumen del día (citas pendientes)
- Métricas básicas (clientes totales, citas esta semana)
- Accesos rápidos

### Funcionalidades de IA (MVP Limitado)

#### Chat Assistant (Básico)
- Asistente virtual para preguntas frecuentes
- Sugiere respuestas a consultas de clientes
- Resumen de información del cliente

**NO incluido en MVP:**
- Análisis predictivo
- Automatización avanzada
- Integración con WhatsApp
- Módulo de ventas/deals
- Reportes avanzados
- App móvil
- Multi-idioma

## User Stories del MVP

### Épica 1: Autenticación

**US-001: Registro de Usuario**
```
COMO veterquiero
QUIERO registrarme con mi email y contraseña
PARA poder acceder al sistema

CRITERIOS DE ACEPTACIÓN:
- El formulario valida email válido y contraseña mínima de 8 caracteres
- Se envía email de confirmación (futuro)
- Se redirige al dashboard después del registro
- Los datos se guardan en la BD con contraseña encriptada
```

**US-002: Login**
```
COMO usuario registrado
QUIERO iniciar sesión con mi email y contraseña
PARA acceder a mi cuenta

CRITERIOS DE ACEPTACIÓN:
- Se valida email y contraseña
- Se genera token JWT
- Se redirige al dashboard
- El token expira en 24 horas
```

### Épica 2: Clientes

**US-003: Crear Cliente**
```
COMO veterinario
QUIERO registrar un nuevo cliente (dueño de mascota)
PARA mantener un registro de mis clientes

CRITERIOS DE ACEPTACIÓN:
- Campos obligatorios: nombre, email, teléfono
- El email debe ser único por cliente
- Se muestra mensaje de éxito
- Se redirige a la lista de clientes
```

**US-004: Listar Clientes**
```
COMO veterinario
QUIERO ver la lista de todos mis clientes
PARA encontrar rápidamente un cliente

CRITERIOS DE ACEPTACIÓN:
- Se muestra tabla con nombre, email, teléfono
- Paginación de 20 elementos
- Búsqueda por nombre
- Ordenamiento por nombre o fecha de creación
```

**US-005: Editar Cliente**
```
COMO veterinario
QUIERO editar la información de un cliente
PARA mantener los datos actualizados

CRITERIOS DE ACEPTACIÓN:
- Se carga el formulario con datos actuales
- Se validan los campos obligatorios
- Se muestra mensaje de éxito
- Los cambios se reflejan inmediatamente
```

**US-006: Eliminar Cliente**
```
COMO veterinario
QUIERO eliminar un cliente
PARA mantener mi lista limpia

CRITERIOS DE ACEPTACIÓN:
- Se muestra confirmación antes de eliminar
- La eliminación es lógica (no se borra de BD)
- Se oculta de la lista
- No se pierden datos históricos
```

### Épica 3: Mascotas

**US-007: Registrar Mascota**
```
COMO veterinario
QUIERO registrar una mascota vinculada a un dueño
PARA tener el historial completo

CRITERIOS DE ACEPTACIÓN:
- Campos: nombre, especie, raza, fecha nacimiento, sexo
- Se vincula a un dueño existente
- Se puede subir foto
- Se muestra en el perfil del dueño
```

**US-008: Ver Mascotas del Cliente**
```
COMO veterinario
QUIERO ver todas las mascotas de un cliente
PARA tener la información completa del hogar

CRITERIOS DE ACEPTACIÓN:
- Se muestra lista de mascotas del cliente
- Cada mascota muestra nombre, especie, raza
- Se puede hacer click para ver detalles
- Botón para agregar nueva mascota
```

### Épica 4: Citas

**US-009: Crear Cita**
```
COMO veterinario
QUIERO programar una cita para una mascota
PARA organizar mi agenda

CRITERIOS DE ACEPTACIÓN:
- Campos: fecha, hora, mascota, motivo
- Se valida que no haya conflicto de horario
- Se envía confirmación al cliente (futuro)
- Se muestra en el calendario
```

**US-010: Ver Calendario**
```
COMO veterinario
QUIERO ver mis citas en un calendario
PARA organizar mi día

CRITERIOS DE ACEPTACIÓN:
- Vista diaria/semanal/mensual
- Las citas se muestran con color por estado
- Se puede hacer click en una cita para ver detalles
- Navegación entre fechas
```

**US-011: Editar/Cancelar Cita**
```
COMO veterinario
QUIERO modificar o cancelar una cita
PARA mantener mi agenda actualizada

CRITERIOS DE ACEPTACIÓN:
- Se puede cambiar fecha, hora, motivo
- Se puede cancelar con confirmación
- Se registra el motivo de cancelación
- Se notifica al cliente (futuro)
```

### Épica 5: Dashboard

**US-012: Ver Dashboard**
```
COMO veterinario
QUIERO ver un resumen de mi negocio
PARA tener visibilidad de mis operaciones

CRITERIOS DE ACEPTACIÓN:
- Citas de hoy
- Próximas 5 citas
- Total de clientes
- Total de mascotas
- Accesos rápidos (nueva cita, nuevo cliente)
```

## Criterios de Aceptación Generales

### Rendimiento
- Tiempo de carga inicial < 3 segundos
- Tiempo de respuesta API < 500ms
- Soporte para 100 usuarios concurrentes (MVP)

### Seguridad
- Contraseñas encriptadas con bcrypt
- JWT con expiración de 24 horas
- HTTPS obligatorio en producción
- Validación de entrada en todos los endpoints

### Usabilidad
- Interfaz responsiva (desktop, tablet)
- Navegación intuitiva sin capacitación
- Mensajes de error claros y accionables
- Loading states en todas las operaciones

### Calidad
- Cobertura de código > 80% backend
- Cobertura de código > 70% frontend
- Tests unitarios para toda la lógica de negocio
- Tests de integración para endpoints críticos

## Fuera de Alcance (Post-MVP)

| Funcionalidad | Versión Estimada |
|---------------|------------------|
| Módulo de Ventas/Deals | v2.0 |
| Integración WhatsApp | v2.0 |
| IA avanzada (predicciones) | v2.1 |
| App Móvil | v3.0 |
| Multi-idioma | v3.0 |
| Marketplace de integraciones | v4.0 |

## Supuestos del MVP

1. Los veterinarios tienen acceso a internet estable
2. Los usuarios están dispuestos a migrar de Excel/WhatsApp
3. El mercado veterinario valora la organización de datos
4. La curva de aprendizaje no es un impedimento mayor

## Validación del MVP

### Métricas de Éxito
- 50 usuarios activos en primeros 3 meses
- 70% de retención mensual
- NPS > 40
- Al menos 3 testimonios positivos

### Métodos de Validación
- Interviews con 10 veterinarios
- beta cerrada con 20 usuarios
- Análisis de uso (analytics)
- Feedback surveys mensuales

---

*Documento MVP - Syncria v1.0*
*Última actualización: 2026*
