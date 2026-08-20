# Sprint History - Syncria

Registro de todos los sprints completados.

---

## Sprint 01: Setup e Infraestructura

| Campo | Valor |
|-------|-------|
| **Estado** | Completado |
| **Fecha Inicio** | 2026-07-25 |
| **Fecha Fin** | 2026-07-26 |
| **Puntos** | 8 |
| **Version** | v0.1.0 |

### Objetivo
Configurar toda la infraestructura base del proyecto: repositorio, contenedores Docker, backend Spring Boot, frontend React, y herramientas de calidad de codigo.

### Entregables
- Repositorio Git con Git Flow (main, develop)
- Docker Compose con PostgreSQL 16, pgAdmin, n8n
- Backend Spring Boot scaffold (BaseEntity, HealthController, Flyway V1)
- Frontend React + TypeScript + Vite + TailwindCSS scaffold
- Documentacion: AGENTS.md, README.md, .env.example, .gitignore

### Historias de Usuario
- Infraestructura base (sin historias de usuario formales)

### Archivos Clave Creados
```
.gitignore, README.md, .env.example
docker-compose.yml, docker/postgres/init.sql
backend/pom.xml, backend/src/.../SyncriaApplication.java
backend/src/.../BaseEntity.java, HealthController.java
backend/src/.../db/migration/V1__create_initial_schema.sql
frontend/package.json, frontend/vite.config.ts
frontend/src/App.tsx, MainLayout.tsx, api.ts, types/index.ts
```

### Veredicto
Completado. Scaffold solido para construir sobre el.

---

## Sprint 02: Autenticacion

| Campo | Valor |
|-------|-------|
| **Estado** | Aceptado con Fixes |
| **Fecha Inicio** | 2026-07-26 |
| **Fecha Fin** | 2026-07-27 |
| **Puntos** | 8 |
| **Version** | v0.2.0 |

### Objetivo
Entregar un sistema completo de autenticacion: registro, login, JWT, rutas protegidas y logout.

### Entregables
- Backend: User entity, UserRepository, UserService
- Backend: AuthController (register, login, me)
- Backend: AuthService con BCrypt + JWT
- Backend: SecurityConfig, JwtAuthenticationFilter, JwtUtil
- Backend: GlobalExceptionHandler con handlers para auth, 404, 409, validacion
- Frontend: LoginPage, RegisterPage, ProtectedRoute
- Frontend: authStore (Zustand) con login, register, logout
- Frontend: Header con nombre de usuario y boton logout
- Frontend: Sidebar con NavLink activo
- Documentacion: sprint-02.md, PROJECT_STATUS.md, roadmap.md actualizado

### Historias de Usuario
| ID | Historia | Puntos | Estado |
|----|----------|--------|--------|
| US-001 | Registro de Usuario | 5 | Completado |
| US-002 | Login/Logout | 3 | Completado |

### Fixes Aplicados Durante Auditoria
| Fix | Descripcion |
|-----|-------------|
| pom.xml | Java 21 → 17 (JDK instalado es 17) |
| SecurityConfig | setAllowedOrigins → setAllowedOriginPatterns (Spring Security 6.x) |
| UserService | RuntimeException → NotFoundException (HTTP 404) |
| GlobalExceptionHandler | Movido a shared/exception/ + handlers para AccessDeniedException, AuthenticationException |
| AGENTS.md | Java 21 → 17 |

### Known Issues
| # | Severidad | Descripcion |
|---|-----------|-------------|
| 1 | HIGH | companyId hardcodeado a 1L |
| 2 | HIGH | JWT query en cada request sin cache |
| 3 | HIGH | Soft-delete no verificado en JWT filter |
| 4 | HIGH | Session no persiste despues de refresh |
| 5 | MEDIUM | JWT secret con fallback predecible |
| 6 | MEDIUM | Sin rate limiting en /login |
| 7 | MEDIUM | Token en localStorage (XSS risk, aceptado MVP) |
| 8 | LOW | Sin tests unitarios de auth |

### Veredicto
**APPROVED WITH FIXES**. Arquitectura solida, modulo funcional, fixes aplicados durante auditoria. Issues restantes programados para Sprint 03.

---

---

## Sprint 02.1: Hardening

| Campo | Valor |
|-------|-------|
| **Estado** | Completado |
| **Fecha** | 2026-07-29 |
| **Puntos** | ~12 |
| **Version** | v0.2.1 |

### Objetivo
Resolver tech debt critico antes de iniciar el desarrollo de modulos de negocio.

### Tareas Completadas
| TD | Tarea | Archivos |
|----|-------|----------|
| TD-001 | Company entity + companyId dinamico | Company.java, CompanyRepository, AuthService |
| TD-002 | Zustand persist middleware | authStore.ts |
| TD-003 | Soft-delete check + JWT cache | JwtAuthenticationFilter, UserRepository |
| TD-004 | JWT_SECRET validation | JwtUtil, application-dev.yml, application-test.yml |
| TD-005 | Rate limiting /login | RateLimitingFilter, SecurityConfig |
| TD-006 | Tests AuthService + JwtUtil | AuthServiceTest (5), JwtUtilTest (6) |
| TD-007 | Documentacion actualizada | CHANGELOG, PROJECT_STATUS, sprint-02.1 |

### Veredicto
Completado. Base estabilizada para modulos de negocio.

---

## Sprint 03: Contactos

| Campo | Valor |
|-------|-------|
| **Estado** | Completado |
| **Fecha** | 2026-07-29 |
| **Puntos** | 15 |
| **Version** | v0.3.0 |

### Objetivo
CRUD completo de contactos (duenos de mascotas) con paginacion, busqueda, multi-tenant y UI funcional.

### Entregables
- Backend: Contact entity, repository, service, controller, DTOs, mapper, exceptions
- Backend: Flyway V2 migration (contacts table)
- Backend: 10 tests unitarios ContactServiceTest
- Frontend: ContactListPage (tabla + busqueda + paginacion)
- Frontend: ContactFormPage (crear/editar con validacion)
- Frontend: ContactDeleteModal (confirmacion)
- Frontend: UI components (Input, Table, Pagination, Spinner, Badge, ConfirmDialog)
- Frontend: Vitest + RTL configurados
- API: Endpoints CRUD con filtro multi-tenant por companyId

### Historias de Usuario
| ID | Historia | Puntos | Estado |
|----|----------|--------|--------|
| US-003 | Crear Contacto | 5 | Completado |
| US-004 | Listar Contactos | 5 | Completado |
| US-005 | Editar Contacto | 3 | Completado |
| US-006 | Eliminar Contacto (soft delete) | 2 | Completado |

### Pruebas de Integracion
| Prueba | Resultado |
|--------|-----------|
| Health endpoint | PASS |
| Register + Login | PASS |
| Create contact | PASS |
| List + Search | PASS |
| Update contact | PASS |
| Delete contact (soft) | PASS |
| Multi-tenant isolation | PASS (Company 1 no ve datos de Company 2) |
| Cross-tenant access | PASS (404) |
| Rate limiting | PASS (429) |

### Veredicto
**STABLE**. Modulo de contactos funcional con aislamiento multi-tenant verificado. 22 tests backend + 6 tests frontend pasando.

---

## Sprint 04: Mascotas

| Campo | Valor |
|-------|-------|
| **Estado** | Completado |
| **Fecha** | 2026-07-29 |
| **Puntos** | 8 |
| **Version** | v0.4.0 |

### Objetivo
CRUD completo de mascotas vinculadas a contactos con aislamiento multi-tenant y UI funcional.

### Entregables
- Backend: Pet entity, repository, service, controller, DTOs, mapper, exceptions
- Backend: Flyway V3 migration (pets table)
- Backend: 13 tests unitarios PetServiceTest
- Frontend: PetListPage (tabla + busqueda + paginacion)
- Frontend: PetFormPage (crear/editar con selector de contacto)
- Frontend: PetDeleteModal (confirmacion)
- API: 6 endpoints CRUD con filtro multi-tenant

### Historias de Usuario
| ID | Historia | Puntos | Estado |
|----|----------|--------|--------|
| US-007 | Registrar Mascota | 5 | Completado |
| US-008 | Ver Mascotas del Contacto | 3 | Completado |

### Metricas Finales
| Metrica | Valor |
|---------|-------|
| Tests backend | 35/35 PASS |
| PetService tests | 13/13 PASS |
| Frontend lint | 0 errors |
| Frontend typecheck | PASS |
| Frontend build | PASS (128 modules) |

### Veredicto
**STABLE**. Modulo de Mascotas completo con CRUD funcional, multi-tenant verificado, y vinculo contacto-mascota implementado.

---

## Sprint 05: Citas

| Campo | Valor |
|-------|-------|
| **Estado** | Completado |
| **Fecha** | 2026-07-29 |
| **Puntos** | 19 |
| **Version** | v0.5.0 |

### Objetivo
CRUD completo de citas con calendario visual, estados de cita, validacion de conflictos de horario, y aislamiento multi-tenant.

### Entregables
- Backend: Appointment entity, AppointmentStatus enum, repository, service, controller, DTOs, mapper, exceptions
- Backend: Flyway V4 migration (appointments table)
- Backend: 13 tests unitarios AppointmentServiceTest
- Backend: AppointmentConflictException handler en GlobalExceptionHandler
- Frontend: AppointmentCalendar con react-big-calendar (mes/semana/dia)
- Frontend: AppointmentFormModal (crear/editar)
- Frontend: AppointmentDetailModal (detalle + acciones de estado)
- Frontend: appointmentStore (Zustand) con manejo de conflictos
- API: 7 endpoints CRUD con conflict detection

### Historias de Usuario
| ID | Historia | Puntos | Estado |
|----|----------|--------|--------|
| US-009 | Crear Cita | 8 | Completado |
| US-010 | Ver Calendario | 8 | Completado |
| US-011 | Editar/Cancelar Cita | 3 | Completado |

### Funcionalidades Implementadas
- Calendario visual con 3 vistas (mes, semana, dia)
- Navegacion prev/next/today
- Colores por estado (SCHEDULED=azul, COMPLETED=verde, CANCELLED=rojo)
- Validacion de conflictos de horario por mascota
- Estados: SCHEDULED, COMPLETED, CANCELLED
- No edicion de citas completadas/canceladas
- Localizacion en espanol

### Metricas Finales
| Metrica | Valor |
|---------|-------|
| Tests backend | 48/48 PASS |
| AppointmentService tests | 13/13 PASS |
| Frontend lint | 0 errors |
| Frontend typecheck | PASS |
| Frontend build | PASS (1528 modules) |

### Veredicto
**STABLE**. Modulo de Citas completo con calendario visual funcional, estados de cita, conflict detection, y multi-tenant verificado.

---

## Sprint 06: Dashboard

| Campo | Valor |
|-------|-------|
| **Estado** | Completado |
| **Fecha** | 2026-07-30 |
| **Puntos** | 5 |
| **Version** | v0.6.0 |

### Objetivo
Dashboard con resumen ejecutivo del negocio: metricas clave, citas del dia, accesos rapidos, y pulido final de UI/UX.

### Entregables
- Backend: DashboardResponseDTO, DashboardService, DashboardController (GET /api/v1/dashboard)
- Backend: 4 tests unitarios DashboardServiceTest
- Backend: Repositories actualizados (Contact, Pet, Appointment)
- Frontend: DashboardPage con layout profesional
- Frontend: StatCard (reutilizable), TodayAppointments, QuickActions
- Frontend: Skeleton loading, empty states, grafico de especies
- Frontend: Dashboard integrado en App.tsx y Sidebar

### Historias de Usuario
| ID | Historia | Puntos | Estado |
|----|----------|--------|--------|
| US-012 | Ver Dashboard | 5 | Completado |

### Funcionalidades Implementadas
- StatCards: Clientes, Mascotas, Citas Hoy, Citas Semana
- Citas del dia con badges de estado
- Estado de citas: Programadas, Completadas, Canceladas
- Mascotas por especie (grafico de barras)
- Accesos rapidos: Nueva Cita, Nuevo Cliente, Nueva Mascota
- Skeleton loading animations
- Empty states para datos vacios
- Responsive (desktop + tablet)

### Metricas Finales
| Metrica | Valor |
|---------|-------|
| Tests backend | 52/52 PASS |
| DashboardService tests | 4/4 PASS |
| Frontend lint | 0 errors |
| Frontend typecheck | PASS |
| Frontend build | PASS (1534 modules) |
| API endpoints | 23 total |

### Veredicto
**STABLE**. Dashboard completo con metricas, citas del dia, distribucion por especie, y UI profesional. MVP al 100%.

---

*Sprint History - Syncria*
*Ultima actualizacion: 2026-07-30*
