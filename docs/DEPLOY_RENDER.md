# Guia de Deployment - Render (v1.4.0)

## Plataforma Elegida: Render

**Por que Render:**
- Free tier generoso (750 horas/mes)
- PostgreSQL manejado incluido
- SSL automatico
- Despliegue automatico desde GitHub
- Sin configuracion compleja

---

## Paso 1: Preparar Repositorio

### Archivos necesarios (ya creados)

```
syncria_1/
├── backend/
│   ├── Dockerfile          ✅ Creado
│   └── pom.xml
├── frontend/
│   ├── Dockerfile          ✅ Creado
│   ├── nginx.conf          ✅ Creado
│   └── package.json
└── render.yaml             ✅ Creado (opcional, para Infrastructure as Code)
```

### Subir a GitHub

```bash
cd C:\Users\user\Dev\syncria_1
git add .
git commit -m "feat: add deployment configs for Render"
git push origin develop
```

---

## Paso 2: Crear Cuenta en Render

1. Ir a [render.com](https://render.com)
2. Sign up con GitHub
3. Autorizar acceso al repositorio

---

## Paso 3: Crear PostgreSQL Database

1. En Render Dashboard → **New** → **PostgreSQL**
2. Configurar:
   - **Name**: `syncria-db`
   - **Database**: `syncria_db`
   - **User**: `syncria_user`
   - **Region**: Oregon (US West) o la mas cercana
   - **Plan**: Free
3. Click **Create Database**
4. **IMPORTANTE**: Copiar el **Internal Database URL** (formato: `postgresql://user:pass@host:port/dbname`)
5. Guardar este URL para usar en el backend

---

## Paso 4: Crear Backend Service

1. En Render Dashboard → **New** → **Web Service**
2. Connect GitHub repository → Seleccionar `syncria_1`
3. Configurar:
   - **Name**: `syncria-backend`
   - **Runtime**: `Docker`
   - **Region**: Oregon (US West)
   - **Plan**: Free
   - **Dockerfile Path**: `backend/Dockerfile`
   - **Docker Context**: `.` (raiz del repo, NO la carpeta backend)

4. **Environment Variables** (agregar todas):

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<Internal Database URL del Paso 3>
DATABASE_USERNAME=syncria_user
DATABASE_PASSWORD=<password de la BD>
JWT_SECRET=<generar con: openssl rand -base64 32>
CORS_ALLOWED_ORIGINS=https://syncria-frontend.onrender.com
COOKIE_DOMAIN=syncria-frontend.onrender.com
COOKIE_SECURE=true
```

5. **Advanced** → **Health Check Path**: `/api/v1/health`
6. Click **Create Web Service**
7. Esperar a que el deploy termine (5-10 minutos)
8. Copiar la URL del backend (algo como: `https://syncria-backend.onrender.com`)

---

## Paso 5: Crear Frontend Service

1. En Render Dashboard → **New** → **Static Site**
2. Connect GitHub repository → Seleccionar `syncria_1`
3. Configurar:
   - **Name**: `syncria-frontend`
   - **Branch**: `develop`
   - **Build Command**: `cd frontend && npm install && npm run build`
   - **Publish Directory**: `frontend/dist`

4. **Environment Variables**:

```
VITE_API_URL=https://syncria-backend.onrender.com/api/v1
```

5. Click **Create Static Site**
6. Esperar a que el deploy termine (2-5 minutos)
7. Copiar la URL del frontend (algo como: `https://syncria-frontend.onrender.com`)

---

## Paso 6: Actualizar CORS del Backend

Despues de tener la URL del frontend, actualizar las variables de entorno del backend:

1. Ir a **syncria-backend** → **Environment**
2. Actualizar:
   ```
   CORS_ALLOWED_ORIGINS=https://syncria-frontend.onrender.com
   COOKIE_DOMAIN=syncria-frontend.onrender.com
   ```
3. Guardar (auto-deploy)

---

## Paso 7: Verificar Flyway

Flyway se ejecuta automaticamente al iniciar el backend. Verificar:

1. Ir a **syncria-backend** → **Logs**
2. Buscar mensajes como:
   ```
   Successfully applied 5 migrations to database "syncria_db"
   ```

---

## Paso 8: Probar la Aplicacion

### URLs

- **Frontend**: `https://syncria-frontend.onrender.com`
- **Backend**: `https://syncria-backend.onrender.com`

### Pruebas

1. Abrir el frontend
2. **Health Check**: `https://syncria-backend.onrender.com/api/v1/health`
3. **Registro**: Crear usuario nuevo
4. **Login**: Iniciar sesion
5. **Cookie httpOnly**: Verificar en DevTools → Application → Cookies
6. **CRUD Contactos**: Crear, editar, eliminar
7. **CRUD Mascotas**: Crear, editar, eliminar
8. **CRUD Citas**: Crear, editar, eliminar
9. **Dashboard**: Verificar metricas

---

## Variables de Entorno - Resumen

### Backend

| Variable | Valor | Descripcion |
|----------|-------|-------------|
| `SPRING_PROFILES_ACTIVE` | `prod` | Profile de produccion |
| `DATABASE_URL` | `postgresql://...` | URL de PostgreSQL |
| `DATABASE_USERNAME` | `syncria_user` | Usuario BD |
| `DATABASE_PASSWORD` | `<secret>` | Password BD |
| `JWT_SECRET` | `<generar>` | Secreto JWT (min 256 bits) |
| `CORS_ALLOWED_ORIGINS` | `https://syncria-frontend.onrender.com` | Dominio frontend |
| `COOKIE_DOMAIN` | `syncria-frontend.onrender.com` | Dominio cookies |
| `COOKIE_SECURE` | `true` | Solo HTTPS |

### Frontend

| Variable | Valor | Descripcion |
|----------|-------|-------------|
| `VITE_API_URL` | `https://syncria-backend.onrender.com/api/v1` | URL del backend |

---

## Generar JWT_SECRET

```bash
# En terminal
openssl rand -base64 32

# Ejemplo output: abc123def456ghi789jkl012mno345pqr678stu901
```

Copiar el output y usarlo como `JWT_SECRET`.

---

## Troubleshooting

### Backend no inicia
- Verificar que `DATABASE_URL` usa `postgresql://` (no `jdbc:postgresql://`)
- Verificar que `JWT_SECRET` tiene al menos 32 caracteres

### CORS errors
- Verificar que `CORS_ALLOWED_ORIGINS` coincide exactamente con la URL del frontend
- Incluir `https://` en la URL

### Cookies no funcionan
- Verificar que `COOKIE_DOMAIN` coincide con el dominio del frontend
- Verificar que `COOKIE_SECURE=true`
- Abrir en HTTPS (no HTTP)

### Flyway no ejecuta migrations
- Verificar logs del backend
- Verificar que la BD tiene permisos de escritura

---

## Costos (Free Tier)

| Servicio | Costo | Limite |
|----------|-------|--------|
| PostgreSQL | $0 | 90 dias, 100MB |
| Backend | $0 | 750 horas/mes |
| Frontend | $0 | Ilimitado |
| **Total** | **$0** | — |

**Nota**: El PostgreSQL free tier expira a los 90 dias. Para produccion real, considerar plan pago ($7/mes).

---

*Guia de Deployment Render — Syncria v1.4.0*
