# PROJECT_STATUS.md - Syncria

## Estado Actual

| Campo | Valor |
|-------|-------|
| **Version** | v1.4.0 |
| **Fecha** | 2026-08-23 |
| **Sprint Actual** | Etapa 5 — Deploy — **EN PROCESO** |
| **Estado** | **DEPLOYING** |
| **Ready for** | Portfolio, Interviews, Freelancing, Deployment |

---

## Sprint Status

| Sprint | Version | Estado |
|--------|---------|--------|
| Sprint 00-06 | v0.1.0 - v0.6.0 | Completado |
| Sprint 06.1 | v1.0.0 | Completado |
| Etapa 1 Production Hardening | v1.1.0 | Completado |
| Etapa 2 Environment & Config | v1.2.0 | Completado |
| Etapa 3 Security Hardening | v1.3.0 | Completado |
| Etapa 4 CI/CD | v1.4.0 | Completado |
| Etapa 5 Deploy | — | **EN PROCESO** |

---

## Historial de Sprints

| Sprint | Nombre | Estado | Version | Puntos |
|--------|--------|--------|---------|--------|
| Sprint 00 | Product Discovery | Completado | — | — |
| Sprint 01 | Setup e Infraestructura | Completado | v0.1.0 | 8 |
| Sprint 02 | Autenticacion | Completado | v0.2.0 | 8 |
| Sprint 02.1 | Hardening | Completado | v0.2.1 | ~12 |
| Sprint 03 | Contactos | Completado | v0.3.0 | 15 |
| Sprint 04 | Mascotas | Completado | v0.4.0 | 8 |
| Sprint 05 | Citas | Completado | v0.5.0 | 19 |
| Sprint 06 | Dashboard | Completado | v0.6.0 | 5 |
| Sprint 06.1 | Hardening Final | Completado | v1.0.0 | — |
| Etapa 1 | JWT httpOnly Cookies | Completado | v1.1.0 | 8 |
| Etapa 2 | Environment & Config | Completado | v1.2.0 | 6 |
| Etapa 3 | Security Hardening | Completado | v1.3.0 | 10 |
| Etapa 4 | CI/CD | **COMPLETADO** | **v1.4.0** | **5** |

---

## Estado por Modulo

| Modulo | Estado | Sprint | Detalle |
|--------|--------|--------|---------|
| Infraestructura | Completado | Sprint 01 | Git, Docker, backend, frontend |
| Autenticacion | Completado | Sprint 02 | Register, login, JWT, protected routes |
| Hardening | Completado | Sprint 02.1 | Tests, security fixes, companyId, rate limiting |
| Contactos | Completado | Sprint 03 | CRUD completo con paginacion, busqueda, multi-tenant |
| Mascotas | Completado | Sprint 04 | CRUD mascotas vinculadas a contactos, multi-tenant |
| Citas | Completado | Sprint 05 | Calendario visual, estados, conflict detection |
| Dashboard | **COMPLETADO** | Sprint 06 | Resumen y metricas |

---

## Stack Tecnologico

| Capa | Tecnologia | Version |
|------|------------|---------|
| Backend | Java | 17 |
| Backend | Spring Boot | 3.5.4 |
| Backend | springdoc-openapi | 2.8.6 |
| Frontend | React | 18.3.1 |
| Frontend | TypeScript | 5.4.5 |
| Frontend | Vite | 5.3.1 |
| Frontend | TailwindCSS | 3.4.4 |
| Frontend | react-big-calendar | latest |
| Frontend | date-fns | latest |
| Base de datos | PostgreSQL | 18.6 |
| Migraciones | Flyway | (via Spring Boot) |
| Estado global | Zustand | 4.5.2 |
| Seguridad | Spring Security + JWT | jjwt 0.12.6 |
| API Docs | springdoc-openapi | 2.8.6 |

---

## Arquitectura Backend

```
com.syncria/
├── config/               # DatabaseConfig
├── module/
│   ├── auth/             # AuthController, AuthService, DTOs, exceptions
│   ├── user/             # User entity, UserRepository, UserService
│   ├── company/          # Company entity, CompanyRepository
│   ├── contact/          # ContactController, ContactService, DTOs, mapper, exceptions
│   ├── pet/              # PetController, PetService, DTOs, mapper, exceptions
│   ├── appointment/      # AppointmentController, AppointmentService, DTOs, mapper, exceptions
│   ├── dashboard/        # DashboardController, DashboardService, DTO (Sprint 06)
│   └── health/           # HealthController
├── security/
│   ├── config/           # SecurityConfig, JwtAuthenticationFilter
│   ├── filter/           # RateLimitingFilter
│   └── util/             # JwtUtil
└── shared/
    ├── entity/           # BaseEntity
    └── exception/        # GlobalExceptionHandler, NotFoundException
```

## Arquitectura Frontend

```
frontend/src/
├── features/auth/            # LoginPage, RegisterPage, ProtectedRoute, authStore
├── features/contacts/        # ContactListPage, ContactFormPage, contactStore, types
├── features/pets/            # PetListPage, PetFormPage, petStore, types
├── features/appointments/    # AppointmentCalendar, FormModal, DetailModal, store, types
├── features/dashboard/       # DashboardPage, StatCard, store, types (Sprint 06)
├── components/layout/        # Header, Sidebar
├── components/ui/            # Button, Input, Table, Pagination, Spinner, Badge, ConfirmDialog
├── layouts/                  # MainLayout
├── lib/                      # api.ts (Axios + JWT interceptor)
├── pages/                    # Home
└── types/                    # Tipos compartidos
```

## API Endpoints (23 total)

| Metodo | Ruta | Auth | Descripcion |
|--------|------|------|-------------|
| GET | /api/v1/health | No | Health check |
| POST | /api/v1/auth/register | No | Registro (auto-crea empresa) |
| POST | /api/v1/auth/login | No | Login (rate limited: 5/min/IP) |
| GET | /api/v1/auth/me | Si | Usuario actual |
| GET | /api/v1/contacts | Si | Listar contactos (paginado + busqueda) |
| GET | /api/v1/contacts/{id} | Si | Detalle contacto |
| POST | /api/v1/contacts | Si | Crear contacto (companyId del JWT) |
| PUT | /api/v1/contacts/{id} | Si | Editar contacto |
| DELETE | /api/v1/contacts/{id} | Si | Eliminar contacto (soft delete) |
| GET | /api/v1/pets | Si | Listar mascotas (paginado + busqueda) |
| GET | /api/v1/pets/{id} | Si | Detalle mascota |
| GET | /api/v1/pets/contact/{contactId} | Si | Mascotas de un contacto |
| POST | /api/v1/pets | Si | Crear mascota |
| PUT | /api/v1/pets/{id} | Si | Editar mascota |
| DELETE | /api/v1/pets/{id} | Si | Eliminar mascota (soft delete) |
| GET | /api/v1/appointments | Si | Listar citas (paginado + filtros) |
| GET | /api/v1/appointments/{id} | Si | Detalle cita |
| GET | /api/v1/appointments/range | Si | Citas por rango de fechas |
| POST | /api/v1/appointments | Si | Crear cita (con conflict detection) |
| PUT | /api/v1/appointments/{id} | Si | Editar cita |
| PUT | /api/v1/appointments/{id}/status | Si | Cambiar estado de cita |
| DELETE | /api/v1/appointments/{id} | Si | Eliminar cita (soft delete) |
| GET | /api/v1/dashboard | Si | Resumen ejecutivo del negocio |

---

## Metricas de Calidad

| Metrica | Valor |
|---------|-------|
| Tests backend | 68/68 PASS |
| Tests frontend | 43/43 PASS |
| Frontend lint | 0 errors |
| Frontend typecheck | PASS |
| Frontend build | PASS (1533 modules, 473KB gzip: 148KB) |
| Backend build | PASS (syncria-backend-1.3.0.jar) |
| PostgreSQL | 18.6 nativo (12 constraints, 19 indexes) |
| Flyway migrations | V1-V4 aplicadas |
| Swagger/OpenAPI | Integrado (springdoc-openapi 2.8.6) |
| npm vulnerabilities | 4 moderadas (requieren breaking changes) |
| Rate limiting | Generalizado (auth: 5, CRUD: 60, dashboard: 30 req/min/IP) |
| Rate limiting por email | Implementado (5 intentos fallidos por email) |
| CSP headers | Implementado (default-src 'self', frame-ancestors 'none') |
| Security logging | Implementado (tokens inválidos, 401/403, rate limits) |
| Cookie domain | Configurable via COOKIE_DOMAIN env var |
| Soft-delete check | Verificado en JWT filter |
| JWT cache | TTL 5 minutos |
| Session persistence | Zustand persist middleware |
| JWT_SECRET validation | Minimo 256 bits en startup |
| Multi-tenant contactos | Aislado por companyId (verificado) |
| Multi-tenant mascotas | Aislado por companyId (verificado) |
| Multi-tenant citas | Aislado por companyId (verificado) |
| Conflict detection | Implementado por mascota + horario |
| Calendar views | Mes, Semana, Dia (react-big-calendar) |

---

## Deferred Items (Sprint 07+)

### Sprint 07 — Deploy + Beta (Prioridad Media)

| Item | Prioridad | Descripcion |
|------|-----------|-------------|
| Deploy backend | HIGH | Railway o Render con PostgreSQL real |
| Deploy frontend | HIGH | Vercel o Netlify |
| CI/CD | MEDIUM | GitHub Actions para testing automatico |
| Dominio + HTTPS | MEDIUM | Configurar DNS + SSL |
| Beta testing | MEDIUM | Reclutar 10-20 veterinarios |

### Sprint 08 — Produccion (Prioridad Baja)

| Item | Prioridad | Descripcion |
|------|-----------|-------------|
| Sentry | MEDIUM | Error tracking |
| Monitoring | LOW | Uptime + metrics |
| Rate limiting global | LOW | No solo en /login |
| Landing page | LOW | Pagina de marketing |

---

## Conocido / Limitaciones

- 4 vulnerabilidades npm moderadas (requieren Vite 8 / react-router-dom 7)
- Docker Desktop CLI instalado pero daemon no corriendo
- PostgreSQL 18.6 nativo (Flyway warning: max soportado 17, funciona OK)
- Sin CI/CD pipeline
- Sin observabilidad (Sentry, monitoring)

---

## Proximos Pasos

1. **Sprint 07 (Deploy)**: Deploy backend (Railway/Render) + frontend (Vercel/Netlify), CI/CD con GitHub Actions
2. **Sprint 08 (Beta)**: Beta testing con veterinarios, feedback, iteracion
3. **Sprint 09 (Launch)**: Produccion publica, dominio, HTTPS, observabilidad

---

*PROJECT_STATUS.md - Syncria v1.3.0*
*Estado: PRODUCTION READY*
*READY FOR PORTFOLIO*
*READY FOR INTERVIEWS*
*READY FOR FREELANCING*
*Ultima actualizacion: 2026-08-23*
