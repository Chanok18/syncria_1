# Syncria - AI Agent Instructions

Plataforma CRM inteligente y modular, preparada para evolucionar a SaaS multi-tenant.

**Version actual**: v1.4.0  
**Estado**: CI/CD completado  
**Siguiente etapa**: Etapa 5 — Deploy

---

## Estado del Proyecto

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

| Etapa | Prioridad | Descripcion |
|-------|-----------|-------------|
| **Etapa 4** | Alta | CI/CD (GitHub Actions) |
| **Etapa 5** | Alta | Deploy (Railway/Render) |
| **Etapa 6** | Media | Beta testing |
| **Etapa 7** | Baja | Portfolio/SaaS |

### Metricas Actuales

| Metrica | Valor |
|---------|-------|
| Backend tests | 68/68 PASS |
| Frontend tests | 43/43 PASS |
| Frontend lint | 0 errors |
| Frontend typecheck | PASS |
| Frontend build | PASS |
| Security Score | 8.5/10 |

---

## Stack Tecnologico

| Capa | Tecnologia |
|------|------------|
| Backend | Java 17 + Spring Boot 3.5.4 + Maven |
| Frontend | React 18 + TypeScript + Vite + TailwindCSS |
| Base de datos | PostgreSQL 16 + H2 (test unitario) |
| Migraciones | Flyway |
| Estado global | Zustand |
| Seguridad | Spring Security + JWT (httpOnly cookies) |
| Automatizaciones | n8n |
| Deploy | Docker Compose |

---

## Arquitectura

Modular por capas. Cada modulo sigue el mismo patron:

```
backend/src/main/java/com/syncria/
├── module/
│   ├── auth/            # Register, Login, JWT, Cookies
│   ├── contact/         # CRUD Contactos
│   ├── pet/             # CRUD Mascotas
│   ├── appointment/     # CRUD Citas + Calendario
│   ├── dashboard/       # Metrics ejecutivas
│   ├── company/         # Multi-tenant
│   └── user/            # Usuarios
├── security/            # JWT, Filtros, Rate Limiting, CSP
├── shared/              # Excepciones globales, BaseEntity
└── SyncriaApplication.java
```

**Regla clave**: Las dependencias van hacia adentro. Los modulos NO dependen entre si directamente.

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

## Multi-tenant

Todas las entidades incluyen campo `companyId` para aislamiento:

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long companyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean deleted = false;
}
```

- Cada query filtra por `companyId`
- El `companyId` viene del JWT token
- Los modulos NO pueden acceder a datos de otras empresas

---

## Frontend

```
frontend/src/
├── features/
│   ├── auth/            # Login, Register, ProtectedRoute
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── store/       # Zustand stores
│   │   └── types/
│   ├── contacts/        # CRUD Contactos
│   ├── pets/            # CRUD Mascotas
│   ├── appointments/    # Calendario + Modales
│   └── dashboard/       # Dashboard ejecutivo
├── components/
│   ├── layout/          # Header, Sidebar
│   └── ui/              # Button, Input, Table, etc.
├── lib/                 # api.ts (Axios + cookies)
├── layouts/             # MainLayout
└── types/               # Tipos compartidos
```

---

## Git Flow

```
main          <- produccion, merge solo desde develop
develop       <- integracion, base para features
feature/*     <- nuevas funcionalidades (feature/user-auth)
bugfix/*      <- correccion de bugs (bugfix/login-error)
```

---

## Comandos Esenciales

### Backend
```bash
cd backend
./mvnw spring-boot:run                    # Ejecutar app
./mvnw test                               # Todos los tests (68)
./mvnw test -Dtest=AuthServiceTest        # Test especifico
./mvnw test -Dtest=AuthServiceTest#method # Metodo especifico
./mvnw clean package                      # Build completo
./mvnw verify                             # Tests + integration tests
```

### Frontend
```bash
cd frontend
npm install       # Instalar deps
npm run dev       # Desarrollo (http://localhost:5173)
npm run build     # Build produccion
npm run lint      # Linting
npm run typecheck # Verificar tipos
npm run test      # Tests (43)
```

### Docker
```bash
docker-compose up -d          # Levantar todo
docker-compose logs -f backend # Logs backend
docker-compose down           # Parar todo
```

---

## Variables de Entorno

### Backend (application.yml)

```yaml
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
```

**IMPORTANTE**: Usamos `127.0.0.1` en lugar de `localhost` porque en esta maquina localhost resolvia a IPv6 (::1) y provocaba problemas de autenticacion con PostgreSQL.

---

## Flyway

Scripts en `backend/src/main/resources/db/migration/`:

```
V1__create_company_table.sql
V2__create_user_table.sql
V3__create_contact_table.sql
V4__create_pet_table.sql
V5__create_appointment_table.sql
```

**Regla**: Nunca editar un script ya ejecutado. Siempre crear nuevo script con version siguiente.

---

## Testing

### Backend (68 tests)
- **Unit**: Services con Mockito + H2
- **Integration**: `@SpringBootTest` + H2
- **Security**: RateLimitingFilterTest, SecurityHeadersTest
- **API**: `@WebMvcTest` + MockMvc

### Frontend (43 tests)
- **Unit**: Vitest + React Testing Library
- **E2E**: Playwright (futuro)

---

## Reglas de Codigo

### Backend (Java)
- Package raiz: `com.syncria`
- Services: `@Service`, inyeccion por constructor (`@RequiredArgsConstructor`)
- Controllers: `@RestController`, ruta `/api/v1/{module}`
- DTOs: `{Entity}RequestDTO`, `{Entity}ResponseDTO`
- Mappers: interfaces con `@Mapper` (MapStruct)
- Excepciones: custom extienden `RuntimeException`
- Manejo global: `@ControllerAdvice`
- Validacion: `@Valid` en controllers

### Frontend (TypeScript)
- Componentes funcionales + hooks
- Archivos PascalCase: `ContactCard.tsx`
- Types/interfaces en archivo separado `types.ts`
- Un Zustand store por feature
- Tailwind para estilos, no CSS custom
- API client centralizado en `lib/api.ts`

### Principios
- **SOLID**: Clases con una sola responsabilidad
- **DRY**: Logica compartida en `shared/`
- **KISS**: Soluciones simples antes que complejas
- **YAGNI**: No construir lo que no se necesita hoy

---

## Proximo: Etapa 5 — Deploy

### Tareas

1. Elegir plataforma de deploy (Railway o Render)
2. Configurar PostgreSQL en produccion
3. Deploy del backend con variables de entorno
4. Deploy del frontend (Vercel o Netlify)
5. Configurar dominio personalizado (opcional)
6. Verificar funcionalidad completa en produccion

### Configuracion Minima

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

## Notas Importantes

### PostgreSQL Local
- Host: `127.0.0.1` (NO `localhost`)
- Port: `5432`
- Database: `syncria_db`
- User: `syncria_user`
- Password: `secret` (dev)

### JWT Secret
- Generar: `openssl rand -base64 32`
- Minimo: 256 bits (32 bytes) en Base64
- Validacion en startup

### Cookies
- httpOnly: true
- secure: false en dev, true en prod
- sameSite: Lax
- domain: vacio en dev, configurable en prod

### Limitaciones Conocidas
- Tests backend corren con H2 (no PostgreSQL real)
- Sin CI/CD pipeline
- Sin observabilidad (Sentry, monitoring)
- CSP con 'unsafe-inline' para TailwindCSS
- Sin JWT blacklist/revocation
