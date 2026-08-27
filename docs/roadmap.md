# Roadmap de Desarrollo - Syncria

## Vision General

```
Fase 0: Product Discovery    ← Completado
Fase 1: System Architecture  ← Completado
Fase 2: MVP Development      ← Completado (v1.0.0)
Fase 3: Beta Testing
Fase 4: Launch
Fase 5: Growth & Iteration
```

## Calendario de Sprints

### Fase 0: Product Discovery (Semanas 1-2)
**Estado**: Completado

| Semana | Entregable | Estado |
|--------| Vision del producto | Completado |
| 1 | Definicion MVP | Completado |
| 2 | Product Backlog | Completado |
| 2 | Historias de Usuario | Completado |
| 2 | Roadmap | Completado |

### Fase 1: System Architecture (Semanas 3-4)
**Estado**: Completado

| Semana | Entregable | Estado |
|--------| Arquitectura general | Completado |
| 3 | Diagramas de arquitectura | Completado |
| 4 | ADRs (Architecture Decision Records) | Completado |
| 4 | Modelo de dominio | Completado |

### Fase 2: MVP Development (Semanas 5-14)

#### Sprint 1: Setup (Semanas 5-6)
**Objetivo**: Infraestructura base del proyecto
**Estado**: Completado (v0.1.0)

---

#### Sprint 2: Autenticacion (Semanas 7-8)
**Objetivo**: Sistema completo de autenticacion
**Estado**: Completado (v0.2.0)

---

#### Sprint 2.1: Hardening (Semanas 9)
**Objetivo**: Resolver tech debt critico
**Estado**: Completado (v0.2.1)

---

#### Sprint 3: Contactos (Semanas 9-10)
**Objetivo**: CRUD completo de contactos
**Estado**: Completado (v0.3.0)

| Historia | Puntos | Estado |
|----------|--------|--------|
| US-003: Crear Contacto | 5 | Completado |
| US-004: Listar Contactos | 5 | Completado |
| US-005: Editar Contacto | 3 | Completado |
| US-006: Eliminar Contacto | 2 | Completado |

---

#### Sprint 4: Mascotas (Semanas 11-12)
**Objetivo**: Gestion de mascotas vinculadas a contactos
**Estado**: Completado (v0.4.0)

| Historia | Puntos | Estado |
|----------|--------|--------|
| US-007: Registrar Mascota | 5 | Completado |
| US-008: Ver Mascotas del Contacto | 3 | Completado |

---

#### Sprint 5: Citas (Semanas 13-14)
**Objetivo**: Sistema de citas con calendario visual
**Estado**: Completado (v0.5.0)

| Historia | Puntos | Estado |
|----------|--------|--------|
| US-009: Crear Cita | 8 | Completado |
| US-010: Ver Calendario | 8 | Completado |
| US-011: Editar/Cancelar Cita | 3 | Completado |

---

#### Sprint 6: Dashboard (Semana 15)
**Objetivo**: Dashboard y pulido final del MVP
**Estado**: Completado (v0.6.0)

| Historia | Puntos | Estado |
|----------|--------|--------|
| US-012: Ver Dashboard | 5 | Completado |

---

### Fase 3: Beta Testing (Semanas 16-19)

| Semana | Actividad |
|--------|-----------|
| 16 | Beta cerrada (20 usuarios) |
| 17 | Recoleccion de feedback |
| 18 | Iteracion y fixes |
| 19 | Preparacion para launch |

### Fase 4: Launch (Semana 20)

| Actividad | Fecha Objetivo |
|-----------|----------------|
| Launch publico | Semana 20 |
| Marketing launch | Semana 20 |
| Onboarding de early adopters | Semanas 20-21 |

### Fase 5: Growth & Iteration (Semana 21+)

| Trimestre | Objetivo |
|-----------|----------|
| Q4 2026 | Alcanzar 50 usuarios activos |
| Q1 2027 | Expandir a clinicas dentales |
| Q2 2027 | Alcanzar 500 usuarios |

---

## Resumen de Sprints

```
Sprint 1 (v0.1.0):  Setup           │████████████████████│ Completado (8 pts)
Sprint 2 (v0.2.0):  Autenticacion   │████████████████████│ Completado (8 pts)
Sprint 2.1(v0.2.1): Hardening       │████████████████████│ Completado (~12 pts)
Sprint 3 (v0.3.0):  Contactos       │████████████████████│ Completado (15 pts)
Sprint 4 (v0.4.0):  Mascotas        │████████████████████│ Completado (8 pts)
Sprint 5 (v0.5.0):  Citas           │████████████████████│ Completado (19 pts)
Sprint 6 (v0.6.0):  Dashboard       │████████████████████│ Completado (5 pts)
                                    Total: 75 pts (75 completados)
```

## Hitos Clave

| Hito | Fecha Objetivo | Estado |
|------|----------------|--------|
| Documentacion completa | Semana 2 | Completado |
| Arquitectura definida | Semana 4 | Completado |
| Infraestructura base | Semana 6 | Completado |
| Autenticacion | Semana 8 | Completado |
| Hardening | Semana 9 | Completado |
| Contactos | Semana 10 | Completado |
| Mascotas | Semana 12 | Completado |
| Citas | Semana 14 | Completado |
| Dashboard | Semana 15 | Completado |
| MVP completo | Semana 15 | Completado |
| Beta cerrada | Semana 19 | Pendiente |
| Launch publico | Semana 20 | Pendiente |

## Riesgos y Mitigaciones

| Riesgo | Probabilidad | Impacto | Mitigacion |
|--------|--------------|---------|------------|
| Scope creep | Alta | Alto | Seguir MVP estrictamente |
| Tecnologia nueva | Media | Medio | Investigar antes de implementar |
| Falta de tiempo | Alta | Alto | Priorizar, no todo es Must Have |
| Cambios de requisitos | Media | Medio | Documentar bien, ADRs |

---

*Roadmap - Syncria v1.0.0*
*Ultima actualizacion: 2026-08-23*
