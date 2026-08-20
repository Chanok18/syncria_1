# Syncria

<div align="center">

**Plataforma CRM inteligente y modular para clinicas veterinarias**

[![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square&logo=openjdk)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.4-6DB33F?style=flat-square&logo=springboot)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.3-61DAFB?style=flat-square&logo=react)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.4-3178C6?style=flat-square&logo=typescript)](https://www.typescriptlang.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat-square)](./LICENSE)

</div>

---

## Descripcion

Syncria es una plataforma CRM completa disenada para clinicas veterinarias. Permite gestionar clientes (duenos de mascotas), mascotas, citas con calendario visual, y un dashboard ejecutivo con metricas del negocio.

Construida con arquitectura modular por capas, preparada para evolucionar a SaaS multi-tenant.

### Funcionalidades Clave

- **Autenticacion segura**: JWT con expiracion, rate limiting, BCrypt
- **CRUD Contactos**: Paginacion, busqueda en tiempo real, multi-tenant
- **CRUD Mascotas**: Vinculadas a contactos, busqueda por especie/raza
- **CRUD Citas**: Calendario visual (mes/semana/dia), conflict detection, estados
- **Dashboard**: Metrics ejecutivas, citas del dia, distribucion por especie
- **Multi-tenant**: Aislamiento total de datos por empresa
- **UI profesional**: Skeleton loading, empty states, responsive

---

## Arquitectura

```
Syncria
├── backend/                     # Spring Boot 3.5.4 + Java 17
│   ├── module/
│   │   ├── auth/                # Register, Login, JWT
│   │   ├── contact/             # CRUD Contactos
│   │   ├── pet/                 # CRUD Mascotas
│   │   ├── appointment/         # CRUD Citas + Calendario
│   │   ├── dashboard/           # Metrics ejecutivas
│   │   ├── company/             # Multi-tenant
│   │   └── user/                # Usuarios
│   ├── security/                # JWT, Filtros, Rate Limiting
│   └── shared/                  # Excepciones globales, BaseEntity
├── frontend/                    # React 18 + TypeScript + Vite
│   └── src/features/
│       ├── auth/                # Login, Register, ProtectedRoute
│       ├── contacts/            # Lista, Formulario, Store
│       ├── pets/                # Lista, Formulario, Store
│       ├── appointments/        # Calendario, Modales, Store
│       └── dashboard/           # DashboardPage, StatCard, Store
├── docs/                        # Documentacion completa
└── docker-compose.yml           # PostgreSQL, pgAdmin, n8n
```

Para mas detalles, ver [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md).

---

## Stack Tecnologico

| Capa | Tecnologia | Version |
|------|------------|---------|
| Backend | Java | 17 |
| Backend | Spring Boot | 3.5.4 |
| Backend | Spring Security + JWT | jjwt 0.12.6 |
| Backend | MapStruct | 1.5.5 |
| Backend | Lombok | (via Spring Boot) |
| Frontend | React | 18.3.1 |
| Frontend | TypeScript | 5.4.5 |
| Frontend | Vite | 5.3.1 |
| Frontend | TailwindCSS | 3.4.4 |
| Frontend | Zustand | 4.5.2 |
| Frontend | react-big-calendar | 1.20.0 |
| Frontend | date-fns | 4.4.0 |
| Frontend | Axios | 1.7.2 |
| Base de datos | PostgreSQL | 16 |
| Migraciones | Flyway | (via Spring Boot) |
| Testing | JUnit 5 + Mockito | (via Spring Boot) |
| Testing | Vitest + RTL | 4.1.10 |
| Contenedores | Docker Compose | 3.8 |

---

## Capturas de Pantalla

### Dashboard
Vista ejecutiva con metricas del negocio, citas del dia, y accesos rapidos.

### Calendario de Citas
Calendario visual con 3 vistas (mes, semana, dia), colores por estado, y conflict detection.

### Gestion de Contactos
Tabla con paginacion, busqueda en tiempo real, y CRUD completo.

### Gestion de Mascotas
Mascotas vinculadas a contactos, busqueda por especie/raza.

---

## Instalacion

### Requisitos Previos

- Java 17+ (JDK)
- Node.js 20+
- Docker + Docker Compose (opcional)

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/kevin-syncria/syncria.git
cd syncria
```

### Paso 2: Backend

```bash
cd backend

# Windows
mvnw.cmd spring-boot:run

# Unix/Mac
./mvnw spring-boot:run
```

**Puerto**: 8080

### Paso 3: Frontend

```bash
cd frontend
npm install
npm run dev
```

**Puerto**: 5173 (proxy automatico al backend en 8080)

### Paso 4: Base de datos (opcional)

```bash
docker-compose up -d postgres pgadmin
```

| Servicio | Puerto | URL |
|----------|--------|-----|
| PostgreSQL | 5432 | localhost:5432 |
| pgAdmin | 5050 | http://localhost:5050 |

Para mas detalles, ver [docs/INSTALLATION.md](docs/INSTALLATION.md).

---

## Variables de Entorno

Copiar `.env.example` a `.env` y configurar:

```bash
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/syncria_db
DATABASE_USERNAME=syncria_user
DATABASE_PASSWORD=secret

# JWT (minimo 256 bits)
JWT_SECRET=tu-clave-secreta-aqui

# pgAdmin
PGADMIN_DEFAULT_EMAIL=admin@syncria.com
PGADMIN_DEFAULT_PASSWORD=admin
```

---

## API Endpoints (23 total)

| Metodo | Ruta | Auth | Descripcion |
|--------|------|------|-------------|
| GET | /api/v1/health | No | Health check |
| POST | /api/v1/auth/register | No | Registro de usuario |
| POST | /api/v1/auth/login | No | Login (rate limited) |
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
| GET | /api/v1/dashboard | Si | Dashboard ejecutivo |

---

## Comandos Utiles

```bash
# Backend
cd backend
./mvnw spring-boot:run          # Ejecutar
./mvnw test                     # Todos los tests (52)
./mvnw compile                  # Compilar
./mvnw clean package            # Build completo

# Frontend
cd frontend
npm run dev                     # Desarrollo
npm run build                   # Build produccion
npm run lint                    # Linting
npm run typecheck               # Verificar tipos
npm run test                    # Tests (6)

# Docker
docker-compose up -d            # Levantar servicios
docker-compose down             # Parar servicios
docker-compose logs -f backend  # Logs del backend
```

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

---

## Roadmap

| Sprint | Version | Contenido | Estado |
|--------|---------|-----------|--------|
| Sprint 01 | v0.1.0 | Setup e Infraestructura | Completado |
| Sprint 02 | v0.2.0 | Autenticacion (JWT) | Completado |
| Sprint 02.1 | v0.2.1 | Hardening (security, tests) | Completado |
| Sprint 03 | v0.3.0 | Modulo de Contactos | Completado |
| Sprint 04 | v0.4.0 | Modulo de Mascotas | Completado |
| Sprint 05 | v0.5.0 | Modulo de Citas | Completado |
| Sprint 06 | v0.6.0 | Dashboard | Completado |
| Sprint 06.1 | v1.0.0 | Hardening Final | Completado |
| Sprint 07 | — | Beta Testing | Futuro |
| Sprint 08 | — | Production Launch | Futuro |

---

## Tareas Pendientes (20% Restante)

El MVP funcional esta al 100%, pero hay pendientes para llevar Syncria a produccion.

### Sprint 06.1 — Hardening (Prioridad Alta)

| # | Tarea | Descripcion |
|---|-------|-------------|
| 1 | **Docker funcional** | Instalar Docker Desktop, verificar PostgreSQL real |
| 2 | **Tests frontend** | Agregar tests para ContactStore, PetStore, AppointmentStore, DashboardStore |
| 3 | **Swagger/OpenAPI** | Integrar springdoc-openapi para documentacion automatica de API |
| 4 | **README profesional** | Capturas, badges, guia completa |
| 5 | **Token sync** | Sincronizar token entre api.ts y Zustand store |

### Sprint 07 — Deploy + Beta (Prioridad Media)

| # | Tarea | Descripcion |
|---|-------|-------------|
| 1 | **Deploy backend** | Railway o Render con PostgreSQL real |
| 2 | **Deploy frontend** | Vercel o Netlify |
| 3 | **Dominio personalizado** | Configurar DNS + HTTPS |
| 4 | **CI/CD** | GitHub Actions para testing automatico |
| 5 | **Beta testing** | Reclutar 10-20 veterinarios |

### Sprint 08 — Produccion (Prioridad Baja)

| # | Tarea | Descripcion |
|---|-------|-------------|
| 1 | **Sentry** | Error tracking en produccion |
| 2 | **Monitoring** | Uptime + metrics |
| 3 | **httpOnly cookies** | Migrar JWT de localStorage |
| 4 | **Rate limiting global** | No solo en /login |
| 5 | **Landing page** | Pagina de marketing |

### Conocido / Limitaciones

- Token JWT en localStorage (riesgo XSS aceptado para MVP)
- Frontend tests limitados (solo Button component)
- Sin Docker funcional (falta Docker Desktop en entorno actual)
- Tests corren con H2 in-memory (no PostgreSQL real)
- Sin CI/CD pipeline
- Sin observabilidad (logging, metrics, error tracking)

---

## Documentacion

- [PROJECT_STATUS.md](PROJECT_STATUS.md) — Estado actual del proyecto
- [CHANGELOG.md](CHANGELOG.md) — Historial de versiones
- [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) — Arquitectura del sistema
- [docs/INSTALLATION.md](docs/INSTALLATION.md) — Guia de instalacion detallada
- [docs/DEPLOYMENT.md](docs/DEPLOYMENT.md) — Guia de deployment
- [docs/CONTRIBUTING.md](docs/CONTRIBUTING.md) — Guia de contribucion
- [docs/roadmap.md](docs/roadmap.md) — Roadmap de desarrollo
- [docs/mvp.md](docs/mvp.md) — Definicion del MVP
- [docs/backlog.md](docs/backlog.md) — Product Backlog
- [docs/sprint-history.md](docs/sprint-history.md) — Historial de sprints
- [RELEASE_NOTES_v1.0.0.md](RELEASE_NOTES_v1.0.0.md) — Notas de la version 1.0.0

---

## Git Flow

```
main          <- produccion, merge solo desde develop
develop       <- integracion, base para features
feature/*     <- nuevas funcionalidades (feature/user-auth)
bugfix/*      <- correccion de bugs (bugfix/login-error)
```

---

## Licencia

Este proyecto esta bajo la licencia MIT. Ver [LICENSE](LICENSE) para mas detalles.

---

<div align="center">

**Syncria v1.0.0** — MVP COMPLETED

Desarrollado con pasion para la industria veterinaria

</div>
