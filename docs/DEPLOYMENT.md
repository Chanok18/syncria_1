# Guia de Deployment — Syncria

## Opciones de Deployment

| Opcion | Complejidad | Costo | Recomendado para |
|--------|-------------|-------|------------------|
| Railway | Baja | Free tier | MVP, Portfolio |
| Render | Baja | Free tier | MVP, Portfolio |
| Fly.io | Media | Free tier | Produccion |
| Vercel (frontend) | Baja | Free tier | Frontend estatico |
| AWS ECS | Alta | Pago | Produccion a escala |

---

## Deployment en Railway (Recomendado)

### Backend

1. Crear cuenta en [railway.app](https://railway.app)
2. New Project → Deploy from GitHub repo
3. Seleccionar la carpeta `backend/`
4. Configurar variables de entorno:

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=jdbc:postgresql://... 
DATABASE_USERNAME=...
DATABASE_PASSWORD=...
JWT_SECRET=...
```

5. Railway detecta automaticamente el `pom.xml`
6. Deploy automatico

### Frontend

1. New Project → Deploy from GitHub repo
2. Seleccionar la carpeta `frontend/`
3. Build Command: `npm run build`
4. Output Directory: `dist`
5. Configurar variable:

```
VITE_API_URL=https://tu-backend.railway.app
```

### Dominio

1. En Railway, Settings → Networking
2. Agregar dominio personalizado
3. Configurar DNS en tu proveedor de dominio

---

## Deployment en Render

### Backend

1. Crear cuenta en [render.com](https://render.com)
2. New → Web Service
3. Connect GitHub repository
4. Configurar:
   - Name: `syncria-backend`
   - Runtime: `Java`
   - Build Command: `cd backend && ./mvnw clean package -DskipTests`
   - Start Command: `cd backend && java -jar target/*.jar`
   - Port: `8080`

5. Environment Variables:

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=...
JWT_SECRET=...
```

### Frontend

1. New → Static Site
2. Connect GitHub repository
3. Configurar:
   - Name: `syncria-frontend`
   - Build Command: `cd frontend && npm install && npm run build`
   - Publish Directory: `frontend/dist`

---

## Deployment con Docker

### Backend Dockerfile

```dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY backend/pom.xml .
COPY backend/mvnw .
COPY backend/.mvn .mvn
RUN ./mvnw dependency:go-offline
COPY backend/src ./src
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Frontend Dockerfile

```dockerfile
FROM node:20-alpine AS build
WORKDIR /app
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ .
RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
```

### nginx.conf

```nginx
server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;

    location /api {
        proxy_pass http://backend:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location / {
        try_files $uri $uri/ /index.html;
    }
}
```

### Docker Compose Completo

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:16-alpine
    environment:
      POSTGRES_DB: syncria_db
      POSTGRES_USER: syncria_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data

  backend:
    build: ./backend
    environment:
      DATABASE_URL: jdbc:postgresql://postgres:5432/syncria_db
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      - postgres

  frontend:
    build: ./frontend
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  postgres_data:
```

---

## Variables de Entorno para Produccion

### Backend

| Variable | Descripcion | Requerido |
|----------|-------------|-----------|
| `SPRING_PROFILES_ACTIVE` | Profile activo | Si (`prod`) |
| `DATABASE_URL` | URL de PostgreSQL | Si |
| `DATABASE_USERNAME` | Usuario BD | Si |
| `DATABASE_PASSWORD` | Password BD | Si |
| `JWT_SECRET` | Secreto JWT (min 256 bits) | Si |

### Frontend

| Variable | Descripcion | Requerido |
|----------|-------------|-----------|
| `VITE_API_URL` | URL del backend | Si |

---

## Post-Deployment

### 1. Verificar health check

```bash
curl https://tu-app.railway.app/api/v1/health
# Deberia retornar: {"status":"UP","version":"1.0.0"}
```

### 2. Crear primer usuario

Abrir `https://tu-frontend.vercel.app/register` y crear una cuenta.

### 3. Monitorear

- Railway: Dashboard → Logs
- Render: Dashboard → Logs
- Sentry: Configurar para error tracking

---

## Dominio y SSL

### Railway
- Dominios gratuitos: `*.up.railway.app`
- SSL automatico con Let's Encrypt

### Render
- Dominios gratuitos: `*.onrender.com`
- SSL automatico

### Dominio personalizado
1. Comprar dominio (Namecheap, Cloudflare, etc.)
2. Configurar DNS:
   - A Record → IP del backend
   - CNAME → URL del frontend
3. SSL automatico con Let's Encrypt

---

*Guia de Deployment — Syncria v1.0.0*
