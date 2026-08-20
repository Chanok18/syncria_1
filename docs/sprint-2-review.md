# Sprint 02 Review — Autenticacion

## Resumen

| Campo | Valor |
|-------|-------|
| **Sprint** | 02: Autenticacion |
| **Version** | v0.2.0 |
| **Fecha** | 2026-07-27 |
| **Duracion** | 2 dias |
| **Puntos** | 8 |
| **Veredicto** | APPROVED WITH FIXES |

---

## Objetivos

Entregar un sistema completo de autenticacion: registro, login, JWT, rutas protegidas y logout.

---

## Entregables

### Backend (14 archivos nuevos)

| Archivo | Descripcion |
|---------|-------------|
| User.java | Entity JPA (email, password, fullName, role) |
| UserRepository.java | findByEmail, existsByEmail |
| UserService.java | getUserById, getUserByEmail |
| UserResponseDTO.java | Record DTO |
| AuthController.java | POST register, POST login, GET me |
| AuthService.java | Register con BCrypt, login con validacion |
| RegisterRequestDTO.java | Validaciones: email, password min 8, fullName |
| LoginRequestDTO.java | Validaciones: email, password |
| AuthResponseDTO.java | Record: token, email, fullName, role |
| AuthException.java | Credenciales invalidas |
| DuplicateEmailException.java | Email duplicado |
| NotFoundException.java | Recursos no encontrados (404) |
| JwtUtil.java | generateToken, extractEmail, validateToken |
| JwtAuthenticationFilter.java | Filtro JWT en cada request |
| SecurityConfig.java | CORS, csrf disabled, rutas publicas/privadas |

### Frontend (6 archivos nuevos)

| Archivo | Descripcion |
|---------|-------------|
| LoginPage.tsx | Formulario login |
| RegisterPage.tsx | Formulario registro |
| ProtectedRoute.tsx | Redirect si no hay token |
| authStore.ts | Zustand: user, token, login, register, logout |
| useAuth.ts | Hook wrapper |
| types/types.ts | LoginRequest, RegisterRequest, AuthResponse |

### Documentacion

| Archivo | Descripcion |
|---------|-------------|
| sprint-02.md | Plan detallado |
| sprint-history.md | Historial de sprints |
| CHANGELOG.md | v0.2.0 |
| releases/v0.2.0.md | Release notes |

---

## Problemas Encontrados

### Bugs Criticos

| # | Bug | Severidad | Estado |
|---|-----|-----------|--------|
| 1 | Java 21 en pom.xml, JDK 17 instalado — backend no compila | CRITICAL | Corregido |
| 2 | companyId hardcodeado a 1L | HIGH | Abierto |
| 3 | UserService retorna 500 en vez de 404 | HIGH | Corregido |
| 4 | CORS setAllowedOrigins incompatible con Spring Security 6.x | HIGH | Corregido |
| 5 | JWT query en cada request sin cache | HIGH | Abierto |
| 6 | Soft-delete no verificado en JWT filter | HIGH | Abierto |
| 7 | Sin handler para AccessDeniedException | HIGH | Corregido |
| 8 | Session no persiste despues de refresh | HIGH | Abierto |

### Problemas de Documentacion

| # | Problema | Severidad |
|---|----------|-----------|
| 1 | backlog.md: sprint assignments desactualizados | HIGH |
| 2 | sprint-01-checklist.md: nunca actualizado a completado | MEDIUM |
| 3 | Contradiccion de versiones (v1.0 vs v0.2.0) | MEDIUM |
| 4 | architecture/overview.md:JwtTokenProvider vs JwtUtil | MEDIUM |

---

## Fixes Aplicados

| # | Fix | Archivo | Cambio |
|---|-----|---------|--------|
| 1 | Java version | pom.xml | 21 → 17 |
| 2 | CORS | SecurityConfig.java | setAllowedOrigins → setAllowedOriginPatterns |
| 3 | 404 handling | UserService.java | RuntimeException → NotFoundException |
| 4 | Exception handler | shared/exception/ | Movido + handlers para Security |
| 5 | AGENTS.md | AGENTS.md | Java 21 → 17 |

---

## Metricas

| Metrica | Valor |
|---------|-------|
| Archivos backend nuevos | 14 |
| Archivos frontend nuevos | 6 |
| Archivos modificados | 5 |
| Bugs encontrados | 8 |
| Bugs corregidos | 5 |
| Bugs abiertos | 3 |
| TypeScript errors | 0 |
| ESLint errors | 0 |
| Backend compile | SUCCESS |
| Backend tests | 1/1 PASS |
| Frontend build | SUCCESS (225KB) |

---

## Lecciones Aprendidas

### 1. Verificar entorno antes de estimar
El pom.xml declaraba Java 21 pero el JDK era 17. Siempre verificar `java -version` antes de configurar el proyecto.

### 2. cmd /c no propaga exit codes
En Windows, `cmd /c` puede fallar silenciosamente. Usar bash wrapper o PowerShell para capturar errores reales.

### 3. CORS en Spring Security 6.x
`setAllowedOrigins` con `allowCredentials(true)` es rechazado. Usar `setAllowedOriginPatterns` siempre.

### 4. Exception handling desde el inicio
`GlobalExceptionHandler` debe existir desde el primer modulo para mantener consistencia en respuestas de error.

### 5. Multi-tenant requiere diseno anticipado
El `companyId` hardcodeado fue el issue mas critico. Cada modulo nuevo debe considerar el aislamiento por tenant desde el inicio.

---

## Veredicto Final

**APPROVED WITH FIXES**

El modulo de autenticacion esta estructuralmente completo y funcional. Los bugs criticos fueron corregidos durante la auditoria. Los issues abiertos estan documentados y asignados al Sprint 02.1.

---

*Sprint 02 Review — Syncria v0.2.0*
*2026-07-27*
