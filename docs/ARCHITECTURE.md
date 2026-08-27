# Arquitectura — Syncria

## Vision General

Syncria sigue una arquitectura **modular por capas** donde cada modulo de negocio (auth, contact, pet, appointment, dashboard) contiene todas las capas necesarias (controller, service, repository, dto, entity). Las dependencias van hacia adentro y los modulos NO dependen entre si directamente.

```
┌─────────────────────────────────────────────────┐
│                   Frontend                       │
│         React + TypeScript + Zustand             │
│    features/ → components/ → hooks/ → store/     │
└──────────────────────┬──────────────────────────┘
                       │ REST API
┌──────────────────────▼──────────────────────────┐
│                   Backend                        │
│           Spring Boot 3.5.4 + Java 17            │
│  ┌─────────┬─────────┬─────────┬─────────┐      │
│  │  auth   │ contact │   pet   │  aptmnt │      │
│  │controlle│controlle│controlle│controlle│      │
│  │service  │service  │service  │service  │      │
│  │repositor│repositor│repositor│repositor│      │
│  │dto      │dto      │dto      │dto      │      │
│  │entity   │entity   │entity   │entity   │      │
│  │mapper   │mapper   │mapper   │mapper   │      │
│  │exceptio │exceptio │exceptio │exceptio │      │
│  └─────────┴─────────┴─────────┴─────────┘      │
│  ┌──────────────────────────────────────┐        │
│  │  shared/ (BaseEntity, Exception)     │        │
│  │  security/ (JWT, Filter, Config)     │        │
│  └──────────────────────────────────────┘        │
└──────────────────────┬──────────────────────────┘
                       │ JPA/Hibernate
┌──────────────────────▼──────────────────────────┐
│              PostgreSQL 16                        │
│           Flyway Migrations (V1-V4)              │
└─────────────────────────────────────────────────┘
```

---

## Principios de Diseno

### 1. Modularidad por Capas

Cada modulo sigue la misma estructura:

```
module/{nombre}/
├── controller/    # Endpoints REST (@RestController)
├── service/       # Logica de negocio (@Service)
├── repository/    # Acceso a datos (JpaRepository)
├── dto/           # Request/Response DTOs (Records)
├── entity/        # Entidades JPA (@Entity)
├── mapper/        # Entity <-> DTO (MapStruct)
└── exception/     # Excepciones custom del modulo
```

### 2. Unidireccionalidad

```
Controller → Service → Repository → Database
     ↓
    DTO (Request/Response)
     ↓
    Mapper (Entity ↔ DTO)
```

**Regla**: Las dependencias van hacia adentro. Un modulo NO importa de otro modulo directamente.

### 3. Multi-tenant

Todas las entidades extienden `BaseEntity` que incluye `companyId`:

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

### 4. Seguridad

```
Request → JwtAuthenticationFilter → SecurityConfig → Controller
              │
              ├─ Extrae JWT del header Authorization
              ├─ Valida token (expiracion, firma)
              ├─ Extrae companyId del token
              ├─ Cache de usuarios (TTL 5 min)
              └─ Verifica soft-delete
```

- **Rate Limiting**: 5 intentos de login por minuto por IP
- **BCrypt**: Passwords encriptados
- **JWT**: Tokens con expiracion de 24 horas

---

## Modulos

### Auth Module
- **Registro**: Crea usuario + empresa automaticamente
- **Login**: Rate limited, retorna JWT
- **JWT Filter**: Extrae y valida token en cada request

### Contact Module
- **CRUD**: Crear, listar, editar, eliminar (soft delete)
- **Busqueda**: Por nombre o email
- **Paginacion**: 20 elementos por pagina
- **Validacion**: Email unico por tenant

### Pet Module
- **CRUD**: Crear, listar, editar, eliminar (soft delete)
- **Vinculo**: Cada mascota pertenece a un contacto
- **Busqueda**: Por nombre, especie o raza

### Appointment Module
- **CRUD**: Crear, listar, editar, eliminar (soft delete)
- **Calendario**: 3 vistas (mes, semana, dia)
- **Conflict Detection**: Verifica overlap por mascota + fecha + hora
- **Estados**: SCHEDULED, COMPLETED, CANCELLED

### Dashboard Module
- **Metrics**: Total clientes, mascotas, citas
- **Hoy**: Citas del dia con estado
- **Semana**: Total de citas de la semana
- **Especies**: Distribucion de mascotas por especie
- **Accesos rapidos**: Nueva cita, nuevo cliente, nueva mascota

---

## Frontend Architecture

```
frontend/src/
├── features/              # Modulos por dominio
│   ├── auth/              # Login, Register, ProtectedRoute
│   │   ├── components/    # Paginas React
│   │   ├── store/         # Zustand store
│   │   └── types/         # Tipos TypeScript
│   ├── contacts/          # CRUD Contactos
│   ├── pets/              # CRUD Mascotas
│   ├── appointments/      # Calendario + Modales
│   └── dashboard/         # Dashboard ejecutivo
├── components/
│   ├── layout/            # Header, Sidebar
│   └── ui/                # Button, Input, Table, etc.
├── lib/                   # api.ts (Axios + JWT interceptor)
├── layouts/               # MainLayout
└── types/                 # Tipos compartidos
```

### State Management (Zustand)

Cada feature tiene su store:

```typescript
// stores/contactStore.ts
interface ContactState {
  contacts: Contact[]
  isLoading: boolean
  error: string | null
  fetchContacts: () => Promise<void>
  createContact: (data: ContactRequest) => Promise<void>
  // ...
}
```

### API Client (Axios)

```typescript
// lib/api.ts
const api = axios.create({
  baseURL: '/api/v1',
  withCredentials: true,  // Envía cookies httpOnly automáticamente
  headers: { 'Content-Type': 'application/json' }
})

// Interceptor: redirige a /login en 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)
```

**Flujo de autenticación:**
1. Login → backend establece cookie httpOnly "token"
2. Requests → navegador envía cookie automáticamente (withCredentials: true)
3. JwtAuthenticationFilter → extrae JWT desde cookie
4. Logout → backend elimina cookie

---

## Base de Datos

### Tablas (Flyway Migrations)

| Version | Tabla | Descripcion |
|---------|-------|-------------|
| V1 | companies | Empresas (multi-tenant) |
| V1 | users | Usuarios con rol |
| V2 | contacts | Clientes/duenos |
| V3 | pets | Mascotas vinculadas a contacts |
| V4 | appointments | Citas con estados |

### Relaciones

```
companies 1───N users
companies 1───N contacts
companies 1───N pets
companies 1───N appointments
contacts  1───N pets
contacts  1───N appointments
pets      1───N appointments
```

---

## Decisiones de Arquitectura (ADR)

Ver [docs/adr/](docs/adr/) para las decisiones tecnicas documentadas:

- **ADR-001**: Arquitectura modular por capas
- **ADR-002**: Multi-tenant con companyId
- **ADR-003**: JWT para autenticacion
- **ADR-004**: Zustand para estado global
- **ADR-005**: Flyway para migraciones

---

*Arquitectura — Syncria v1.0.0*
