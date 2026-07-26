# Syncria

Plataforma CRM inteligente y modular, preparada para evolucionar a SaaS multi-tenant.

## Descripción

Syncria es una plataforma CRM (Customer Relationship Management) diseñada para ayudar a negocios de múltiples industrias a gestionar sus relaciones con clientes de manera eficiente, automatizada y escalable.

### Características Principales

- **Modular**: Módulos independientes que se activan por industria
- **Multi-industria**: Adaptado a veterinarias, clínicas, inmobiliarias, restaurantes, etc.
- **IA Integrada**: Asistente inteligente para automatizar tareas
- **Multi-tenant Preparado**: Arquitectura lista para SaaS
- **API First**: Backend REST, frontend React

## Stack Tecnológico

| Capa | Tecnología |
|------|------------|
| Backend | Java 21 + Spring Boot 3.x + Maven |
| Frontend | React + TypeScript + Vite + TailwindCSS |
| Base de datos | PostgreSQL 16 |
| Migraciones | Flyway |
| Estado | Zustand |
| Seguridad | Spring Security + JWT |
| Docker | Docker Compose |

## Requisitos Previos

- [Docker](https://docs.docker.com/get-docker/) v20.10+
- [Docker Compose](https://docs.docker.com/compose/install/) v2.0+
- [Java 21](https://adoptium.net/) (para desarrollo backend)
- [Node.js 20+](https://nodejs.org/) (para desarrollo frontend)
- [Maven 3.9+](https://maven.apache.org/) (para desarrollo backend)

## Inicio Rápido

### 1. Clonar el repositorio

```bash
git clone https://github.com/kevin/syncria.git
cd syncria
```

### 2. Configurar variables de entorno

```bash
cp .env.example .env
# Editar .env con tus configuraciones
```

### 3. Levantar servicios

```bash
docker-compose up -d
```

### 4. Verificar servicios

```bash
docker-compose ps
```

## Servicios Disponibles

| Servicio | URL | Descripción |
|----------|-----|-------------|
| PostgreSQL | `localhost:5432` | Base de datos |
| PGAdmin | `http://localhost:5050` | Interfaz web para BD |
| n8n | `http://localhost:5678` | Automatizaciones |

### Credenciales por Defecto

#### PGAdmin
- **Email**: `admin@syncria.com`
- **Password**: `admin`

#### PostgreSQL
- **Database**: `syncria_db`
- **User**: `syncria_user`
- **Password**: `secret`

## Desarrollo

### Backend (Spring Boot)

```bash
cd backend

# Ejecutar en desarrollo
./mvnw spring-boot:run

# Ejecutar tests
./mvnw test

# Build completo
./mvnw clean package
```

### Frontend (React + Vite)

```bash
cd frontend

# Instalar dependencias
npm install

# Ejecutar en desarrollo
npm run dev

# Build producción
npm run build

# Tests
npm run test
```

## Estructura del Proyecto

```
syncria/
├── backend/                    # Spring Boot API
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/syncria/
│   │   │   └── resources/
│   │   └── test/
│   └── pom.xml
├── frontend/                   # React + Vite
│   ├── src/
│   ├── package.json
│   └── vite.config.ts
├── docker/                     # Docker configurations
│   ├── postgres/
│   └── pgadmin/
├── docs/                       # Documentación
│   ├── architecture/
│   ├── diagrams/
│   └── adr/
├── docker-compose.yml
├── .env.example
├── .gitignore
└── README.md
```

## Arquitectura

Ver [docs/architecture/overview.md](docs/architecture/overview.md) para detalles de la arquitectura.

### Principios

- **Modular por capas**: Cada módulo sigue el mismo patrón
- **Multi-tenant**: Campo `company_id` en todas las entidades
- **SOLID**: Principios de diseño orientado a objetos
- **Clean Code**: Código legible y mantenible

## API Documentation

La documentación de la API se generará automáticamente con SpringDoc OpenAPI.

Una vez que el backend esté ejecutándose:
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

## Testing

### Backend

```bash
cd backend

# Todos los tests
./mvnw test

# Test específico
./mvnw test -Dtest=UserServiceTest

# Cobertura
./mvnw verify jacoco:report
```

### Frontend

```bash
cd frontend

# Todos los tests
npm run test

# Cobertura
npm run test:coverage

# Lint
npm run lint

# Type check
npm run typecheck
```

## Contribuir

1. Crear una rama feature (`git checkout -b feature/nueva-funcionalidad`)
2. Hacer commit (`git commit -m 'feat: agregar nueva funcionalidad'`)
3. Push a la rama (`git push origin feature/nueva-funcionalidad`)
4. Abrir Pull Request

### Convenciones de Commits

- `feat:`: Nueva funcionalidad
- `fix:`: Corrección de bug
- `docs:`: Documentación
- `style:`: Formato (no afecta el código)
- `refactor:`: Refactorización
- `test:`: Tests
- `chore:`: Mantenimiento

## Git Flow

```
main          ← producción
develop       ← integración
feature/*     ← nuevas funcionalidades
bugfix/*      ← corrección de bugs
```

## Documentación

- [Visión del Producto](docs/vision.md)
- [Definición MVP](docs/mvp.md)
- [Product Backlog](docs/backlog.md)
- [Roadmap](docs/roadmap.md)
- [Arquitectura](docs/architecture/overview.md)
- [ADR-001: Arquitectura Modular](docs/adr/001-architecture.md)
- [ADR-002: Patrón de Módulo](docs/adr/002-modular-layered.md)
- [ADR-003: Multi-tenant](docs/adr/003-multi-tenant.md)
- [ADR-004: Estado Frontend](docs/adr/004-state-management.md)

## Licencia

MIT License - Ver [LICENSE](LICENSE) para detalles.

## Contacto

- **Kevin** - kevin@syncria.com
- **GitHub**: https://github.com/kevin/syncria

---

*Construido con pasión por Kevin - 2026*
