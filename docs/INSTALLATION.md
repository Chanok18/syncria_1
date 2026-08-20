# Guia de Instalacion — Syncria

## Requisitos Previos

| Requisito | Version Minima | Verificar |
|-----------|----------------|-----------|
| Java (JDK) | 17+ | `java -version` |
| Node.js | 20+ | `node -version` |
| npm | 9+ | `npm -version` |
| Git | 2.40+ | `git --version` |
| Docker (opcional) | 24+ | `docker --version` |
| Docker Compose (opcional) | 2.20+ | `docker compose version` |

---

## Instalacion Rapida (5 minutos)

### 1. Clonar el repositorio

```bash
git clone https://github.com/kevin-syncria/syncria.git
cd syncria
```

### 2. Backend

```bash
cd backend

# Windows
mvnw.cmd spring-boot:run

# Unix/Mac
./mvnw spring-boot:run
```

El backend estara disponible en `http://localhost:8080`.

### 3. Frontend (otra terminal)

```bash
cd frontend
npm install
npm run dev
```

El frontend estara disponible en `http://localhost:5173`.

### 4. Crear usuario

1. Abrir `http://localhost:5173/register`
2. Completar el formulario
3. Login automatico

---

## Instalacion con Docker (PostgreSQL)

### 1. Iniciar base de datos

```bash
docker-compose up -d postgres pgadmin
```

### 2. Verificar

```bash
docker-compose ps
# postgres  -> Running (port 5432)
# pgadmin   -> Running (port 5050)
```

### 3. pgAdmin

- URL: `http://localhost:5050`
- Email: `admin@syncria.com`
- Password: `admin`

### 4. Conectar a la BD

En pgAdmin:
1. Right click Servers → Register → Server
2. General: Name = `Syncria`
3. Connection:
   - Host: `postgres`
   - Port: `5432`
   - Database: `syncria_db`
   - Username: `syncria_user`
   - Password: `secret`

---

## Variables de Entorno

### Backend (application.yml)

Las variables se configuran en `backend/src/main/resources/`:

| Variable | Descripcion | Default (test) |
|----------|-------------|----------------|
| `DATABASE_URL` | URL de conexion | `jdbc:h2:mem:testdb` |
| `DATABASE_USERNAME` | Usuario BD | `sa` |
| `DATABASE_PASSWORD` | Password BD | (vacio) |
| `JWT_SECRET` | Secreto JWT (min 256 bits) | Requerido |
| `JWT_EXPIRATION` | Expiracion JWT (ms) | 86400000 (24h) |

### Frontend (vite.config.ts)

El proxy esta configurado automaticamente en `vite.config.ts`:

```typescript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    }
  }
}
```

---

## Estructura de Directorios

```
syncria/
├── backend/                     # Spring Boot API
│   ├── src/main/java/com/syncria/
│   │   ├── config/              # DatabaseConfig
│   │   ├── module/              # Modulos de negocio
│   │   │   ├── auth/            # Autenticacion
│   │   │   ├── contact/         # Contactos
│   │   │   ├── pet/             # Mascotas
│   │   │   ├── appointment/     # Citas
│   │   │   ├── dashboard/       # Dashboard
│   │   │   ├── company/         # Multi-tenant
│   │   │   └── user/            # Usuarios
│   │   ├── security/            # JWT, Filtros
│   │   └── shared/              # Excepciones, BaseEntity
│   ├── src/main/resources/      # Config, Flyway
│   ├── src/test/                # Tests unitarios
│   └── pom.xml                  # Dependencias Maven
├── frontend/                    # React + Vite
│   ├── src/
│   │   ├── features/            # Modulos por dominio
│   │   ├── components/          # Componentes compartidos
│   │   ├── lib/                 # API client
│   │   └── layouts/             # Layouts
│   ├── package.json
│   └── vite.config.ts
├── docs/                        # Documentacion
├── docker/                      # Config Docker
├── docker-compose.yml
└── .env.example
```

---

## Comandos de Desarrollo

### Backend

```bash
cd backend

./mvnw spring-boot:run                    # Ejecutar
./mvnw test                               # Todos los tests (52)
./mvnw test -Dtest=DashboardServiceTest   # Test especifico
./mvnw compile                            # Compilar
./mvnw clean package                      # Build completo
./mvnw verify                             # Tests + integration
```

### Frontend

```bash
cd frontend

npm install                               # Instalar dependencias
npm run dev                               # Desarrollo (hot reload)
npm run build                             # Build produccion
npm run lint                              # Linting
npm run typecheck                         # Verificar tipos
npm run test                              # Tests (6)
npm run test:watch                        # Tests en watch mode
```

### Docker

```bash
docker-compose up -d                      # Levantar todo
docker-compose down                       # Parar todo
docker-compose logs -f backend            # Logs del backend
docker-compose ps                         # Estado de servicios
```

---

## Solucion de Problemas

### Puerto 8080 en uso (Windows)

```bash
# Matar proceso que usa el puerto
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

### Port conflict with XAMPP/Tomcat

Detener Apache/Tomcat en XAMPP Control Panel, o matar el proceso:

```bash
taskkill /F /IM java.exe
```

### MapStruct bean not found

Ejecutar compilation antes de run:

```bash
cd backend
./mvnw compile
./mvnw spring-boot:run
```

### Frontend no conecta al backend

Verificar que el proxy esta configurado en `vite.config.ts` y que el backend esta corriendo en puerto 8080.

---

## Contacto

- **Issues**: https://github.com/kevin-syncria/syncria/issues
- **Email**: kevin@syncria.com

---

*Guia de Instalacion — Syncria v1.0.0*
