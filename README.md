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

- **Autenticacion segura**: JWT en httpOnly cookies, rate limiting, BCrypt
- **CRUD Contactos**: Paginacion, busqueda en tiempo real, multi-tenant
- **CRUD Mascotas**: Vinculadas a contactos, busqueda por especie/raza
- **CRUD Citas**: Calendario visual (mes/semana/dia), conflict detection, estados
- **Dashboard**: Metrics ejecutivas, citas del dia, distribucion por especie
- **Multi-tenant**: Aislamiento total de datos por empresa
- **UI profesional**: Skeleton loading, empty states, responsive
- **Security Hardening**: CSP, security headers, rate limiting generalizado

---

## Estado Actual del Proyecto

### Version: v1.3.0

| Metrica | Estado |
|---------|--------|
| Backend tests | 68/68 PASS |
| Frontend tests | 43/43 PASS |
| Frontend lint | 0 errors |
| Frontend typecheck | PASS |
| Frontend build | PASS |
| PostgreSQL local | Funcionando (127.0.0.1:5432) |
| Security Score | 8.5/10 |

### Etapas Completadas

| Etapa | Version | Descripcion | Estado |
|-------|---------|-------------|--------|
| Sprint 01 | v0.1.0 | Setup e Infraestructura | Completado |
| Sprint 02 | v0.2.0 | Autenticacion (JWT) | Completado |
| Sprint 02.1 | v0.2.1 | Hardening (security, tests) | Completado |
| Sprint 03 | v0.3.0 | Modulo de Contactos | Completado |
| Sprint 04 | v0.4.0 | Modulo de Mascotas | Completado |
| Sprint 05 | v0.5.0 | Modulo de Citas | Completado |
| Sprint 06 | v0.6.0 | Dashboard | Completado |
| Sprint 06.1 | v1.0.0 | Hardening Final | Completado |
| Etapa 1 | v1.1.0 | JWT httpOnly Cookies | Completado |
| Etapa 2 | v1.2.0 | Environment & Config | Completado |
| Etapa 3 | v1.3.0 | Security Hardening | **Completado** |

### Etapas Pendientes

| Etapa | Prioridad | Descripcion | Dependencias |
|-------|-----------|-------------|--------------|
| **Etapa 4** | Alta | CI/CD (GitHub Actions) | Ninguna |
| **Etapa 5** | Alta | Deploy (Railway/Render) | Etapa 4 |
| **Etapa 6** | Media | Beta testing | Etapa 5 |
| **Etapa 7** | Baja | Portfolio/SaaS | Etapa 6 |

---

## Arquitectura

```
Syncria
├── backend/                     # Spring Boot 3.5.4 + Java 17
│   ├── module/
│   │   ├── auth/                # Register, Login, JWT, Cookies
│   │   ├── contact/             # CRUD Contactos
│   │   ├── pet/                 # CRUD Mascotas
│   │   ├── appointment/         # CRUD Citas + Calendario
│   │   ├── dashboard/           # Metrics ejecutivas
│   │   ├── company/             # Multi-tenant
│   │   └── user/                # Usuarios
│   ├── security/                # JWT, Filtros, Rate Limiting, CSP
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

## Seguridad (v1.3.0)

### Autenticacion
- JWT en httpOnly cookies (no localStorage)
- BCrypt para passwords
- Expiracion 24 horas
- Validacion de JWT_SECRET en startup (min 256 bits)

### Rate Limiting

| Endpoint | Limite | Ventana |
|----------|--------|---------|
| Auth (login, register) | 5 req/min/IP | 60s |
| CRUD (contacts, pets, appointments) | 60 req/min/IP | 60s |
| Dashboard | 30 req/min/IP | 60s |
| Login por email | 5 intentos fallidos | 60s |

### Security Headers

| Header | Valor |
|--------|-------|
| X-Content-Type-Options | nosniff |
| X-Frame-Options | DENY |
| X-XSS-Protection | 1; mode=block |
| Strict-Transport-Security | max-age=31536000 |
| Content-Security-Policy | default-src 'self' |
| Referrer-Policy | strict-origin-when-cross-origin |

### Cookie Configuration

```yaml
app:
  jwt:
    cookie-domain: ${COOKIE_DOMAIN:}  # Vacio en dev, configurable en prod
    cookie-secure: true                # Solo HTTPS en prod
    cookie-max-age: 86400              # 24 horas
```

---

## Instalacion

### Requisitos Previos

- Java 17+ (JDK)
- Node.js 20+
- PostgreSQL 16+ (o Docker)
- Docker + Docker Compose (opcional)

### Paso 1: Clonar el repositorio

```bash
git clone https://github.com/Chanok18/syncria_1.git
cd syncria_1
```

### Paso 2: Base de datos

```bash
# Opcion A: PostgreSQL local (recomendado)
# Crear base de datos syncria_db con usuario syncria_user

# Opcion B: Docker
docker-compose up -d postgres pgadmin
```

### Paso 3: Backend

```bash
cd backend

# Windows
mvnw.cmd spring-boot:run

# Unix/Mac
./mvnw spring-boot:run
```

**Puerto**: 8080

### Paso 4: Frontend

```bash
cd frontend
npm install
npm run dev
```

**Puerto**: 5173 (proxy automatico al backend en 8080)

### Paso 5: Crear usuario

1. Abrir `http://localhost:5173/register`
2. Completar el formulario
3. Login automatico

---

## Variables de Entorno

Copiar `.env.example` a `.env` y configurar:

```bash
# Database
DATABASE_URL=jdbc:postgresql://127.0.0.1:5432/syncria_db
DATABASE_USERNAME=syncria_user
DATABASE_PASSWORD=secret

# JWT (minimo 256 bits, Base64)
JWT_SECRET=tu-clave-secreta-aqui

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:5173

# Cookie Domain (vacio en dev)
COOKIE_DOMAIN=

# pgAdmin
PGADMIN_DEFAULT_EMAIL=admin@syncria.com
PGADMIN_DEFAULT_PASSWORD=admin
```

**IMPORTANTE**: Usamos `127.0.0.1` en lugar de `localhost` porque en esta maquina localhost resolvia a IPv6 (::1) y provocaba problemas de autenticacion con PostgreSQL.

---

## API Endpoints (24 total)

| Metodo | Ruta | Auth | Rate Limit | Descripcion |
|--------|------|------|------------|-------------|
| GET | /api/v1/health | No | No | Health check |
| POST | /api/v1/auth/register | No | 5/min/IP | Registro de usuario |
| POST | /api/v1/auth/login | No | 5/min/IP + 5/email | Login |
| POST | /api/v1/auth/logout | Si | No | Cerrar sesion |
| GET | /api/v1/auth/me | Si | No | Usuario actual |
| GET | /api/v1/contacts | Si | 60/min/IP | Listar contactos |
| GET | /api/v1/contacts/{id} | Si | 60/min/IP | Detalle contacto |
| POST | /api/v1/contacts | Si | 60/min/IP | Crear contacto |
| PUT | /api/v1/contacts/{id} | Si | 60/min/IP | Editar contacto |
| DELETE | /api/v1/contacts/{id} | Si | 60/min/IP | Eliminar contacto |
| GET | /api/v1/pets | Si | 60/min/IP | Listar mascotas |
| GET | /api/v1/pets/{id} | Si | 60/min/IP | Detalle mascota |
| GET | /api/v1/pets/contact/{id} | Si | 60/min/IP | Mascotas por contacto |
| POST | /api/v1/pets | Si | 60/min/IP | Crear mascota |
| PUT | /api/v1/pets/{id} | Si | 60/min/IP | Editar mascota |
| DELETE | /api/v1/pets/{id} | Si | 60/min/IP | Eliminar mascota |
| GET | /api/v1/appointments | Si | 60/min/IP | Listar citas |
| GET | /api/v1/appointments/{id} | Si | 60/min/IP | Detalle cita |
| GET | /api/v1/appointments/range | Si | 60/min/IP | Citas por rango |
| POST | /api/v1/appointments | Si | 60/min/IP | Crear cita |
| PUT | /api/v1/appointments/{id} | Si | 60/min/IP | Editar cita |
| PUT | /api/v1/appointments/{id}/status | Si | 60/min/IP | Cambiar estado |
| DELETE | /api/v1/appointments/{id} | Si | 60/min/IP | Eliminar cita |
| GET | /api/v1/dashboard | Si | 30/min/IP | Dashboard ejecutivo |

---

## Comandos Utiles

```bash
# Backend
cd backend
./mvnw spring-boot:run          # Ejecutar
./mvnw test                     # Todos los tests (68)
./mvnw compile                  # Compilar
./mvnw clean package            # Build completo

# Frontend
cd frontend
npm run dev                     # Desarrollo
npm run build                   # Build produccion
npm run lint                    # Linting
npm run typecheck               # Verificar tipos
npm run test                    # Tests (43)

# Docker
docker-compose up -d            # Levantar servicios
docker-compose down             # Parar servicios
docker-compose logs -f backend  # Logs del backend
```

---

## Testing

### Backend (68 tests)

| Modulo | Tests | Estado |
|--------|-------|--------|
| DashboardService | 4 | PASS |
| AppointmentService | 13 | PASS |
| PetService | 13 | PASS |
| ContactService | 10 | PASS |
| AuthService | 5 | PASS |
| JwtUtil | 6 | PASS |
| RateLimitingFilter | 10 | PASS |
| SecurityHeaders | 6 | PASS |
| Context load | 1 | PASS |
| **Total** | **68** | **ALL PASS** |

### Frontend (43 tests)

| Componente | Tests | Estado |
|------------|-------|--------|
| Button | 6 | PASS |
| ContactStore | ~10 | PASS |
| PetStore | ~10 | PASS |
| AppointmentStore | ~10 | PASS |
| DashboardStore | ~7 | PASS |

---

## Roadmap

| Etapa | Version | Contenido | Estado |
|-------|---------|-----------|--------|
| Sprint 01 | v0.1.0 | Setup e Infraestructura | Completado |
| Sprint 02 | v0.2.0 | Autenticacion (JWT) | Completado |
| Sprint 02.1 | v0.2.1 | Hardening (security, tests) | Completado |
| Sprint 03 | v0.3.0 | Modulo de Contactos | Completado |
| Sprint 04 | v0.4.0 | Modulo de Mascotas | Completado |
| Sprint 05 | v0.5.0 | Modulo de Citas | Completado |
| Sprint 06 | v0.6.0 | Dashboard | Completado |
| Sprint 06.1 | v1.0.0 | Hardening Final | Completado |
| Etapa 1 | v1.1.0 | JWT httpOnly Cookies | Completado |
| Etapa 2 | v1.2.0 | Environment & Config | Completado |
| Etapa 3 | v1.3.0 | Security Hardening | Completado |
| **Etapa 4** | **v1.4.0** | **CI/CD (GitHub Actions)** | **Completado** |
| **Etapa 5** | — | Deploy (Railway/Render) | **Pendiente** |
| **Etapa 6** | — | Beta testing | **Pendiente** |
| **Etapa 7** | — | Portfolio/SaaS | **Pendiente** |

---

## Proximo: Etapa 5 — Deploy

### Tareas Pendientes

| # | Tarea | Descripcion | Prioridad |
|---|-------|-------------|-----------|
| 1 | **Deploy backend** | Railway o Render con PostgreSQL real | Alta |
| 2 | **Deploy frontend** | Vercel o Netlify | Alta |
| 3 | **Dominio personalizado** | Configurar DNS + HTTPS | Media |
| 4 | **Variables de entorno prod** | Configurar en plataforma de deploy | Alta |
| 5 | **Verificar deploy** | Probar funcionalidad completa en produccion | Alta |

### Configuracion Requerida

```yaml
# Variables de entorno para produccion
DATABASE_URL=jdbc:postgresql://...
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
JWT_SECRET=...
CORS_ALLOWED_ORIGINS=https://tudominio.com
COOKIE_DOMAIN=tudominio.com
```

---

## Conocido / Limitaciones

- Tests corren con H2 in-memory (no PostgreSQL real en CI)
- Sin CI/CD pipeline
- Sin observabilidad (Sentry, monitoring)
- CSP con 'unsafe-inline' para estilos (TailwindCSS lo requiere)
- Sin JWT blacklist/revocation (Redis)

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
- [docs/EXECUTION_REPORT_SPRINT_03_SECURITY.md](docs/EXECUTION_REPORT_SPRINT_03_SECURITY.md) — Reporte Etapa 3
- [FINAL_AUDIT.md](FINAL_AUDIT.md) — Auditoria final de seguridad
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

## Configuracion Local Importante

### PostgreSQL

- **Host**: `127.0.0.1` (NO `localhost` por problemas IPv6)
- **Port**: `5432`
- **Database**: `syncria_db`
- **User**: `syncria_user`
- **Password**: `secret` (dev)

### JWT

- **Secret**: Minimo 256 bits en Base64
- **Generar**: `openssl rand -base64 32`
- **Expiracion**: 24 horas

### Cookies

- **httpOnly**: true (no accesible via JavaScript)
- **secure**: false en dev, true en prod
- **sameSite**: Lax
- **domain**: vacio en dev, configurable en prod

---

## Licencia

Este proyecto esta bajo la licencia MIT. Ver [LICENSE](LICENSE) para mas detalles.

---

<div align="center">

**Syncria v1.4.0** — CI/CD COMPLETED

Desarrollado con pasion para la industria veterinaria

</div>
