# Syncria - AI Agent Instructions

Plataforma CRM inteligente y modular, preparada para evolucionar a SaaS multi-tenant.

## Stack Tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Java 17 + Spring Boot 3.x + Maven |
| Frontend | React + TypeScript + Vite + TailwindCSS |
| Base de datos | PostgreSQL 16 + H2 (test unitario) |
| Migraciones | Flyway |
| Estado global | Zustand |
| Seguridad | Spring Security + JWT |
| IA | OpenAI / Gemini |
| Automatizaciones | n8n |
| Deploy | Docker Compose |

## Arquitectura

Modular por capas. Cada módulo sigue el mismo patrón:

```
backend/src/main/java/com/syncria/
├── module/
│   ├── user/
│   │   ├── controller/    # REST endpoints
│   │   ├── service/       # Lógica de negocio
│   │   ├── repository/    # Acceso a datos
│   │   ├── dto/           # Request/Response
│   │   ├── mapper/        # Entity ↔ DTO
│   │   ├── entity/        # JPA entities
│   │   ├── validation/    # Validaciones
│   │   └── exception/     # Excepciones del módulo
│   ├── contact/
│   ├── deal/
│   └── company/
├── security/              # JWT, filtros, config
├── config/                # Configuración general
├── shared/                # Utilidades compartidas
└── SyncriaApplication.java
```

**Regla clave**: Las dependencias van hacia adentro. Los módulos NO dependen entre sí directamente — usan interfaces en `shared/` si necesitan comunicarse.

## Multi-tenant

Todas las entidades incluyen campo `companyId` para aislamiento futuro:

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

- Módulo `Company/Workspace` gestiona tenants
- Filtro JPA por defecto para aislamiento
- Header `X-Company-Id` en requests autenticados
- `TenantContext` (ThreadLocal) almacena company actual

## Frontend

```
frontend/src/
├── components/           # Componentes compartidos
├── features/             # Módulos por dominio
│   ├── auth/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── store/        # Zustand stores
│   │   └── types/
│   ├── contacts/
│   ├── deals/
│   └── dashboard/
├── layouts/              # Layouts (MainLayout, AuthLayout)
├── lib/                  # API client, utils
├── hooks/                # Hooks compartidos
├── stores/               # Zustand stores globales
└── styles/               # Tailwind config
```

## Git Flow

```
main          ← producción, merge solo desde develop
develop       ← integración, base para features
feature/*     ← nueva funcionalidad (feature/user-auth)
bugfix/*      ← corrección de bugs (bugfix/login-error)
```

## Comandos Esenciales

### Backend
```bash
cd backend
./mvnw spring-boot:run                    # Ejecutar app
./mvnw test                               # Todos los tests
./mvnw test -Dtest=UserServiceTest        # Test específico
./mvnw test -Dtest=UserServiceTest#method # Método específico
./mvnw clean package                      # Build completo
./mvnw verify                             # Tests + integration tests
```

### Frontend
```bash
cd frontend
npm install       # Instalar deps
npm run dev       # Desarrollo (http://localhost:5173)
npm run build     # Build producción
npm run lint      # Linting
npm run typecheck # Verificar tipos
npm run test      # Tests
```

### Docker
```bash
docker-compose up -d          # Levantar todo
docker-compose logs -f backend # Logs backend
docker-compose down           # Parar todo
```

## Docker Compose

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| postgres | 5432 | Base de datos |
| pgadmin | 5050 | Admin BD (web UI) |
| backend | 8080 | API REST |
| frontend | 5173 | App React (dev) |
| n8n | 5678 | Automatizaciones |

## Flyway

Scripts en `backend/src/main/resources/db/migration/`:

```
V1__create_company_table.sql
V2__create_user_table.sql
V3__add_company_id_to_users.sql
```

**Regla**: Nunca editar un script ya ejecutado. Siempre crear nuevo script con versión siguiente.

## Testing

### Backend (80% cobertura mínima)
- **Unit**: Services con Mockito + H2
- **Integration**: `@SpringBootTest` + PostgreSQL real (Docker)
- **API**: `@WebMvcTest` + MockMvc

### Frontend (70% cobertura mínima)
- **Unit**: Vitest + React Testing Library
- **E2E**: Playwright (futuro)

### Cobertura
```bash
cd backend && ./mvnw verify jacoco:report
cd frontend && npm run test:coverage
```

## Reglas de Código

### Backend (Java)
- Package raíz: `com.syncria`
- Services: `@Service`, inyección por constructor (`@RequiredArgsConstructor`)
- Controllers: `@RestController`, ruta `/api/v1/{module}`
- DTOs: `{Entity}RequestDTO`, `{Entity}ResponseDTO`
- Mappers: interfaces con `@Mapper` (MapStruct) o manuales
- Excepciones: custom extienden `RuntimeException`
- Manejo global: `@ControllerAdvice`
- Validación: `@Valid` en controllers

### Frontend (TypeScript)
- Componentes funcionales + hooks
- Archivos PascalCase: `ContactCard.tsx`
- Types/interfaces en archivo separado `types.ts`
- Un Zustand store por feature
- Tailwind para estilos, no CSS custom
- API client centralizado en `lib/api.ts`

### Principios
- **SOLID**: Clases con una sola responsabilidad, dependencias por interfaz
- **DRY**: Lógica compartida en `shared/`
- **KISS**: Soluciones simples antes que complejas
- **YAGNI**: No construir lo que no se necesita hoy

## Variables de Entorno

`.env` (nunca commitear):
```
DATABASE_URL=jdbc:postgresql://localhost:5432/syncria_db
DATABASE_USERNAME=syncria
DATABASE_PASSWORD=secret
JWT_SECRET=tu-clave-secreta-aqui
OPENAI_API_KEY=sk-...
GEMINI_API_KEY=...
PGADMIN_DEFAULT_EMAIL=admin@syncria.com
PGADMIN_DEFAULT_PASSWORD=admin
```

## Documentación del Proyecto

```
docs/
├── vision.md              # Visión del producto
├── roadmap.md             # Hitos y plan de desarrollo
├── mvp.md                 # Definición del MVP
├── backlog.md             # Product Backlog e Historias de Usuario
├── sprint-01.md           # Plan Sprint 1
├── architecture/
│   └── overview.md        # Arquitectura general
├── diagrams/              # Diagramas Mermaid
│   ├── system-overview.mmd
│   ├── entity-relationship.mmd
│   ├── request-flow.mmd
│   └── multi-tenant-overview.mmd
└── adr/                   # Architecture Decision Records
    ├── 001-architecture.md
    ├── 002-modular-layered.md
    ├── 003-multi-tenant.md
    └── 004-state-management.md
```

## Metodología

- **Scrum simplificado**: Sprints de 1-2 semanas
- **Git Flow**: main → develop → feature/* → develop → main
- **Pull Requests**: Requeridos para merge a develop/main
- **Code Reviews**: Obligatorios antes de merge
- **Conventional Commits**: `feat:`, `fix:`, `docs:`, `refactor:`, `test:`
- **Desarrollo guiado por documentación**: No código sin documentación previa

## Notas para Kevin

**Arquitectura modular por capas**: Las capas (controller → service → repository) están organizadas por módulo de negocio, no por dependencia de framework. Más práctica para CRMs y SaaS.

**Multi-tenant**: El campo `companyId` es la base. Cada query filtra por empresa. Cuando evoluciones a SaaS real, solo necesitas:
1. Asignar `companyId` al login
2. Activar el filtro JPA
3. Aislar datos por empresa

**Flyway**: Cada cambio de BD = un nuevo archivo SQL. Nunca edites scripts ya ejecutados.

**Zustand**: Más ligero y simple que Redux. Un store por feature, sin boilerplate.
