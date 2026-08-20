# Portfolio Package — Syncria

## Texto para LinkedIn

### Opcion 1 (Corta)

Proyecto completo: Syncria — CRM para clinicas veterinarias.

Stack: Java 17 + Spring Boot 3.5 + React 18 + TypeScript + PostgreSQL 16 + Zustand

Funcionalidades:
- Autenticacion JWT con rate limiting
- CRUD Contactos, Mascotas, Citas
- Calendario visual con conflict detection
- Dashboard ejecutivo con metrics
- Multi-tenant (aislamiento por empresa)
- 52 tests backend pasando

Arquitectura modular, preparada para SaaS.

#springboot #react #typescript #postgresql #java #crm #veterinary #portfolio

---

### Opcion 2 (Larga)

Desarrolle Syncria, una plataforma CRM completa para clinicas veterinarias.

El proyecto demuestra:

Backend:
- Java 17 + Spring Boot 3.5.4
- Spring Security + JWT (rate limiting, BCrypt)
- PostgreSQL 16 + Flyway migrations
- Arquitectura modular por capas
- 52 tests unitarios (JUnit 5 + Mockito)

Frontend:
- React 18 + TypeScript + Vite
- Zustand (state management)
- TailwindCSS (UI responsive)
- react-big-calendar (calendario visual)

Funcionalidades clave:
- Autenticacion segura con JWT
- CRUD completo: Contactos, Mascotas, Citas
- Calendario visual con 3 vistas (mes/semana/dia)
- Conflict detection por mascota + horario
- Dashboard ejecutivo con metrics
- Multi-tenant (aislamiento total por empresa)
- Skeleton loading y empty states

El 100% del MVP esta completado. Preparado para evolucionar a SaaS.

#springboot #react #typescript #postgresql #java17 #crm #portfolio #fullstack

---

## Descripcion para GitHub

### Descripcion Corta

CRM para clinicas veterinarias con React, Spring Boot, PostgreSQL. Multi-tenant, JWT, calendario visual, dashboard ejecutivo.

### Descripcion Larga

**Syncria** es una plataforma CRM completa disenada para clinicas veterinarias. Permite gestionar clientes, mascotas, citas con calendario visual, y un dashboard ejecutivo con metricas del negocio.

**Caracteristicas principales:**
- Autenticacion JWT con rate limiting
- CRUD completo: Contactos, Mascotas, Citas
- Calendario visual (mes/semana/dia) con conflict detection
- Dashboard ejecutivo con metrics y accesos rapidos
- Multi-tenant (aislamiento total de datos por empresa)
- UI profesional con skeleton loading y empty states

**Stack tecnologico:**
- Backend: Java 17, Spring Boot 3.5.4, Spring Security, JWT, PostgreSQL 16, Flyway
- Frontend: React 18, TypeScript, Vite, Zustand, TailwindCSS, react-big-calendar
- Testing: JUnit 5 + Mockito (52 tests), Vitest + RTL (6 tests)

**Arquitectura:** Modular por capas, preparada para SaaS multi-tenant.

---

## Elevator Pitch (30 segundos)

"Desarrolle Syncria, una plataforma CRM completa para clinicas veterinarias. El backend esta construido con Spring Boot y Java 17, el frontend con React y TypeScript, y usa PostgreSQL como base de datos.

Lo que lo hace interesante es la arquitectura: es modular y multi-tenant, significa que cada clinica tiene sus datos completamente aislados. Tiene un calendario visual con deteccion de conflictos, un dashboard ejecutivo con metricas, y 52 tests backend pasando.

El 100% del MVP esta completado y esta preparado para evolucionar a SaaS."

---

## Pitch de 2 Minutos para Entrevistas

"El proyecto que quiero mostrarles es Syncria, una plataforma CRM para clinicas veterinarias.

**Problema:** Las clinicas veterinarias necesitan organizar clientes, mascotas y citas de forma eficiente. Muchas usan Excel o WhatsApp, lo cual no es escalable.

**Solucion:** Syncria es una web app con 5 modulos: autenticacion, contactos, mascotas, citas con calendario visual, y un dashboard ejecutivo.

**Backend:** Use Spring Boot con Java 17. La arquitectura es modular: cada modulo tiene su controller, service, repository, DTOs y mapper. Usa Spring Security con JWT para autenticacion, y BCrypt para passwords. Implemente rate limiting en el login para prevenir abusos.

**Frontend:** Use React con TypeScript y Vite. El estado global lo manejo con Zustand, que es mas ligero que Redux. El calendario usa react-big-calendar con 3 vistas: mes, semana y dia. Tiene deteccion de conflictos: si intentas crear dos citas para la misma mascota en el mismo horario, te avisa.

**Base de datos:** PostgreSQL 16 con Flyway para migraciones. Cada tabla tiene un campo companyId para multi-tenant: los datos de cada clinica estan completamente aislados.

**Testing:** 52 tests backend con JUnit 5 y Mockito. Cubren todos los servicios: auth, contactos, mascotas, citas y dashboard. El frontend tiene tests con Vitest y React Testing Library.

**Resultado:** El MVP esta al 100%. La arquitectura es escalable y esta preparada para evolucionar a SaaS. Los 52 tests dan confianza en la calidad del codigo."

---

## Lista de Tecnologias

### Backend
- Java 17
- Spring Boot 3.5.4
- Spring Security + JWT (jjwt 0.12.6)
- Spring Data JPA
- PostgreSQL 16
- Flyway (migraciones)
- MapStruct 1.5.5 (mappers)
- Lombok
- Maven
- JUnit 5 + Mockito

### Frontend
- React 18.3.1
- TypeScript 5.4.5
- Vite 5.3.1
- TailwindCSS 3.4.4
- Zustand 4.5.2 (state management)
- Axios 1.7.2 (HTTP client)
- react-big-calendar 1.20.0
- date-fns 4.4.0
- react-hot-toast
- Vitest + React Testing Library

### Herramientas
- Git + GitHub
- Docker + Docker Compose
- pgAdmin (database UI)
- n8n (automatizaciones)

### Conceptos
- Arquitectura modular por capas
- Multi-tenant (companyId)
- REST API
- JWT authentication
- Rate limiting
- Soft delete
- Paginacion
- Conflict detection
- Skeleton loading
- Empty states
- Responsive design

---

## Preguntas Tecnicas para Entrevistas

### Java / Spring Boot

**P: Como implementaste la autenticacion en Syncria?**
R: Use Spring Security con JWT. El flujo es: 1) El usuario envia credenciales a /auth/login, 2) AuthService valida con BCrypt, 3) JwtUtil genera un token con el companyId, 4) En cada request, JwtAuthenticationFilter extrae y valida el token, 5) El companyId se guarda en el SecurityContext para uso en los controllers.

**P: Que es el multi-tenant y como lo implementaste?**
R: Multi-tenant significa que cada empresa tiene sus datos completamente aislados. Lo implemente con un campo companyId en todas las entidades (via BaseEntity). Cada query incluye WHERE companyId = ?. El companyId viene del JWT token. Asi, una clinica nunca puede ver los datos de otra.

**P: Como manejas las excepciones?**
R: Use un GlobalExceptionHandler con @ControllerAdvice. Cada modulo tiene sus propias excepciones (ContactNotFoundException, AppointmentConflictException, etc.). El handler las captura y retorna responses HTTP consistentes con status code, timestamp y mensaje.

**P: Que es el rate limiting y como lo implementaste?**
R: Es limitar el numero de request por tiempo. Implemente un filtro en /login: 5 intentos por minuto por IP. Usa un ConcurrentHashMap con timestamps. Si una IP supera el limite, retorna 429 Too Many Requests.

**P: Que es MapStruct y por que lo usas?**
R: Es un generador de mappers type-safe. Convierte entre Entity y DTO automaticamente. Es mejor que los mappers manuales porque: 1) Genera codigo en compile time (no reflection), 2) Es type-safe (compila errores), 3) Reduce boilerplate.

### React / TypeScript

**P: Por que Zustand en vez de Redux?**
R: Zustand es mas simple y ligero. No necesita boilerplate (reducers, actions, providers). Un store se define con create(). Es perfecto para proyectos donde no necesitas la complejidad de Redux. Ademas, tiene mejor performance porque solo re-renderiza los componentes que usan el estado que cambio.

**P: Como funciona el calendario con react-big-calendar?**
R: Es una libreria que provee un componente Calendar. Yo le paso los eventos (citas) como un array de objetos con start, end, y title. Tiene 3 vistas: mes, semana, dia. Usa date-fns para localizacion en espanol. Implemente un eventPropGetter para colorear eventos por estado.

**P: Que es el conflict detection en las citas?**
R: Antes de crear una cita, verifico si ya existe otra cita para la misma mascota en la misma fecha y horario que se superponga. Lo hago en el backend con una query JPA que busca overlap: a.startTime <= :endTime AND a.endTime > :startTime. Si hay conflicto, lanzo AppointmentConflictException.

**P: Que son los skeleton loading y empty states?**
R: Skeleton loading es mostrar placeholders animados mientras cargan los datos (en vez de un spinner). Empty states es mostrar un mensaje amigable cuando no hay datos (ej: "No hay citas programadas para hoy"). Ambos mejoran la experiencia de usuario.

### Arquitectura

**P: Por que una arquitectura modular?**
R: Porque facilita el mantenimiento y la escalabilidad. Cada modulo es independiente: puede modificarse sin afectar a otros. Es mas facil de entender (un desarrollador puede enfocarse en un modulo). Y prepara para SaaS: cada modulo podria desplegarse independientemente.

**P: Que es el soft delete?**
R: En vez de borrar registros fisicamente, marco un campo deleted = true. Esto: 1) Preserva datos historicos, 2) Permite "deshacer" eliminaciones, 3) Mantiene integridad referencial. Los queries incluyen WHERE deleted = false.

**P: Como escalarias Syncria a SaaS?**
R: Los pasos serian: 1) Migrar JWT a httpOnly cookies, 2) Agregar Redis para caching, 3) Configurar CDN para frontend, 4) Implementar connection pooling, 5) Agregar load balancing, 6) Configurar monitoring (Sentry), 7) CI/CD con GitHub Actions.

---

*Portfolio Package — Syncria v1.0.0*
