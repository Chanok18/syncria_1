# Tech Debt — Syncria

Registro de deuda tecnica acumulada y pendiente de resolver.

---

## Estado Actual

| Severidad | Cantidad | Target |
|-----------|----------|--------|
| HIGH | 0 | Resuelto |
| MEDIUM | 2 | Deferred |
| LOW | 2 | Deferred |
| **Total** | **4** | |

---

## RESUELTOS — Sprint 02.1 (Hardening)

### TD-001: companyId Hardcodeado a 1L
- **Archivo**: `AuthService.java`
- **Solucion**: Creada Company entity, auto-crea empresa "Mi Clinica" al registrar, usa companyId dinamico del JWT.
- **Estado**: RESUELTO

### TD-002: JWT Query en Cada Request
- **Archivo**: `JwtAuthenticationFilter.java`
- **Solucion**: Cache con ConcurrentHashMap, TTL 5 minutos.
- **Estado**: RESUELTO

### TD-003: Soft-delete No Verificado en JWT Filter
- **Archivo**: `JwtAuthenticationFilter.java`, `UserRepository.java`
- **Solucion**: Query `findByEmailAndDeletedFalse` + check `user.getDeleted()` en filter.
- **Estado**: RESUELTO

### TD-004: Session No Persiste despues de Refresh
- **Archivo**: `authStore.ts`
- **Solucion**: Zustand persist middleware (persiste user + token).
- **Estado**: RESUELTO

### TD-005: JWT Secret con Fallback Predecible
- **Archivo**: `application.yml`, `JwtUtil.java`
- **Solucion**: Removido default. Validacion en startup: minimo 256 bits, Base64 valido.
- **Estado**: RESUELTO

### TD-006: Sin Rate Limiting en /login
- **Archivo**: `RateLimitingFilter.java`, `SecurityConfig.java`
- **Solucion**: Filtro con ConcurrentHashMap, 5 intentos/minuto/IP.
- **Estado**: RESUELTO

### TD-007: Sin Tests Unitarios de Auth
- **Archivo**: `AuthServiceTest.java`, `JwtUtilTest.java`
- **Solucion**: 11 tests creados (5 AuthService, 6 JwtUtil), todos pasando.
- **Estado**: RESUELTO

---

## DEFERRED — Post-MVP (Sprint 06.1)

### TD-008: Token en localStorage (XSS Risk)
- **Problema**: JWT almacenado en localStorage, susceptible a XSS.
- **Impacto**: Aceptado para MVP. Riesgo conocido.
- **Solucion**: Migrar a httpOnly cookies.
- **Estado**: DEFERRED (Sprint 06.1)
- **Prioridad**: LOW

### TD-009: Docker Desktop Pendiente
- **Problema**: Docker no disponible en el entorno de desarrollo actual.
- **Impacto**: No se puede verificar conexion PostgreSQL ni flujo completo.
- **Solucion**: Instalar Docker Desktop o usar WSL2.
- **Estado**: DEFERRED (Sprint 06.1)
- **Prioridad**: LOW

### TD-010: Tests Frontend Limitados
- **Problema**: Solo 6 tests frontend (Button.test.tsx). Faltan tests para stores y paginas.
- **Impacto**: Sin cobertura en componentes React criticos.
- **Solucion**: Agregar tests para ContactStore, PetStore, AppointmentStore, DashboardStore.
- **Estado**: DEFERRED (Sprint 06.1)
- **Prioridad**: MEDIUM

### TD-011: Sin Swagger/OpenAPI
- **Problema**: API sin documentacion automatica.
- **Impacto**: Dificultad para consumidores de la API.
- **Solucion**: Integrar springdoc-openapi.
- **Estado**: DEFERRED (Sprint 06.1)
- **Prioridad**: MEDIUM

---

## DEFERRED — Futuro (Sprint 07+)

### TD-012: Sin CI/CD Pipeline
- **Problema**: No hay pipeline automatizado de testing y deploy.
- **Impacto**: Deploy manual, riesgo de errores.
- **Solucion**: Configurar GitHub Actions.
- **Estado**: DEFERRED (Sprint 07)
- **Prioridad**: LOW

### TD-013: Sin Observabilidad
- **Problema**: Sin logging estructurado, metrics, ni error tracking.
- **Impacto**: Dificultad para diagnosticar problemas en produccion.
- **Solucion**: Sentry + logging + metrics.
- **Estado**: DEFERRED (Sprint 07)
- **Prioridad**: LOW

---

*Tech Debt — Syncria*
*Ultima actualizacion: 2026-07-30*
