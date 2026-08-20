# Sprint 05 — Citas (Appointments)

## Informacion del Sprint

| Campo | Valor |
|-------|-------|
| **Nombre** | Sprint 05: Citas |
| **Duracion** | 2 semanas (10 dias laborales) |
| **Fecha Inicio** | 2026-07-29 |
| **Fecha Fin** | 2026-07-29 |
| **Puntos de Historia** | 19 |
| **Capacidad** | 40 horas |
| **Sprint anterior** | Sprint 04: Mascotas (v0.4.0) |

## Objetivo del Sprint

> "Entregar el modulo de Citas: CRUD completo con calendario visual, estados de cita, validacion de conflictos de horario, y aislamiento multi-tenant."

---

## Historias de Usuario

### US-009: Crear Cita

| Campo | Valor |
|-------|-------|
| **ID** | US-009 |
| **Prioridad** | Must Have |
| **Estimacion** | 8 puntos |

**Descripcion**:
COMO veterinario
QUIERO programar una cita para una mascota
PARA organizar mi agenda

**Criterios de Aceptacion**:
1. Campos: fecha, hora, mascota (petId), motivo
2. Se valida que no haya conflicto de horario para la misma mascota
3. Se muestra en el calendario inmediatamente
4. Se vincula a mascota y contacto existentes
5. Estado inicial: SCHEDULED
6. Solo se pueden crear citas en el futuro

**Tarea tecnica**:
- Crear Appointment entity con enum AppointmentStatus
- Crear endpoint POST /api/v1/appointments
- Implementar validacion de conflictos de horario
- Crear componente de formulario con date picker

---

### US-010: Ver Calendario

| Campo | Valor |
|-------|-------|
| **ID** | US-010 |
| **Prioridad** | Must Have |
| **Estimacion** | 8 puntos |

**Descripcion**:
COMO veterinario
QUIERO ver mis citas en un calendario
PARA organizar mi dia

**Criterios de Aceptacion**:
1. Vista diaria, semanal y mensual
2. Las citas se muestran con color por estado (SCHEDULED=azul, COMPLETED=verde, CANCELLED=rojo)
3. Se puede hacer click en una cita para ver detalles
4. Navegacion entre fechas (hoy, anterior, siguiente)
5. Leyenda de colores por estado
6. Loading state mientras se cargan datos

**Tarea tecnica**:
- Integrar react-big-calendar + date-fns
- Crear componente CalendarView con 3 vistas
- Cargar citas desde API por rango de fechas
- Implementar navegacion y selector de vistas

---

### US-011: Editar/Cancelar Cita

| Campo | Valor |
|-------|-------|
| **ID** | US-011 |
| **Prioridad** | Must Have |
| **Estimacion** | 3 puntos |

**Descripcion**:
COMO veterinario
QUIERO modificar o cancelar una cita
PARA mantener mi agenda actualizada

**Criterios de Aceptacion**:
1. Se puede cambiar fecha, hora, motivo
2. Se puede cancelar con confirmacion
3. Se puede marcar como completada
4. Los cambios se reflejan en el calendario inmediatamente
5. No se pueden editar citas completadas o canceladas

**Tarea tecnica**:
- Crear endpoints PUT y DELETE para citas
- Crear componente de edicion
- Implementar confirmacion de cancelacion
- Implementar cambio de estado a COMPLETED

---

## Arquitectura del Modulo

### Backend

```
module/appointment/
├── controller/AppointmentController.java
├── service/AppointmentService.java
├── repository/AppointmentRepository.java
├── entity/Appointment.java
├── entity/AppointmentStatus.java
├── dto/AppointmentRequestDTO.java
├── dto/AppointmentResponseDTO.java
├── mapper/AppointmentMapper.java
└── exception/
    ├── AppointmentNotFoundException.java
    └── AppointmentConflictException.java
```

### Frontend

```
features/appointments/
├── components/
│   ├── AppointmentCalendar.tsx
│   ├── AppointmentFormModal.tsx
│   ├── AppointmentDetailModal.tsx
│   └── AppointmentDeleteModal.tsx
├── hooks/useAppointments.ts
├── store/appointmentStore.ts
├── types/appointmentTypes.ts
└── utils/calendarStyles.ts
```

### Base de Datos

```sql
CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    pet_id BIGINT NOT NULL REFERENCES pets(id),
    contact_id BIGINT NOT NULL REFERENCES contacts(id),
    title VARCHAR(255) NOT NULL,
    reason TEXT,
    appointment_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_appointments_company ON appointments(company_id);
CREATE INDEX idx_appointments_pet ON appointments(pet_id);
CREATE INDEX idx_appointments_contact ON appointments(contact_id);
CREATE INDEX idx_appointments_date ON appointments(company_id, appointment_date);
CREATE INDEX idx_appointments_status ON appointments(company_id, status);
```

### API Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/v1/appointments | Listar citas (filtrado por fecha + estado) |
| GET | /api/v1/appointments/{id} | Detalle de cita |
| POST | /api/v1/appointments | Crear cita (con validacion de conflictos) |
| PUT | /api/v1/appointments/{id} | Editar cita |
| PUT | /api/v1/appointments/{id}/status | Cambiar estado de cita |
| DELETE | /api/v1/appointments/{id} | Eliminar cita (soft delete) |
| GET | /api/v1/appointments/range | Obtener citas por rango de fechas (para calendario) |

### AppointmentStatus Enum

```java
public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED
}
```

---

## Dependencias NPM (Frontend)

```bash
npm install react-big-calendar date-fns
npm install -D @types/react-big-calendar
```

---

## Estimacion de Tiempo

| Fase | Horas |
|------|-------|
| Backend (entity, repo, service, controller, tests) | 14h |
| Frontend (calendar integration, CRUD modals, store) | 16h |
| Integracion y validacion | 4h |
| Buffer | 6h |
| **Total** | **40h** |

---

## Criterios de Aceptacion para el Sprint

- [x] Un usuario puede crear una cita vinculada a mascota y contacto
- [x] Se valida que no haya conflicto de horario para la misma mascota
- [x] Las citas se muestran en calendario con colores por estado
- [x] Se puede ver en vista diaria, semanal y mensual
- [x] Se puede navegar entre fechas
- [x] Se puede editar una cita programada
- [x] Se puede cancelar una cita con confirmacion
- [x] Se puede marcar una cita como completada
- [x] No se pueden editar citas completadas o canceladas
- [x] Solo ve citas de su empresa (multi-tenant)
- [x] Los tests pasan
- [x] El build es exitoso

---

*Sprint 05 Plan — Syncria*
*Fecha: 2026-07-29*
*Estado: COMPLETADO*
