# Changelog - Syncria

Todos los cambios notables en el proyecto, organizados por version.

El formato se basa en [Keep a Changelog](https://keepachangelog.com/).

---

## [0.3.0] - 2026-07-29

### Sprint 3: Contactos

#### Added (Agregado)
- **Backend**
  - `Contact` entity (extends BaseEntity) con name, email, phone, address, notes
  - `ContactRepository` con busqueda por nombre/email, paginacion, filtro por companyId
  - `ContactService` con CRUD completo + validacion de email unico por tenant
  - `ContactController` con endpoints REST (GET, POST, PUT, DELETE)
  - `ContactRequestDTO` / `ContactResponseDTO` con validaciones
  - `ContactMapper` (MapStruct)
  - `ContactNotFoundException` (404) y `DuplicateContactException` (409)
  - `V2__create_contact_table.sql` (Flyway) con indice por company + name

- **Frontend**
  - `contacts/` modulo completo con types, store (Zustand), hook
  - `ContactListPage` con tabla, busqueda, paginacion
  - `ContactFormPage` para crear/editar con validacion de formulario
  - `ContactDeleteModal` con confirmacion

- **UI Components**
  - `Input.tsx` con label, error state, forwardRef
  - `Table.tsx` generica con Column generics, loading, empty state
  - `Pagination.tsx` con navegacion e informacion de resultados
  - `Spinner.tsx` con sizes sm/md/lg
  - `Badge.tsx` con variantes default/success/warning/danger/info
  - `ConfirmDialog.tsx` con overlay, escape key, loading state

- **Testing Infrastructure**
  - Vitest + React Testing Library + jest-dom + jsdom configurados
  - `test` y `test:watch` scripts en package.json
  - `setup.ts` con jest-dom imports
  - `Button.test.tsx` (6 tests: render, variants, click, disabled)

#### Changed (Cambiado)
- `App.tsx`: Agregadas rutas /contacts, /contacts/new, /contacts/:id/edit
- `Sidebar.tsx`: `/clients` → `/contacts`, label "Clients" → "Contacts"
- `GlobalExceptionHandler.java`: Agregado handler para DuplicateContactException
- `package.json`: Agregados scripts `test`, `test:watch`, `typecheck`
- `tsconfig.json`: Agregados types vitest/globals

#### Tests
- `ContactServiceTest.java` (10 tests): create, duplicate email, findAll, findById, update, update duplicate, delete, not found

## [0.2.1] - 2026-07-29

### Sprint 02.1: Hardening

#### Added (Agregado)
- **Backend**
  - `Company` entity con name, createdAt, updatedAt, soft-delete
  - `CompanyRepository` (JpaRepository)
  - `AuthService` ahora crea empresa "Mi Clinica" al registrar usuario (companyId dinámico)
  - `RateLimitingFilter`: 5 intentos/login/minuto por IP usando ConcurrentHashMap
  - Cache de usuarios en `JwtAuthenticationFilter` con ConcurrentHashMap (TTL 5 min)
  - Validación de JWT_SECRET en startup (mínimo 256 bits, Base64 válido)
  - Consulta `findByEmailAndDeletedFalse` en UserRepository

- **Tests**
  - `AuthServiceTest` (5 tests): registro exitoso, email duplicado, login válido, password inválido, usuario inexistente
  - `JwtUtilTest` (6 tests): generación de token, extractEmail, extractCompanyId, token inválido, token expirado, token válido

- **Frontend**
  - `authStore` ahora usa Zustand `persist` middleware (persiste user + token en localStorage)
  - Sesión se mantiene después de F5 (refresco de página)

#### Changed (Cambiado)
- `AuthService.java`: CompanyRepository inyectado, companyId dinámico desde empresa creada
- `JwtAuthenticationFilter.java`: Cache con ConcurrentHashMap + verificación de soft-delete
- `UserRepository.java`: Nuevo método `findByEmailAndDeletedFalse`
- `application.yml`: Removido default de JWT_SECRET (ahora requiere variable de entorno)
- `application-dev.yml`: Agregado JWT secret para desarrollo local
- `application-test.yml`: Agregado JWT secret para tests
- `authStore.ts`: Migrado a Zustand persist middleware
- `sprint-01-checklist.md`: Marcado como completado

#### Fixed (Corregido)
- CompanyId hardcodeado a 1L → ahora se crea empresa dinámicamente al registrar
- Usuarios con `is_deleted=true` ya no pueden autenticarse (verificado en filter)
- JWT query en cada request → cache de 5 minutos
- Sesión perdida en refresh → Zustand persist middleware
- JWT_SECRET sin validación → validación en startup con mensaje claro
- Sin rate limiting → filtro con 5 intentos/minuto/IP

## [0.2.0] - 2026-07-27

### Sprint 2: Autenticacion

#### Added (Agregado)
- **Backend**
  - `User` entity con campos: email, password, fullName, role
  - `UserRepository` con findByEmail y existsByEmail
  - `UserService` con getUserById y getUserByEmail
  - `AuthController` con endpoints: POST /register, POST /login, GET /me
  - `AuthService` con registro (BCrypt) y login (validacion de credenciales)
  - `JwtUtil` con generateToken, extractEmail, extractCompanyId, validateToken
  - `JwtAuthenticationFilter` para extraccion y validacion de JWT en cada request
  - `SecurityConfig` con CORS, CSRF disabled, rutas publicas/privadas, BCrypt
  - `RegisterRequestDTO` con validaciones (email, password min 8, fullName)
  - `LoginRequestDTO` con validaciones (email, password)
  - `AuthResponseDTO` con token, email, fullName, role
  - `AuthException` para credenciales invalidas
  - `DuplicateEmailException` para email duplicado
  - `NotFoundException` para recursos no encontrados (HTTP 404)
  - `GlobalExceptionHandler` con handlers para: auth, 401, 403, 404, 409, validacion, runtime

- **Frontend**
  - `LoginPage` con formulario de email y contrasena
  - `RegisterPage` con formulario de registro (nombre, email, contrasena, confirmacion)
  - `ProtectedRoute` para proteger rutas autenticadas
  - `authStore` (Zustand) con login, register, logout, loadSession, clearError
  - `useAuth` hook como wrapper del auth store
  - Auth types: LoginRequest, RegisterRequest, AuthResponse

- **Configuracion**
  - Spring Security con filtro JWT
  - JWT con expiration de 24 horas
  - CORS configurado para localhost:5173
  - Password hashing con BCrypt

#### Changed (Cambiado)
- `pom.xml`: Java 21 → 17 (compatibilidad con JDK instalado)
- `pom.xml`: Spring Boot 4.0.0 → 3.5.4
- `pom.xml`: Agregado spring-boot-starter-security
- `pom.xml`: Agregado jjwt-api/impl/jackson 0.12.6
- `pom.xml`: spring-boot-starter-webmvc → spring-boot-starter-web
- `application.yml`: Agregada configuracion JWT (secret, expiration)
- `application.yml`: Agregado open-in-view: false
- `App.tsx`: Rutas /login y /register agregadas, ProtectedRoute envuelve rutas privadas
- `Header.tsx`: Muestra nombre real del usuario, boton de logout
- `Sidebar.tsx`: NavLink con active state en vez de anchor tags
- `HealthController`: Version actualizada a 0.2.0
- `package.json`: Version actualizada a 0.2.0
- `pom.xml`: Version actualizada a 0.2.0

#### Fixed (Corregido)
- `SecurityConfig`: setAllowedOrigins → setAllowedOriginPatterns (Spring Security 6.x compatibility)
- `UserService`: RuntimeException → NotFoundException (HTTP 404 en vez de 500)
- `GlobalExceptionHandler`: Movido de module/auth/exception/ a shared/exception/
- `GlobalExceptionHandler`: Agregados handlers para AccessDeniedException y AuthenticationException

#### Documentation (Documentacion)
- Creado `docs/sprint-02.md` con plan detallado del Sprint 2
- Creado `PROJECT_STATUS.md` con estado actual del proyecto
- Actualizado `docs/roadmap.md` con progreso real de sprints

---

## [0.1.0] - 2026-07-26

### Sprint 1: Setup e Infraestructura

#### Added (Agregado)
- **Repositorio**
  - Git Flow configurado (main, develop)
  - .gitignore completo para Java, React, Docker, IDEs
  - README.md con documentacion completa

- **Docker**
  - docker-compose.yml con PostgreSQL 16, pgAdmin, n8n
  - docker/postgres/init.sql para inicializacion
  - docker/pgadmin/servers.json para configuracion automatica
  - .env.example con todas las variables documentadas

- **Backend**
  - Spring Boot scaffold con Maven wrapper
  - BaseEntity con id, companyId, createdAt, updatedAt, deleted
  - HealthController con GET /api/v1/health
  - DatabaseConfig con @EnableJpaAuditing
  - Flyway V1: create companies y users tables
  - application.yml, application-dev.yml, application-test.yml

- **Frontend**
  - React + TypeScript + Vite scaffold
  - TailwindCSS con paleta de colores primary
  - MainLayout con Header y Sidebar
  - api.ts con Axios interceptor para JWT
  - Tipos compartidos: User, Client, Pet, Appointment, PaginatedResponse
  - Button component reutilizable

- **Documentacion**
  - AGENTS.md con instrucciones para agentes AI
  - docs/vision.md, docs/mvp.md, docs/backlog.md
  - docs/roadmap.md, docs/architecture/overview.md
  - docs/adr/ (5 Architecture Decision Records)
  - docs/diagrams/ (4 diagramas Mermaid)

---

## [1.0.0] - 2026-07-30

### Sprint 06.1: Hardening Final

#### Added (Agregado)
- README.md profesional con capturas, instalacion, y documentacion completa
- docs/ARCHITECTURE.md — Arquitectura del sistema
- docs/INSTALLATION.md — Guia de instalacion detallada
- docs/CONTRIBUTING.md — Guia de contribucion
- docs/DEPLOYMENT.md — Guia de deployment
- LICENSE — MIT
- RELEASE_NOTES_v1.0.0.md — Notas de la version
- FINAL_AUDIT.md — Auditoria final (Production Readiness, Security, Maintainability, Scalability)
- PORTFOLIO_PACKAGE.md — Texto LinkedIn, GitHub, Elevator Pitch, Preguntas Tecnicas

#### Changed (Cambiado)
- Version: v0.6.0 → v1.0.0
- README.md: Actualizado a version profesional
- PROJECT_STATUS.md: Marcado como MVP COMPLETED, READY FOR PORTFOLIO

---

## [0.6.0] - 2026-07-30

### Sprint 6: Dashboard

#### Added (Agregado)
- **Backend**
  - `DashboardResponseDTO` con metrics: totalClients, totalPets, todayAppointments, weekAppointments, status counts, recentAppointments, speciesDistribution
  - `DashboardService` con queries agregadas multi-tenant (count contacts, pets, appointments, species distribution)
  - `DashboardController` con endpoint GET /api/v1/dashboard
  - `DashboardServiceTest` (4 tests): dashboard response, recent appointments, species distribution, empty state

- **Frontend**
  - `DashboardPage` con layout profesional estilo HubSpot/Salesforce
  - `StatCard` componente reutilizable con icono, color, y trend
  - `TodayAppointments` con lista cronologica, badges de estado, empty state
  - `QuickActions` con accesos rapidos (Nueva Cita, Nuevo Cliente, Nueva Mascota)
  - `dashboardStore` (Zustand) con auto-fetch
  - `useDashboard` hook
  - Skeleton loading animations en todas las secciones
  - Grafico de distribucion por especie (barras animadas)
  - Estado de citas: Programadas, Completadas, Canceladas

- **Tests**
  - `DashboardServiceTest.java` (4 tests): response, recent appointments, species, empty state

#### Changed (Cambiado)
- `App.tsx`: Ruta / ahora muestra DashboardPage (antes Home.tsx)
- `Sidebar.tsx`: "Inicio" renamed to "Dashboard"
- `ContactRepository.java`: Agregado countByCompanyIdAndDeletedFalse
- `PetRepository.java`: Agregado findByCompanyIdAndDeletedFalse
- `AppointmentRepository.java`: Agregado countByCompanyIdAndAppointmentDateAndDeletedFalse

#### Fixed (Corregido)
- N/A

## [0.5.0] - 2026-07-29

### Sprint 5: Citas

#### Added (Agregado)
- **Backend**
  - `Appointment` entity (extends BaseEntity) con petId, contactId, title, reason, appointmentDate, startTime, endTime, status, notes
  - `AppointmentStatus` enum (SCHEDULED, COMPLETED, CANCELLED)
  - `AppointmentRepository` con busqueda por companyId, status, rango de fechas, conflict detection
  - `AppointmentService` con CRUD completo + conflict detection por mascota + horario
  - `AppointmentController` con 7 endpoints REST (GET, POST, PUT, DELETE, status)
  - `AppointmentRequestDTO` / `AppointmentResponseDTO` con validaciones
  - `AppointmentMapper` (MapStruct)
  - `AppointmentNotFoundException` (404) y `AppointmentConflictException` (409)
  - `V4__create_appointment_table.sql` (Flyway)

- **Frontend**
  - `appointments/` modulo completo con types, store (Zustand), hook, utils
  - `AppointmentCalendar` con react-big-calendar (mes/semana/dia)
  - `AppointmentFormModal` para crear/editar citas
  - `AppointmentDetailModal` con detalle + acciones de estado
  - `appointmentStore` con manejo de conflictos
  - `calendarStyles` para colores por estado
  - Localizacion en espanol con date-fns

- **Tests**
  - `AppointmentServiceTest.java` (13 tests): create, conflict detection, findAll, findById, findByDateRange, update, updateStatus, delete, not found

#### Changed (Cambiado)
- `GlobalExceptionHandler.java`: Agregado handler para AppointmentConflictException
- `package.json`: Agregados react-big-calendar, date-fns, @types/react-big-calendar

#### Fixed (Corregido)
- N/A

## [0.4.0] - 2026-07-29

### Sprint 4: Mascotas

#### Added (Agregado)
- **Backend**
  - `Pet` entity (extends BaseEntity) con contactId, name, species, breed, birthDate, gender, notes
  - `PetRepository` con busqueda por companyId, nombre/especie/raza, count
  - `PetService` con CRUD completo + vinculo contacto-mascota
  - `PetController` con 6 endpoints REST (GET, POST, PUT, DELETE, byContact)
  - `PetRequestDTO` / `PetResponseDTO` con validaciones
  - `PetMapper` (MapStruct)
  - `PetNotFoundException` (404)
  - `V3__create_pet_table.sql` (Flyway)

- **Frontend**
  - `pets/` modulo completo con types, store (Zustand), hook
  - `PetListPage` con tabla, busqueda, paginacion
  - `PetFormPage` para crear/editar con selector de contacto
  - `PetDeleteModal` con confirmacion

- **Tests**
  - `PetServiceTest.java` (13 tests): create, findAll, findById, findByContactId, update, delete, not found, count

#### Changed (Cambiado)
- `App.tsx`: Agregadas rutas /pets, /pets/new, /pets/:id/edit
- `Sidebar.tsx`: Agregado enlace a Mascotas

---

*Changelog - Syncria*
*Ultima actualizacion: 2026-07-30*
*Version: v1.0.0 — MVP COMPLETED*
