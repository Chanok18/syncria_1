# Sprint 01 - Setup e Infraestructura

## Información del Sprint

| Campo | Valor |
|-------|-------|
| **Nombre** | Sprint 01: Setup e Infraestructura |
| **Duración** | 2 semanas (10 días laborales) |
| **Fecha Inicio** | Por definir |
| **Fecha Fin** | Por definir |
| **Puntos de Historia** | 8 |
| **Capacidad** | 40 horas |

## Objetivo del Sprint

> "Configurar toda la infraestructura base del proyecto Syncria: repositorio, contenedores Docker, backend Spring Boot, frontend React, y herramientas de calidad de código."

## Definition of Done (DoD)

- [ ] Código commiteado en rama `develop`
- [ ] Tests unitarios pasando
- [ ] Linting sin errores
- [ ] Build exitoso
- [ ] Docker Compose funcionando
- [ ] Documentación actualizada

---

## Tareas del Sprint

### Tarea 1: Configurar Repositorio Git
**Asignado**: Kevin
**Estimación**: 2 horas
**Prioridad**: Alta

**Descripción**:
Inicializar el repositorio Git con la estructura base y configuración de Git Flow.

**Subtareas**:
1. `git init` en el directorio del proyecto
2. Crear `.gitignore` completo para Java, React, Docker
3. Configurar Git Flow
4. Crear ramas `main` y `develop`
5. Crear `README.md` básico
6. Configurar hooks de pre-commit (opcional)

**Criterios de Aceptación**:
- [ ] Repositorio inicializado
- [ ] `.gitignore` excluye: `target/`, `node_modules/`, `.env`, `*.class`
- [ ] Ramas `main` y `develop` existen
- [ ] Primer commit realizado

**Archivos a crear**:
```
.gitignore
README.md
.gitconfig (opcional)
```

---

### Tarea 2: Docker Compose
**Asignado**: Kevin
**Estimación**: 4 horas
**Prioridad**: Alta

**Descripción**:
Crear `docker-compose.yml` con todos los servicios necesarios para desarrollo.

**Subtareas**:
1. Configurar servicio PostgreSQL 16
2. Configurar servicio pgAdmin
3. Configurar servicio Backend (Spring Boot)
4. Configurar servicio Frontend (React dev server)
5. Configurar servicio n8n (automatizaciones)
6. Crear `docker-compose.yml` con volumes y networks
7. Crear archivo `.env` de ejemplo

**Criterios de Aceptación**:
- [ ] `docker-compose up -d` levanta todos los servicios
- [ ] PostgreSQL accesible en puerto 5432
- [ ] pgAdmin accesible en puerto 5050
- [ ] Backend accesible en puerto 8080
- [ ] Frontend accesible en puerto 5173
- [ ] n8n accesible en puerto 5678
- [ ] Todos los servicios se conectan correctamente

**Archivos a crear**:
```
docker-compose.yml
.env.example
docker/postgres/init.sql (opcional)
```

**Configuración PostgreSQL**:
```yaml
postgres:
  image: postgres:16-alpine
  ports:
    - "5432:5432"
  environment:
    POSTGRES_DB: syncria_db
    POSTGRES_USER: syncria_user
    POSTGRES_PASSWORD: ${DATABASE_PASSWORD:-secret}
  volumes:
    - postgres_data:/var/lib/postgresql/data
```

**Configuración pgAdmin**:
```yaml
pgadmin:
  image: dpage/pgadmin4
  ports:
    - "5050:80"
  environment:
    PGADMIN_DEFAULT_EMAIL: admin@syncria.com
    PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_PASSWORD:-admin}
```

---

### Tarea 3: Backend Spring Boot
**Asignado**: Kevin
**Estimación**: 6 horas
**Prioridad**: Alta

**Descripción**:
Crear el proyecto backend con Spring Boot, configurando la estructura base y dependencias esenciales.

**Subtareas**:
1. Inicializar proyecto con Spring Initializr
2. Configurar `pom.xml` con dependencias
3. Crear estructura de paquetes
4. Configurar `application.yml`
5. Configurar Flyway para migraciones
6. Configurar Spring Security básico
7. Crear `BaseEntity` para multi-tenant
8. Crear primer endpoint de prueba
9. Configurar profiles (dev, test, prod)

**Criterios de Aceptación**:
- [ ] Proyecto compila sin errores
- [ ] `./mvnw spring-boot:run` ejecuta la app
- [ ] Endpoint `/api/v1/health` responde 200 OK
- [ ] Flyway crea la tabla `flyway_schema_history`
- [ ] Conexión a PostgreSQL funciona
- [ ] Profile `dev` funciona correctamente

**Archivos a crear**:
```
backend/
├── pom.xml
├── src/main/java/com/syncria/
│   ├── SyncriaApplication.java
│   ├── config/
│   │   └── DatabaseConfig.java
│   ├── shared/
│   │   └── entity/
│   │       └── BaseEntity.java
│   └── module/
│       └── health/
│           └── controller/
│               └── HealthController.java
├── src/main/resources/
│   ├── application.yml
│   ├── application-dev.yml
│   └── db/migration/
└── src/test/java/com/syncria/
    └── SyncriaApplicationTests.java
```

**Dependencias principales (pom.xml)**:
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
</dependencies>
```

---

### Tarea 4: Frontend React + Vite
**Asignado**: Kevin
**Estimación**: 4 horas
**Prioridad**: Alta

**Descripción**:
Crear el proyecto frontend con React, TypeScript, Vite y TailwindCSS.

**Subtareas**:
1. Inicializar proyecto con Vite
2. Configurar TypeScript estricto
3. Instalar y configurar TailwindCSS
4. Configurar ESLint y Prettier
5. Crear estructura de carpetas
6. Configurar proxy de API a backend
7. Crear componente de prueba
8. Configurar Vitest para testing

**Criterios de Aceptación**:
- [ ] `npm run dev` ejecuta el frontend
- [ ] TailwindCSS funciona (estilos aplicados)
- [ ] TypeScript compila sin errores
- [ ] ESLint configurado
- [ ] Proxy a backend funciona (localhost:5173 → localhost:8080)
- [ ] Vitest configurado

**Archivos a crear**:
```
frontend/
├── package.json
├── vite.config.ts
├── tsconfig.json
├── tailwind.config.js
├── postcss.config.js
├── index.html
├── src/
│   ├── main.tsx
│   ├── App.tsx
│   ├── index.css
│   ├── components/
│   │   └── ui/
│   │       └── Button.tsx
│   ├── lib/
│   │   └── api.ts
│   └── types/
│       └── index.ts
└── src/__tests__/
    └── App.test.tsx
```

**Dependencias principales (package.json)**:
```json
{
  "dependencies": {
    "react": "^18.3.1",
    "react-dom": "^18.3.1",
    "react-router-dom": "^6.23.0",
    "zustand": "^4.5.2",
    "axios": "^1.7.2",
    "react-hot-toast": "^2.4.1"
  },
  "devDependencies": {
    "@types/react": "^18.3.3",
    "@types/react-dom": "^18.3.0",
    "@vitejs/plugin-react": "^4.3.1",
    "autoprefixer": "^10.4.19",
    "eslint": "^8.57.0",
    "postcss": "^8.4.38",
    "tailwindcss": "^3.4.4",
    "typescript": "^5.4.5",
    "vite": "^5.3.1",
    "vitest": "^1.6.0"
  }
}
```

---

### Tarea 5: Configurar Calidad de Código
**Asignado**: Kevin
**Estimación**: 4 horas
**Prioridad**: Media

**Descripción**:
Configurar herramientas de calidad de código para backend y frontend.

**Subtareas**:
1. **Backend**:
   - Configurar Checkstyle
   - Configurar JaCoCo para cobertura
   - Configurar PMD (opcional)
   - Crear script de verificación

2. **Frontend**:
   - Configurar ESLint reglas estrictas
   - Configurar Prettier
   - Configurar Vitest con cobertura
   - Crear scripts npm para quality

**Criterios de Aceptación**:
- [ ] `./mvnw verify` pasa sin errores
- [ ] Cobertura backend > 80% configurada
- [ ] `npm run lint` pasa sin errores
- [ ] `npm run test` ejecuta tests
- [ ] Cobertura frontend > 70% configurada

**Archivos a crear**:
```
backend/
├── checkstyle.xml
├── .editorconfig
└── pom.xml (configuración JaCoCo)

frontend/
├── .eslintrc.cjs
├── .prettierrc
├── vitest.config.ts
└── package.json (scripts de quality)
```

**Scripts package.json**:
```json
{
  "scripts": {
    "dev": "vite",
    "build": "tsc && vite build",
    "lint": "eslint . --ext ts,tsx --report-unused-disable-directives --max-warnings 0",
    "lint:fix": "eslint . --ext ts,tsx --fix",
    "format": "prettier --write \"src/**/*.{ts,tsx,css}\"",
    "typecheck": "tsc --noEmit",
    "test": "vitest",
    "test:coverage": "vitest run --coverage",
    "test:ui": "vitest --ui"
  }
}
```

---

### Tarea 6: Configurar CI Básico (Opcional)
**Asignado**: Kevin
**Estimación**: 2 horas
**Prioridad**: Baja

**Descripción**:
Configurar pipeline básico de integración continua con GitHub Actions.

**Subtareas**:
1. Crear workflow de GitHub Actions
2. Configurar jobs para backend y frontend
3. Ejecutar lint, test, build
4. Reportar cobertura

**Criterios de Aceptación**:
- [ ] Workflow ejecuta en cada PR
- [ ] Jobs de backend y frontend funcionan
- [ ] Build exitoso en CI

**Archivos a crear**:
```
.github/
└── workflows/
    └── ci.yml
```

**Workflow básico**:
```yaml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  backend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      - name: Build with Maven
        run: ./mvnw verify

  frontend:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Node.js
        uses: actions/setup-node@v4
        with:
          node-version: '20'
      - run: npm ci
      - run: npm run lint
      - run: npm run typecheck
      - run: npm run test
      - run: npm run build
```

---

## Criterios de Aceptación del Sprint

### Infraestructura
- [ ] Docker Compose levanta todos los servicios
- [ ] PostgreSQL funcional y accesible
- [ ] pgAdmin funcional y accesible
- [ ] Backend compilable y ejecutable
- [ ] Frontend compilable y ejecutable

### Código
- [ ] Repositorio con Git Flow configurado
- [ ] Estructura de carpetas creada
- [ ] Configuración de calidad implementada
- [ ] Tests base funcionando

### Documentación
- [ ] README.md actualizado
- [ ] `.env.example` creado
- [ ] Guía de setup en README

---

## Riesgos del Sprint

| Riesgo | Probabilidad | Impacto | Mitigación |
|--------|--------------|---------|------------|
| Configuración Docker compleja | Media | Alto | Usar imágenes oficiales, documentar |
| Dependencias Spring Boot | Baja | Medio | Usar versiones estables |
| Configuración TailwindCSS | Baja | Bajo | Seguir documentación oficial |

---

## Entregables del Sprint

1. **Repositorio Git** con Git Flow
2. **Docker Compose** con todos los servicios
3. **Backend Spring Boot** funcional
4. **Frontend React** funcional
5. **Herramientas de calidad** configuradas
6. **Documentación** de setup

---

## Notas para Kevin

### Comandos útiles durante este sprint:

```bash
# Docker
docker-compose up -d          # Levantar servicios
docker-compose down           # Parar servicios
docker-compose logs -f        # Ver logs
docker-compose ps             # Ver estado

# Backend
cd backend
./mvnw spring-boot:run        # Ejecutar app
./mvnw test                   # Ejecutar tests
./mvnw verify                 # Tests + build
./mvnw clean package          # Limpiar y buildear

# Frontend
cd frontend
npm install                   # Instalar dependencias
npm run dev                   # Ejecutar en desarrollo
npm run build                 # Build producción
npm run lint                  # Verificar código
npm run test                  # Ejecutar tests
```

### Errores comunes a evitar:

1. **Docker**: No olvidar crear el volume `postgres_data`
2. **Backend**: No olvidar configurar el profile `dev`
3. **Frontend**: No olvidar configurar el proxy de API
4. **Git**: Siempre trabajar en ramas feature, nunca directamente en develop

### Checklist diario:

- [ ] ¿Docker está corriendo?
- [ ] ¿Backend responde?
- [ ] ¿Frontend carga?
- [ ] ¿Tests pasan?
- [ ] ¿Commiteo al final del día?

---

*Sprint 01 Plan - Syncria v1.0*
*Última actualización: 2026*
