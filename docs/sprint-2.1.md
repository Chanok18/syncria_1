# Sprint 02.1 — Hardening

## Informacion del Sprint

| Campo | Valor |
|-------|-------|
| **Nombre** | Sprint 02.1: Hardening |
| **Duracion** | 1 semana (5 dias laborales) |
| **Fecha Inicio** | Por definir |
| **Fecha Fin** | Por definir |
| **Puntos de Historia** | ~12 (tech debt) |
| **Capacidad** | 20 horas |
| **Sprint anterior** | Sprint 02: Autenticacion (v0.2.0) |

## Objetivo del Sprint

> "Resolver el tech debt critico del Sprint 02, agregar tests de autenticacion, y mejorar la seguridad antes de construir funcionalidad nueva."

## Definition of Done (DoD)

- [ ] companyId funcional (no hardcodeado)
- [ ] Session persiste despues de refresh
- [ ] JWT filter verifica soft-delete
- [ ] Tests de AuthService pasando
- [ ] Tests de JwtUtil pasando
- [ ] Rate limiting en /login
- [ ] JWT secret validado en startup
- [ ] Build exitoso (backend + frontend)

---

## Tareas del Sprint

### Fase 1: Multi-tenant Funcional (4 horas)

#### TD-001: Company Entity + Migracion

**Descripcion**: Crear la tabla `companies` con entity JPA y migracion Flyway.

**Archivos a crear**:
- `backend/src/main/java/com/syncria/module/company/entity/Company.java`
- `backend/src/main/java/com/syncria/module/company/repository/CompanyRepository.java`
- `backend/src/main/resources/db/migration/V2__create_contact_table.sql` (incluye companies si no existe)

**Criterios de Aceptacion**:
- [ ] Company entity con campos: id, name, createdAt, updatedAt, deleted
- [ ] CompanyRepository con findById
- [ ] V2 migration crea tabla companies (si no existe de V1)

#### TD-002: AuthService — companyId Dinamico

**Descripcion**: Modificar AuthService para crear empresa por defecto al registrar.

**Archivos a modificar**:
- `AuthService.java` — crear Company antes de User
- `CompanyRepository` — inyectar en AuthService

**Criterios de Aceptacion**:
- [ ] Al registrar, se crea empresa "Mi Clinica" por defecto
- [ ] El companyId se asigna dinamicamente
- [ ] El JWT incluye el companyId real

---

### Fase 2: Seguridad JWT (3 horas)

#### TD-003: JWT Cache en Filter

**Descripcion**: Agregar cache simple para evitar query a BD en cada request.

**Archivos a modificar**:
- `JwtAuthenticationFilter.java` — usar ConcurrentHashMap

**Criterios de Aceptacion**:
- [ ] El filter no consulta la BD en cada request
- [ ] La cache se invalida cuando el token expira
- [ ] El usuario sigue siendo verificado

#### TD-004: Soft-delete Check

**Descripcion**: Verificar `is_deleted = false` en el filtro JWT.

**Archivos a modificar**:
- `JwtAuthenticationFilter.java` — agregar check
- `UserRepository.java` — agregar query

**Criterios de Aceptacion**:
- [ ] Usuarios con `is_deleted=true` no pueden autenticarse
- [ ] El JWT existente no funciona si el usuario fue eliminado

#### TD-005: JWT Secret Validation

**Descripcion**: Validar que el JWT secret tenga longitud minima.

**Archivos a modificar**:
- `JwtUtil.java` — agregar validacion en constructor o @PostConstruct

**Criterios de Aceptacion**:
- [ ] La app no inicia si el JWT secret es menor a 256 bits
- [ ] Mensaje de error claro si la validacion falla

---

### Fase 3: Frontend Session (1 hora)

#### TD-006: Session Persistence

**Descripcion**: Persistir la sesion del usuario despues de refresh.

**Archivos a modificar**:
- `frontend/src/features/auth/store/authStore.ts` — Zustand persist middleware

**Criterios de Aceptacion**:
- [ ] Despues de F5, el nombre del usuario aparece en el Header
- [ ] El token se mantiene en localStorage
- [ ] El user se hidrata del token o de la cache

---

### Fase 4: Rate Limiting (2 horas)

#### TD-007: Rate Limiting en Auth Endpoints

**Descripcion**: Agregar proteccion contra fuerza bruta.

**Archivos a modificar**:
- `pom.xml` — agregar dependency (bucket4j o spring interceptor)
- `SecurityConfig.java` — configurar rate limiting
- O crear filtro manual con ConcurrentHashMap

**Criterios de Aceptacion**:
- [ ] Maximo 5 intentos de login por minuto por IP
- [ ] Mensaje de error claro cuando se excede el limite
- [ ] El rate limiting no afecta otros endpoints

---

### Fase 5: Tests (4 horas)

#### TD-008: Tests de AuthService

**Archivos a crear**:
- `backend/src/test/java/com/syncria/module/auth/service/AuthServiceTest.java`

**Que testear**:
- [ ] Registro exitoso
- [ ] Registro con email duplicado (lanza DuplicateEmailException)
- [ ] Login exitoso
- [ ] Login con credenciales invalidas (lanza AuthException)
- [ ] Login con usuario no existente

#### TD-009: Tests de JwtUtil

**Archivos a crear**:
- `backend/src/test/java/com/syncria/security/util/JwtUtilTest.java`

**Que testear**:
- [ ] Generar token valido
- [ ] Extraer email del token
- [ ] Extraer companyId del token
- [ ] Token invalido retorna false
- [ ] Token expirado retorna false

---

## Estimacion de Tiempo

| Fase | Horas |
|------|-------|
| Multi-tenant funcional | 4h |
| Seguridad JWT | 3h |
| Frontend session | 1h |
| Rate limiting | 2h |
| Tests | 4h |
| **Total** | **14h / 20h** |

---

## Dependencias

| Tarea | Depende de |
|-------|------------|
| TD-001 (Company entity) | Ninguna |
| TD-002 (AuthService fix) | TD-001 |
| TD-003 (JWT cache) | Ninguna |
| TD-004 (Soft-delete) | Ninguna |
| TD-005 (Secret validation) | Ninguna |
| TD-006 (Session persistence) | Ninguna |
| TD-007 (Rate limiting) | Ninguna |
| TD-008 (AuthService tests) | TD-001, TD-002 |
| TD-009 (JwtUtil tests) | Ninguna |

---

## Entregables del Sprint

1. **Company entity** funcional
2. **companyId dinamico** en registro
3. **JWT cache** en filter
4. **Soft-delete check** en JWT
5. **JWT secret validation** en startup
6. **Session persistence** en frontend
7. **Rate limiting** en auth endpoints
8. **Tests** de AuthService y JwtUtil
9. **Version**: v0.2.1

---

## Notas

- Este sprint es **tecnico**, sin funcionalidad nueva para el usuario
- Priorizar TD-001 y TD-002 (companyId) porque bloean Sprint 03
- Los tests son criticos para mantener calidad a medida que el proyecto crece
- El rate limiting es una proteccion de seguridad basica

---

*Sprint 02.1 Plan — Syncria*
*Estado: COMPLETADO — Implementado el 2026-07-29*

### Resultado Final

| TD | Tarea | Estimacion | Resultado | Archivos |
|----|-------|------------|-----------|----------|
| TD-001 | Company entity + companyId dinamico | 4h | COMPLETADO | Company.java, CompanyRepository.java, AuthService.java |
| TD-002 | Session persistence | 1h | COMPLETADO | authStore.ts (Zustand persist) |
| TD-003 | Soft-delete + cache JWT | 1.5h | COMPLETADO | JwtAuthenticationFilter.java, UserRepository.java |
| TD-004 | JWT secret validation | 0.5h | COMPLETADO | JwtUtil.java, application-dev.yml, application-test.yml |
| TD-005 | Rate limiting | 2h | COMPLETADO | RateLimitingFilter.java, SecurityConfig.java |
| TD-006 | Tests | 4h | COMPLETADO | AuthServiceTest.java (5), JwtUtilTest.java (6) |
| TD-007 | Documentacion | 1h | COMPLETADO | CHANGELOG.md, PROJECT_STATUS.md |
| **Total** | | **14h** | **12/12 tests PASS** | **7 tareas** |

### Entregables
1. Company entity funcional + companyId dinamico
2. Session persistence (Zustand persist middleware)
3. Soft-delete check + JWT cache (TTL 5 min)
4. JWT_SECRET validation en startup (min 256 bits)
5. Rate limiting en /login (5 intentos/min/IP)
6. 11 nuevos tests unitarios (AuthService + JwtUtil)
7. Documentacion actualizada

**Veredicto**: Sprint 02.1 completado. Base estabilizada para iniciar Sprint 03.
