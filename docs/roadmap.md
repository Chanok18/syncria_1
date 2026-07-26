# Roadmap de Desarrollo - Syncria

## Visión General

```
Fase 0: Product Discovery    ← ESTAMOS AQUÍ
Fase 1: System Architecture
Fase 2: MVP Development (Sprints 1-5)
Fase 3: Beta Testing
Fase 4: Launch
Fase 5: Growth & Iteration
```

## Calendario de Sprints

### Fase 0: Product Discovery (Semanas 1-2)
**Estado**: En Progreso

| Semana | Entregable | Estado |
|--------|------------|--------|
| 1 | Visión del producto | ✅ Completado |
| 1 | Definición MVP | ✅ Completado |
| 2 | Product Backlog | ✅ Completado |
| 2 | Historias de Usuario | ✅ Completado |
| 2 | Roadmap | 🔄 En Progreso |

### Fase 1: System Architecture (Semanas 3-4)
**Estado**: Pendiente

| Semana | Entregable | Estado |
|--------|------------|--------|
| 3 | Arquitectura general | ⏳ Pendiente |
| 3 | Diagramas de arquitectura | ⏳ Pendiente |
| 4 | ADRs (Architecture Decision Records) | ⏳ Pendiente |
| 4 | Modelo de dominio | ⏳ Pendiente |

### Fase 2: MVP Development (Semanas 5-14)

#### Sprint 1: Setup (Semanas 5-6)
**Objetivo**: Infraestructura base del proyecto

| Día | Tarea | Estimación |
|-----|-------|------------|
| 1-2 | Configurar repositorio Git | 2h |
| 1-2 | Configurar Git Flow | 1h |
| 3-4 | Docker Compose (PostgreSQL, pgAdmin) | 4h |
| 5-6 | Backend Spring Boot setup | 6h |
| 7-8 | Frontend React + Vite setup | 4h |
| 9-10 | Configurar linting, testing, cobertura | 4h |

**Entregables**:
- Repositorio con Git Flow
- Docker Compose funcionando
- Backend: Spring Boot + PostgreSQL + Flyway
- Frontend: React + TypeScript + Vite + Tailwind
- CI básico (lint, test, build)

**Puntos**: 8

---

#### Sprint 2: Clientes (Semanas 7-8)
**Objetivo**: CRUD completo de clientes

| Historia | Puntos | Días |
|----------|--------|------|
| US-003: Crear Cliente | 5 | 2.5 |
| US-004: Listar Clientes | 5 | 2.5 |
| US-005: Editar Cliente | 3 | 1.5 |
| US-006: Eliminar Cliente | 2 | 1 |
| Tests y fixes | - | 2 |

**Entregables**:
- API REST de clientes completa
- UI de clientes completa
- Tests unitarios y de integración
- Documentación de API

**Puntos**: 15

---

#### Sprint 3: Mascotas (Semanas 9-10)
**Objetivo**: Gestión de mascotas vinculadas a clientes

| Historia | Puntos | Días |
|----------|--------|------|
| US-007: Registrar Mascota | 5 | 2.5 |
| US-008: Ver Mascotas del Cliente | 3 | 1.5 |
| Tests y fixes | - | 2 |

**Entregables**:
- API REST de mascotas
- UI de mascotas integrada con clientes
- Upload de fotos
- Tests completos

**Puntos**: 8

---

#### Sprint 4: Citas (Semanas 11-12)
**Objetivo**: Sistema de citas y calendario

| Historia | Puntos | Días |
|----------|--------|------|
| US-009: Crear Cita | 8 | 4 |
| US-010: Ver Calendario | 8 | 4 |
| US-011: Editar/Cancelar Cita | 3 | 1.5 |
| Tests y fixes | - | 2.5 |

**Entregables**:
- API REST de citas
- Calendario visual interactivo
- Validación de conflictos
- Tests completos

**Puntos**: 19

---

#### Sprint 5: Dashboard y Pulido (Semanas 13-14)
**Objetivo**: Dashboard y preparación para beta

| Historia | Puntos | Días |
|----------|--------|------|
| US-012: Ver Dashboard | 5 | 2.5 |
| Pulido de UI/UX | - | 2 |
| Tests E2E | - | 2 |
| Bug fixes | - | 1.5 |

**Entregables**:
- Dashboard con métricas
- UI pulida y consistente
- Tests E2E críticos
- Beta lista

**Puntos**: 5

---

### Fase 3: Beta Testing (Semanas 15-18)

| Semana | Actividad |
|--------|-----------|
| 15 | Beta cerrada (20 usuarios) |
| 16 | Recolección de feedback |
| 17 | Iteración y fixes |
| 18 | Preparación para launch |

### Fase 4: Launch (Semana 19)

| Actividad | Fecha Objetivo |
|-----------|----------------|
| Launch público | Semana 19 |
| Marketing launch | Semana 19 |
| Onboarding de early adopters | Semanas 19-20 |

### Fase 5: Growth & Iteration (Semanas 20+)

| Trimestre | Objetivo |
|-----------|----------|
| Q3 2026 | Alcanzar 50 usuarios activos |
| Q4 2026 | Expandir a clínicas dentales |
| Q1 2027 | Alcanzar 500 usuarios |
| Q2 2027 | Expandir a inmobiliarias |

---

## Resumen de Sprints

```
Sprint 1 (Sem 5-6):  Setup           │████░░░░░░░░░░░░░░░░│ 8 pts
Sprint 2 (Sem 7-8):  Clientes        │████████████░░░░░░░░│ 15 pts
Sprint 3 (Sem 9-10): Mascotas        │███████░░░░░░░░░░░░░│ 8 pts
Sprint 4 (Sem 11-12): Citas          │███████████████░░░░░│ 19 pts
Sprint 5 (Sem 13-14): Dashboard      │████░░░░░░░░░░░░░░░░│ 5 pts
                                     Total: 55 pts
```

## Hitos Clave

| Hito | Fecha Objetivo | Estado |
|------|----------------|--------|
| Documentación completa | Semana 2 | 🔄 En Progreso |
| Arquitectura definida | Semana 4 | ⏳ Pendiente |
| Backend funcional | Semana 10 | ⏳ Pendiente |
| Frontend funcional | Semana 12 | ⏳ Pendiente |
| MVP completo | Semana 14 | ⏳ Pendiente |
| Beta cerrada | Semana 15 | ⏳ Pendiente |
| Launch público | Semana 19 | ⏳ Pendiente |

## Riesgos y Mitigaciones

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Scope creep | Alta | Alto | Seguir MVP estrictamente |
| Tecnología nueva | Media | Medio | Investigar antes de implementar |
| Falta de tiempo | Alta | Alto | Priorizar, no todo es Must Have |
| Cambios de requisitos | Media | Medio | Documentar bien, ADRs |

---

*Roadmap - Syncria v1.0*
*Última actualización: 2026*
