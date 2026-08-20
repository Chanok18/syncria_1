# Sprint 02 - Autenticacion

## Informacion del Sprint

| Campo | Valor |
|-------|-------|
| **Nombre** | Sprint 02: Autenticacion |
| **Duracion** | 2 semanas (10 dias laborales) |
| **Fecha Inicio** | 2026-07-27 |
| **Fecha Fin** | 2026-08-08 |
| **Puntos de Historia** | 8 |
| **Capacidad** | 40 horas |

## Objetivo del Sprint

> "Entregar un sistema completo de autenticacion: registro, login, JWT, rutas protegidas y logout."

## Definition of Done (DoD)

- [x] Registro de usuario funciona
- [x] Login de usuario funciona
- [x] Generacion y validacion de JWT funciona
- [x] Endpoints protegidos retornan 401 sin autenticacion
- [x] Paginas de login/registro del frontend funcionan
- [x] Auth Store persiste la sesion
- [x] El usuario puede acceder a paginas protegidas despues del login
- [x] Cierre de sesion funciona correctamente

---

## Historias de Usuario

### US-001: Registro de Usuario

| Campo | Valor |
|-------|-------|
| **ID** | US-001 |
| **Prioridad** | Must Have |
| **Estimacion** | 5 puntos |
| **Estado** | Completado |

**Criterios de Aceptacion**:
- [x] El formulario valida email valido
- [x] La contrasena debe tener minimo 8 caracteres
- [x] Se muestra error si el email ya existe
- [x] Se redirige al dashboard despues del registro
- [x] Los datos se guardan con contrasena encriptada (bcrypt)

### US-002: Login/Logout

| Campo | Valor |
|-------|-------|
| **ID** | US-002 |
| **Prioridad** | Must Have |
| **Estimacion** | 3 puntos |
| **Estado** | Completado |

**Criterios de Aceptacion**:
- [x] Se valida email y contrasena
- [x] Se muestra error claro si credenciales son incorrectas
- [x] Se genera token JWT
- [x] Se redirige al dashboard
- [x] El token expira en 24 horas
- [x] Logout limpia el token del localStorage

---

## Tareas del Sprint

### Backend

#### Tarea 1: Corregir pom.xml
- Cambiar Spring Boot 4.0.0 a 3.5.4
- Cambiar Java 17 a 21
- Agregar spring-boot-starter-security
- Agregar jjwt-api/impl/jackson 0.12.6
- Corregir spring-boot-starter-test
- Agregar spring-security-test

#### Tarea 2: Configuracion JWT
- Agregar `app.jwt.secret` y `app.jwt.expiration-ms` a application.yml
- Configurar open-in-view: false

#### Tarea 3: Modulo User
- User entity (extiende BaseEntity)
- UserRepository (findByEmail, existsByEmail)
- UserResponseDTO
- UserService

#### Tarea 4: Modulo Auth
- AuthController (register, login, me)
- RegisterRequestDTO (con validaciones)
- LoginRequestDTO (con validaciones)
- AuthResponseDTO
- AuthService (register con bcrypt, login con validacion)
- AuthException, DuplicateEmailException

#### Tarea 5: Seguridad
- JwtUtil (generateToken, extractEmail, extractCompanyId, validateToken)
- JwtAuthenticationFilter (extrae JWT del header, valida, setea SecurityContext)
- SecurityConfig (cors, csrf disabled, rutas publicas/privadas, BCryptPasswordEncoder)
- GlobalExceptionHandler (manejo de errores)

### Frontend

#### Tarea 6: Tipos y Store
- Auth types (LoginRequest, RegisterRequest, AuthResponse)
- authStore con Zustand (user, token, login, register, logout, loadSession)
- useAuth hook

#### Tarea 7: Paginas de Auth
- LoginPage (formulario email + contrasena)
- RegisterPage (formulario nombre + email + contrasena + confirmacion)
- ProtectedRoute (redirect a /login si no hay token)

#### Tarea 8: Integracion
- Actualizar App.tsx con rutas /login, /register y ProtectedRoute
- Actualizar Header con nombre de usuario real y boton logout
- Actualizar Sidebar con NavLink (link activo)
- api.ts ya tenia interceptor JWT configurado

### Documentacion

#### Tarea 9: Documentacion
- Crear docs/sprint-02.md
- Crear PROJECT_STATUS.md
- Actualizar roadmap.md

---

## Archivos Creados/Modificados

### Backend (nuevos)
```
backend/src/main/java/com/syncria/
├── module/user/
│   ├── entity/User.java
│   ├── repository/UserRepository.java
│   ├── service/UserService.java
│   └── dto/UserResponseDTO.java
├── module/auth/
│   ├── controller/AuthController.java
│   ├── service/AuthService.java
│   ├── dto/
│   │   ├── RegisterRequestDTO.java
│   │   ├── LoginRequestDTO.java
│   │   └── AuthResponseDTO.java
│   └── exception/
│       ├── AuthException.java
│       ├── DuplicateEmailException.java
│       └── GlobalExceptionHandler.java
└── security/
    ├── config/
    │   ├── SecurityConfig.java
    │   └── JwtAuthenticationFilter.java
    └── util/
        └── JwtUtil.java
```

### Backend (modificados)
```
backend/pom.xml                                    # Java 21, Spring Boot 3.5.4, Security, JWT
backend/src/main/resources/application.yml         # JWT config, open-in-view
```

### Frontend (nuevos)
```
frontend/src/features/auth/
├── components/
│   ├── LoginPage.tsx
│   ├── RegisterPage.tsx
│   └── ProtectedRoute.tsx
├── hooks/useAuth.ts
├── store/authStore.ts
└── types/types.ts
```

### Frontend (modificados)
```
frontend/src/App.tsx                    # Rutas /login, /register, ProtectedRoute
frontend/src/components/layout/Header.tsx   # Nombre real, boton logout
frontend/src/components/layout/Sidebar.tsx  # NavLink con active state
```

---

## Endpoints API

| Metodo | Ruta | Descripcion | Publico |
|--------|------|-------------|---------|
| POST | /api/v1/auth/register | Registro de usuario | Si |
| POST | /api/v1/auth/login | Login, retorna JWT | Si |
| GET | /api/v1/auth/me | Usuario actual (del JWT) | No |
| GET | /api/v1/health | Health check | Si |

---

## Notas Tecnicas

### JWT
- Algoritmo: HMAC-SHA (HS256)
- Expiracion: 24 horas
- Claims: sub (email), companyId, iat, exp
- Secret configurable via variable de entorno JWT_SECRET

### Seguridad
- BCrypt para hashing de contrasenas
- Stateless (sin sesiones en servidor)
- CORS configurado para localhost:5173
- Rutas publicas: /api/v1/auth/**, /api/v1/health

### Frontend
- Token almacenado en localStorage
- Axios interceptor agrega Authorization: Bearer <token>
- 401 response redirige a /login
- Zustand store para estado de autenticacion

---

*Sprint 02 Plan - Syncria v0.2.0*
*Fecha: 2026-07-27*
