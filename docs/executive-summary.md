# Executive Summary — Syncria

## Que es Syncria?

Syncria es una **plataforma CRM (Customer Relationship Management)** disenada originalmente para clinicas veterinarias, con arquitectura modular preparada para expandirse a multiples industrias (clinicas dentales, inmobiliarias, restaurantes, etc.).

Es un producto **SaaS (Software as a Service)** que busca reemplazar herramientas como Excel, WhatsApp y cuadernos para la gestion de clientes, mascotas, citas y comunicacion.

## Que problema resuelve?

Los veterinarios y profesionales de salud animal actualmente gestionan sus clientes de forma fragmentada:

- **Excel** para registros de clientes
- **WhatsApp** para comunicacion
- **Cuadernos** para citas
- **Memoria** para historiales

Esto provoca:
- Perdida de informacion
- Dificultad para encontrar clientes
- Citas perdidas o duplicadas
- Falta de metricas del negocio
- Experiencia de cliente inconsistente

Syncria propone una **solucion unificada** con interfaz intuitiva, buscador rapido, calendario visual y dashboard de metricas.

## Arquitectura Utilizada

### Stack

| Capa | Tecnologia |
|------|------------|
| Backend | Java 17 + Spring Boot 3.5.4 |
| Frontend | React 18 + TypeScript + Vite + TailwindCSS |
| Base de datos | PostgreSQL 16 |
| Seguridad | Spring Security + JWT |
| Estado | Zustand |
| Migraciones | Flyway |
| Deploy | Docker Compose |

### Patron Arquitectonico

**Modular por capas** — Cada modulo de negocio (auth, user, contact, pet, appointment) sigue la misma estructura:

```
module/{nombre}/
├── controller/    # REST endpoints
├── service/       # Logica de negocio
├── repository/    # Acceso a datos
├── entity/        # JPA entities
├── dto/           # Request/Response
└── exception/     # Excepciones del modulo
```

### Multi-tenant

Preparado para SaaS multi-tenant:
- Campo `company_id` en todas las entidades
- JWT incluye `companyId`
- Aislamiento por empresa en cada query
- Filtro JPA pendiente de implementar

## Funcionalidades Actuales (v0.2.0)

| Modulo | Funcionalidad | Estado |
|--------|---------------|--------|
| **Auth** | Registro de usuario | Completado |
| **Auth** | Login con JWT | Completado |
| **Auth** | Rutas protegidas | Completado |
| **Auth** | Logout | Completado |
| **Infra** | Docker (PostgreSQL, pgAdmin) | Completado |
| **Infra** | Backend Spring Boot | Completado |
| **Infra** | Frontend React | Completado |

### API Endpoints Disponibles

| Metodo | Ruta | Auth | Descripcion |
|--------|------|------|-------------|
| GET | /api/v1/health | No | Health check |
| POST | /api/v1/auth/register | No | Registro |
| POST | /api/v1/auth/login | No | Login |
| GET | /api/v1/auth/me | Si | Usuario actual |

## Funcionalidades Pendientes

| Modulo | Funcionalidad | Sprint |
|--------|---------------|--------|
| **Hardening** | Tests, security fixes, companyId | Sprint 02.1 |
| **Contactos** | CRUD completo con paginacion | Sprint 03 |
| **Mascotas** | Registro y gestion | Sprint 04 |
| **Citas** | Sistema de citas y calendario | Sprint 05 |
| **Dashboard** | Resumen y metricas | Sprint 06 |

## Estado del MVP

### Alcance del MVP

El MVP incluye 12 historias de usuario en 5 modulos:

| Modulo | Historias | Puntos | Estado |
|--------|-----------|--------|--------|
| Autenticacion | US-001, US-002 | 8 | Completado |
| Contactos | US-003 a US-006 | 15 | Pendiente |
| Mascotas | US-007, US-008 | 8 | Pendiente |
| Citas | US-009 a US-011 | 19 | Pendiente |
| Dashboard | US-012 | 5 | Pendiente |
| **Total** | **12 historias** | **55 pts** | **~20%** |

### Progreso del MVP

```
[████░░░░░░░░░░░░░░░░] ~20% completado
```

### Criterios de Exito del MVP

- 50 usuarios activos en primeros 3 meses
- 70% de retencion mensual
- NPS > 40
- Al menos 3 testimonios positivos

## Estado para SaaS

### Preparado

- [x] Arquitectura modular
- [x] Campo `company_id` en entidades
- [x] JWT con `companyId`
- [x] Backend stateless (listo para escalado)

### Pendiente

- [ ] Filtro JPA por defecto para `company_id`
- [ ] Asignacion dinamica de `companyId` al login
- [ ] Aislamiento real de datos por empresa
- [ ] Rate limiting por tenant
- [ ] Planes de pricing
- [ ] Portal de admin de tenants

### Timeline Estimada para SaaS

| Hito | Fecha Objetivo |
|------|----------------|
| MVP funcional | Semana 15 |
| Beta cerrada | Semana 19 |
| Launch publico | Semana 20 |
| Multi-tenant real | Q4 2026 |
| 500 usuarios | Q1 2027 |

---

*Executive Summary — Syncria v0.2.0*
*2026-07-27*
