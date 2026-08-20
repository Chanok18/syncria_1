# Release Notes — Syncria v1.0.0

## Fecha de Release

30 de Julio, 2026

---

## Sinopsis

Syncria v1.0.0 es el primer release estable de la plataforma CRM para clinicas veterinarias. Incluye el MVP completo con 6 modulos funcionales, 52 tests backend, y una arquitectura preparada para SaaS multi-tenant.

---

## Nuevas Funcionalidades

### Modulo de Autenticacion
- Registro de usuario con BCrypt
- Login con JWT (expiracion 24h)
- Rate limiting (5 intentos/minuto/IP)
- Protected routes
- Sesion persistente (Zustand persist)

### Modulo de Contactos
- CRUD completo (crear, listar, editar, eliminar)
- Paginacion (20 elementos por pagina)
- Busqueda en tiempo real por nombre/email
- Soft delete (eliminacion logica)
- Multi-tenant (aislamiento por empresa)

### Modulo de Mascotas
- CRUD completo vinculado a contactos
- Busqueda por nombre, especie, raza
- Selector de contacto en formulario
- Soft delete
- Multi-tenant

### Modulo de Citas
- CRUD completo con calendario visual
- 3 vistas: mes, semana, dia
- Conflict detection por mascota + horario
- Estados: SCHEDULED, COMPLETED, CANCELLED
- Localizacion en espanol
- Soft delete
- Multi-tenant

### Modulo Dashboard
- Metricas ejecutivas (clientes, mascotas, citas)
- Citas del dia con badges de estado
- Distribucion de mascotas por especie
- Accesos rapidos (nueva cita, nuevo cliente, nueva mascota)
- Skeleton loading
- Empty states
- Responsive

### Seguridad
- JWT con validacion en startup (min 256 bits)
- Cache de usuarios (TTL 5 min)
- Soft-delete check en JWT filter
- Rate limiting en /login
- CORS configurado

---

## Mejoras de Arquitectura

- Arquitectura modular por capas (controller → service → repository)
- Multi-tenant con companyId en BaseEntity
- DTOs inmutables (Records de Java)
- Mappers type-safe (MapStruct)
- Excepciones custom por modulo
- GlobalExceptionHandler centralizado

---

## Testing

### Backend (52 tests)

| Modulo | Tests | Estado |
|--------|-------|--------|
| DashboardService | 4 | PASS |
| AppointmentService | 13 | PASS |
| PetService | 13 | PASS |
| ContactService | 10 | PASS |
| AuthService | 5 | PASS |
| JwtUtil | 6 | PASS |
| Context load | 1 | PASS |
| **Total** | **52** | **ALL PASS** |

### Frontend (6 tests)

| Componente | Tests | Estado |
|------------|-------|--------|
| Button | 6 | PASS |

### Validaciones

| Validacion | Estado |
|------------|--------|
| Backend compilation | PASS |
| Backend tests | PASS |
| Frontend typecheck | PASS |
| Frontend lint | PASS |
| Frontend build | PASS (1534 modules) |

---

## Stack Tecnologico

| Capa | Tecnologia | Version |
|------|------------|---------|
| Backend | Java | 17 |
| Backend | Spring Boot | 3.5.4 |
| Backend | Spring Security + JWT | jjwt 0.12.6 |
| Backend | MapStruct | 1.5.5 |
| Frontend | React | 18.3.1 |
| Frontend | TypeScript | 5.4.5 |
| Frontend | Vite | 5.3.1 |
| Frontend | TailwindCSS | 3.4.4 |
| Frontend | Zustand | 4.5.2 |
| Frontend | react-big-calendar | 1.20.0 |
| Frontend | date-fns | 4.4.0 |
| Base de datos | PostgreSQL | 16 |
| Migraciones | Flyway | V1-V4 |
| Testing | JUnit 5 + Mockito | Backend |
| Testing | Vitest + RTL | Frontend |

---

## API Endpoints (23 total)

| Metodo | Ruta | Auth | Descripcion |
|--------|------|------|-------------|
| GET | /api/v1/health | No | Health check |
| POST | /api/v1/auth/register | No | Registro |
| POST | /api/v1/auth/login | No | Login |
| GET | /api/v1/auth/me | Si | Usuario actual |
| GET | /api/v1/contacts | Si | Listar contactos |
| GET | /api/v1/contacts/{id} | Si | Detalle contacto |
| POST | /api/v1/contacts | Si | Crear contacto |
| PUT | /api/v1/contacts/{id} | Si | Editar contacto |
| DELETE | /api/v1/contacts/{id} | Si | Eliminar contacto |
| GET | /api/v1/pets | Si | Listar mascotas |
| GET | /api/v1/pets/{id} | Si | Detalle mascota |
| GET | /api/v1/pets/contact/{id} | Si | Mascotas por contacto |
| POST | /api/v1/pets | Si | Crear mascota |
| PUT | /api/v1/pets/{id} | Si | Editar mascota |
| DELETE | /api/v1/pets/{id} | Si | Eliminar mascota |
| GET | /api/v1/appointments | Si | Listar citas |
| GET | /api/v1/appointments/{id} | Si | Detalle cita |
| GET | /api/v1/appointments/range | Si | Citas por rango |
| POST | /api/v1/appointments | Si | Crear cita |
| PUT | /api/v1/appointments/{id} | Si | Editar cita |
| PUT | /api/v1/appointments/{id}/status | Si | Cambiar estado |
| DELETE | /api/v1/appointments/{id} | Si | Eliminar cita |
| GET | /api/v1/dashboard | Si | Dashboard |

---

## Conocido / Limitaciones

- Token JWT en localStorage (riesgo XSS aceptado para MVP)
- Frontend tests limitados (solo Button component)
- Sin Docker funcional (falta Docker Desktop)
- Sin CI/CD pipeline
- Sin Swagger/OpenAPI
- Sin observabilidad (logging, metrics, error tracking)
- Tests corren con H2 in-memory (no PostgreSQL real)

---

## Siguientes Pasos

- **Sprint 06.1**: Swagger, tests frontend, Docker, README
- **Sprint 07**: Beta testing
- **Sprint 08**: Production launch

---

## Agradecimientos

Desarrollado como proyecto de portafolio para demostrar habilidades en:
- Spring Boot + Java 17
- React + TypeScript
- Arquitectura modular
- Multi-tenant
- Testing

---

*Release Notes — Syncria v1.0.0*
*30 de Julio, 2026*
