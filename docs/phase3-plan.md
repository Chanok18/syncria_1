# Fase 3 — Production Hardening + Deploy

**Proyecto**: Syncria v1.1.0 → v1.2.0
**Fecha**: 2026-08-23
**Rol**: Senior Backend/DevOps/Cloud Engineer
**Objetivo**: Llevar Syncria de "funciona en local" a "desplegado en producción"

---

## 1. Auditoría del Estado Actual

### 1.1 Seguridad — Hallazgos

| # | Hallazgo | Severidad | Estado actual |
|---|----------|-----------|---------------|
| S1 | JWT almacenado en `localStorage` | ALTA | `authStore.ts:73` persiste token en localStorage; `api.ts:11` lo envía via `Authorization: Bearer` header |
| S2 | Token visible en respuesta HTTP | MEDIA | `AuthController.java:33-34` retorna token en body JSON. Aceptable para MVP, mejorable con httpOnly cookie |
| S3 | CORS hardcoded a `localhost:5173` | ALTA | `SecurityConfig.java:63` — no funcionará en producción con dominio real |
| S4 | Rate limiting solo en `/login` | MEDIA | `RateLimitingFilter.java:27` — otros endpoints no tienen protección |
| S5 | Rate limiting in-memory (ConcurrentHashMap) | MEDIA | Se resetea al reiniciar instancia; en producción con múltiples instancias no comparte estado |
| S6 | `JWT_SECRET` hardcoded en `application-dev.yml:18` | BAJA | Es solo para dev, pero fácil de olvidar en un code review |
| S7 | Swagger expuesto sin auth | BAJA | `SecurityConfig.java:40` — paths de Swagger en `permitAll()`. En producción expondría información interna |
| S8 | Sin headers de seguridad HTTP | MEDIA | No hay `X-Content-Type-Options`, `X-Frame-Options`, `Strict-Transport-Security` |

### 1.2 Configuración — Hallazgos

| # | Hallazgo | Severidad | Estado actual |
|---|----------|-----------|---------------|
| C1 | `spring.profiles.active: dev` hardcoded en `application.yml:8` | ALTA | Siempre arranca en modo dev; en producción debe ser sobreescrito por variable de entorno |
| C2 | No existe `application-prod.yml` | ALTA | Sin configuración específica de producción |
| C3 | `POSTGRES_PASSWORD: secret` en `docker-compose.yml:24` | BAJA | Solo visible localmente, pero es un patrón inseguro |
| C4 | Sin Dockerfile para backend ni frontend | MEDIA | No se puede hacer deploy containerizado |
| C5 | Sin health check endpoint robusto | BAJA | `HealthController.java` solo retorna status estático, no verifica DB |

### 1.3 Infraestructura — Hallazgos

| # | Hallazgo | Severidad | Estado actual |
|---|----------|-----------|---------------|
| I1 | Sin CI/CD pipeline | ALTA | No hay GitHub Actions |
| I2 | Sin deploy configurado | ALTA | No hay rampa a producción |
| I3 | Sin observabilidad | MEDIA | Sin logging estructurado, sin Sentry, sin métricas |
| I4 | docker-compose.yml con servicios comentados | BAJA | Backend y frontend comentados en `docker-compose.yml:63-101` |

---

## 2. Comparativa de Plataformas de Deploy

### 2.1 Backend + PostgreSQL: Railway vs Render

| Criterio | Render | Railway |
|----------|--------|---------|
| **Free tier** | 750 hrs/mes Starter instances | No hay free tier real ($5 trial, luego $1/mes credit) |
| **Precio entry** | $7/mes (Starter: 0.5 CPU, 512MB) | ~$5-10/mes (usage-based) |
| **PostgreSQL** | Managed, $7/mes (1GB) | Managed, usage-based |
| **Pricing model** | Instancias fijas (predecible) | Por segundo de uso (variable) |
| **Deploy speed** | 1-3 minutos | 30-90 segundos |
| **Cold start** | Free: 15min idle → sleep. Paid: siempre on | Siempre on (serverless opcional) |
| **SSL automático** | Sí | Sí |
| **Migraciones Flyway** | Funcionan via startup | Funcionan via startup |

**Recomendación: Render**
- Free tier real para empezar (luego upgrade a Starter $7/mes)
- Pricing predecible: $7 (backend) + $7 (PostgreSQL) = $14/mes fijo
- Cold start manejable con paid tier (siempre on)
- PostgreSQL con backups automáticos

### 2.2 Frontend: Vercel vs Netlify

| Criterio | Vercel | Netlify |
|----------|--------|---------|
| **Free tier** | Hobby (solo no-comercial) | Starter (comercial OK) |
| **Commercial use** | Requiere Pro ($20/mes) | Permitido en free tier |
| **Build minutes** | 6,000/mes | 300/mes (créditos) |
| **Bandwidth** | 100GB/mes | 100GB/mes |
| **Framework** | React/Vite (soportado, no es Next.js) | React/Vite (soportado) |
| **Preview deploys** | Sí, por PR | Sí, por PR |
| **Custom domain** | Sí, gratis | Sí, gratis |
| **SSL** | Automático | Automático |

**Recomendación: Netlify**
- Free tier permite uso comercial (Vercel no)
- React + Vite se despliega igual en ambos
- Preview deploys por PR incluidos
- Sin vendor lock-in de Next.js

### 2.3 Arquitectura Recomendada

```
┌─────────────────────────────────────────────────────┐
│                    PRODUCCIÓN                       │
│                                                     │
│  ┌──────────────┐    ┌──────────────────────────┐   │
│  │   FRONTEND   │    │        BACKEND           │   │
│  │   Netlify    │───▶│      Render              │   │
│  │   (React)    │    │   (Spring Boot)          │   │
│  │              │    │                          │   │
│  │  Port: 443   │    │  Port: 8080 (internal)   │   │
│  └──────────────┘    └────────────┬─────────────┘   │
│                                   │                  │
│                          ┌────────▼─────────┐       │
│                          │    PostgreSQL     │       │
│                          │    Render         │       │
│                          │  Port: 5432       │       │
│                          └──────────────────┘       │
│                                                     │
│  CORS: https://syncria-app.netlify.app              │
│  Flyway: auto-migrate en startup                    │
│  HTTPS: automático en ambos servicios               │
└─────────────────────────────────────────────────────┘
```

**Costo estimado**: $0/mes (free tier) → $14/mes (Starter backend + DB)

---

## 3. Plan por Etapas

### Etapa 1: Seguridad (JWT → httpOnly cookies)
**Riesgo: ALTO** — Breaking change en flujo de auth completo
**Requiere pruebas manuales: SÍ**

#### Cambios necesarios:

**Backend:**
1. `AuthController.java` — Login y Register deben setear httpOnly cookie en vez de retornar token en body
   ```java
   ResponseCookie cookie = ResponseCookie.from("token", jwtToken)
       .httpOnly(true)
       .secure(true)
       .sameSite("Strict")
       .path("/")
       .maxAge(86400)
       .build();
   response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
   ```
2. `JwtAuthenticationFilter.java:71-77` — Extraer token de cookie en vez de header Authorization
   ```java
   private String extractToken(HttpServletRequest request) {
       // Intentar cookie primero, fallback a header (compatibilidad)
       Cookie[] cookies = request.getCookies();
       if (cookies != null) {
           for (Cookie cookie : cookies) {
               if ("token".equals(cookie.getName())) {
                   return cookie.getValue();
               }
           }
       }
       // Fallback: Authorization header (para API clients externos)
       String header = request.getHeader("Authorization");
       if (StringUtils.hasText(header) && header.startsWith("Bearer ")) {
           return header.substring(7);
       }
       return null;
   }
   ```
3. `SecurityConfig.java` — Re-habilitar CSRF con protección por cookies (o mantener CSRF disabled con SameSite=Strict)
4. `SecurityConfig.java:63` — CORS configurable via variable de entorno
5. Crear `application-prod.yml` con CORS dinámico
6. Agregar headers de seguridad HTTP

**Frontend:**
1. `api.ts:10-16` — REMOVER interceptor de Authorization header (el browser envía cookies automáticamente)
2. `authStore.ts:73-76` — REMOVER persist del token (ya no se almacena en localStorage)
3. `api.ts` — Agregar `withCredentials: true` a la config de Axios
4. `authStore.ts:21,30` — Login ya no necesita guardar token manualmente
5. Eliminar toda referencia a `localStorage.getItem('token')`

#### Criterios de aceptación Etapa 1:
- [ ] Login setea cookie httpOnly, token NO visible en DevTools → Application → Cookies
- [ ] Token NO aparece en localStorage
- [ ] Requests autenticados envían cookie automáticamente
- [ ] Logout limpia la cookie
- [ ] 401 limpia cookie y redirige a /login
- [ ] Backend tests pasan (52/52)
- [ ] Frontend tests pasan (43/43)
- [ ] CORS funciona entre frontend y backend

---

### Etapa 2: Variables de Entorno y Configuración
**Riesgo: BAJO** — Solo refactor de configuración
**Requiere pruebas manuales: NO**

#### Cambios necesarios:

1. Crear `application-prod.yml`:
   ```yaml
   spring:
     profiles:
       active: prod
     datasource:
       url: ${DATABASE_URL}
       username: ${DATABASE_USERNAME}
       password: ${DATABASE_PASSWORD}
     jpa:
       show-sql: false
       hibernate:
         ddl-auto: validate
     flyway:
       enabled: true
       baseline-on-migrate: true

   app:
     jwt:
       secret: ${JWT_SECRET}
       expiration-ms: 86400000

   server:
     port: ${PORT:8080}
   ```

2. Modificar `application.yml` — Quitar `spring.profiles.active: dev` hardcoded
   ```yaml
   spring:
     profiles:
       active: ${SPRING_PROFILES_ACTIVE:dev}
   ```

3. Crear `.env.production.example` con variables necesarias para Render

4. Agregar CORS configurable en `SecurityConfig.java`:
   ```java
   @Value("${app.cors.allowed-origins:http://localhost:5173}")
   private String allowedOrigins;
   ```

5. Actualizar `.env.example` con todas las variables necesarias

#### Criterios de aceptación Etapa 2:
- [ ] `java -jar backend.jar` arranca con profile prod sin errores
- [ ] DATABASE_URL, JWT_SECRET, CORS se configuran por variable de entorno
- [ ] Local sigue funcionando con application-dev.yml
- [ ] Tests no se ven afectados

---

### Etapa 3: Seguridad HTTP y Rate Limiting Global
**Riesgo: BAJO** — Adiciones no destructivas
**Requiere pruebas manuales: NO**

#### Cambios necesarios:

1. Agregar headers de seguridad en `SecurityConfig.java` o via filtro:
   ```java
   .headers(headers -> headers
       .contentTypeOptions(Customizer.withDefaults())
       .frameOptions(frame -> frame.deny())
       .xssProtection(Customizer.withDefaults())
       .httpStrictTransportSecurity(hsts -> hsts
           .includeSubDomains(true)
           .maxAgeInSeconds(31536000))
   )
   ```

2. Rate limiting global en `RateLimitingFilter.java` — Ampliar a todos los endpoints:
   - Login: 5 req/min/IP
   - Register: 3 req/min/IP
   - API general: 100 req/min/IP
   - Health: sin límite

3. Crear `application-prod.yml` con CORS configurable

4. Swagger: Deshabilitar en producción via profile
   ```yaml
   springdoc:
     api-docs:
       enabled: ${SWAGGER_ENABLED:false}
     swagger-ui:
       enabled: ${SWAGGER_ENABLED:false}
   ```

#### Criterios de aceptación Etapa 3:
- [ ] Headers de seguridad presentes en responses
- [ ] Rate limiting aplica a todos los endpoints
- [ ] Swagger deshabilitado en producción
- [ ] Rate limit excesivo retorna 429 con mensaje claro

---

### Etapa 4: CI/CD con GitHub Actions
**Riesgo: BAJO** — No afecta código existente
**Requiere pruebas manuales: NO**

#### Pipeline diseñado:

```yaml
# .github/workflows/ci.yml
name: CI

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main, develop]

jobs:
  backend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
      - run: cd backend && ./mvnw test
      - run: cd backend && ./mvnw package -DskipTests

  frontend-tests:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-node@v4
        with:
          node-version: '20'
          cache: 'npm'
          cache-dependency-path: frontend/package-lock.json
      - run: cd frontend && npm ci
      - run: cd frontend && npm run lint
      - run: cd frontend && npm run test
      - run: cd frontend && npm run build

  deploy:
    needs: [backend-tests, frontend-tests]
    if: github.ref == 'refs/heads/main'
    runs-on: ubuntu-latest
    steps:
      # Deploy se dispara automáticamente al merge a main
      - name: Deploy Backend (Render)
        run: curl $RENDER_DEPLOY_HOOK
      - name: Deploy Frontend (Netlify)
        uses: nwtgck/actions-netlify@v3
        with:
          publish-dir: ./frontend/dist
          production-deploy: true
```

#### Criterios de aceptación Etapa 4:
- [ ] Tests corren en cada PR
- [ ] Lint y typecheck corren en cada PR
- [ ] Build se verifica en cada PR
- [ ] Deploy automático al merge a main
- [ ] PRs sin deploy automático (solo verificación)

---

### Etapa 5: Infraestructura de Deploy
**Riesgo: MEDIO** — Configuración nueva
**Requiere pruebas manuales: SÍ**

#### 5.1 Render Backend

1. Crear `backend/Dockerfile`:
   ```dockerfile
   FROM eclipse-temurin:17-jre-alpine
   WORKDIR /app
   COPY target/syncria-backend-1.0.0.jar app.jar
   EXPOSE 8080
   CMD ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
   ```

2. Configuración en Render Dashboard:
   - Service: Web Service
   - Build: Docker
   - Env vars: DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD, JWT_SECRET, SPRING_PROFILES_ACTIVE=prod
   - Health check path: `/api/v1/health`

3. PostgreSQL en Render:
   - Database: PostgreSQL
   - Plan: Starter ($7/mes) o free para empezar
   - Connection string: usar `DATABASE_URL` del dashboard

#### 5.2 Netlify Frontend

1. Crear `frontend/netlify.toml`:
   ```toml
   [build]
     command = "npm run build"
     publish = "dist"

   [[redirects]]
     from = "/*"
     to = "/index.html"
     status = 200
   ```

2. Configuración en Netlify:
   - Build command: `cd frontend && npm run build`
   - Publish directory: `frontend/dist`
   - Node version: 20
   - Environment variables: `VITE_API_URL=https://syncria-backend.onrender.com`

#### 5.3 Variables de Entorno Necesarias

**Backend (Render):**
| Variable | Valor | Ejemplo |
|----------|-------|---------|
| `SPRING_PROFILES_ACTIVE` | `prod` | `prod` |
| `DATABASE_URL` | URL de Render PostgreSQL | `jdbc:postgresql://...` |
| `DATABASE_USERNAME` | Usuario de la DB | `syncria_user` |
| `DATABASE_PASSWORD` | Password de la DB | `***` |
| `JWT_SECRET` | Clave generada con `openssl rand -base64 32` | `abc123...` |
| `PORT` | Puerto (Render asigna el suyo) | `8080` |
| `SWAGGER_ENABLED` | `false` en producción | `false` |

**Frontend (Netlify):**
| Variable | Valor | Ejemplo |
|----------|-------|---------|
| `VITE_API_URL` | URL del backend Render | `https://syncria-backend.onrender.com` |

#### 5.4 CORS entre Frontend y Backend

```
Frontend: https://syncria-app.netlify.app
Backend:  https://syncria-backend.onrender.com

CORS allowedOrigins: https://syncria-app.netlify.app
```

#### 5.5 Flyway en Producción

- Flyway se ejecuta automáticamente al arrancar Spring Boot
- `baseline-on-migrate: true` permite que Flyway maneje la BD existente
- Migraciones V1-V4 ya están en el classpath
- **Nunca** editar migraciones existentes
- Nuevo schema = nuevo archivo `V5__xxx.sql`

#### 5.6 HTTPS

- Render: automático con Let's Encrypt
- Netlify: automático con Let's Encrypt
- Sin configuración manual necesaria

---

### Etapa 6: Observabilidad
**Riesgo: BAJO** — Solo adiciones
**Requiere pruebas manuales: NO**

#### Recomendación mínima para primera versión:

1. **Health check robusto** — Verificar conexión a DB:
   ```java
   @GetMapping("/health")
   public ResponseEntity<Map<String, Object>> health() {
       Map<String, Object> status = new HashMap<>();
       status.put("project", "Syncria");
       status.put("version", "1.2.0");
       try {
           jdbcTemplate.queryForObject("SELECT 1", Integer.class);
           status.put("status", "UP");
           status.put("database", "UP");
       } catch (Exception e) {
           status.put("status", "DOWN");
           status.put("database", "DOWN");
       }
       return ResponseEntity.ok(status);
   }
   ```

2. **Logging estructurado** — Spring Boot ya configura Logback
   - En producción: logs a stdout (Render los captura)
   - No agregar Sentry todavía (premature optimization)

3. **No agregar Sentry/monitoring** en esta fase
   - Los health checks de Render ya monitorean uptime
   - Sentry es para cuando hay usuarios reales reportando bugs

---

## 4. Riesgos y Breaking Changes

### Riesgos Identificados

| Riesgo | Impacto | Probabilidad | Mitigación |
|--------|---------|--------------|------------|
| JWT cookie migration rompe tests existentes | ALTO | ALTA | Ejecutar Etapa 1 con suite de tests completa; usar Bearer como fallback temporal |
| CORS mal configurado bloquea requests | ALTO | MEDIA | Probar con curl primero, luego frontend real |
| Flyway falla en Render por schema existente | MEDIO | BAJA | `baseline-on-migrate: true` maneja esto |
| Render free tier cold start causa 502 | MEDIO | ALTA | Usar paid tier ($7/mes) desde el inicio |
| `application-dev.yml` rompe al cambiar profile | BAJO | BAJA | Profile dev solo se activa con `SPRING_PROFILES_ACTIVE=dev` |

### Cambios que Requieren Pruebas Manuales

1. **Login completo**: Register → Login → Navegar → Logout
2. **Cookie behavior**: Verificar que cookie se setea y se envía automáticamente
3. **CORS**: Probar requests cross-origin desde el frontend
4. **Swagger**: Verificar que está deshabilitado en prod, habilitado en dev
5. **Rate limiting**: Probar con múltiples requests
6. **Health check**: Verificar que retorna status de DB
7. **Flyway**: Verificar que migra correctamente en Render

---

## 5. Criterios de Aceptación — Syncria "Desplegable"

### Seguridad
- [ ] JWT en httpOnly cookie (no localStorage)
- [ ] CORS configurable por variable de entorno
- [ ] Rate limiting global (login, register, API general)
- [ ] Headers de seguridad HTTP (X-Frame-Options, HSTS, etc.)
- [ ] Swagger deshabilitado en producción
- [ ] JWT_SECRET por variable de entorno (no hardcoded)
- [ ] Sin secretos en archivos commiteados

### Testing
- [ ] Backend: 52/52 tests pasan
- [ ] Frontend: 43/43 tests pasan
- [ ] Frontend: lint 0 errores
- [ ] Frontend: typecheck OK
- [ ] Frontend: build OK
- [ ] Integración: login → API → logout funciona end-to-end

### Deploy
- [ ] Backend desplegado en Render con PostgreSQL
- [ ] Frontend desplegado en Netlify
- [ ] CORS funcionando entre ambos
- [ ] HTTPS habilitado en ambos
- [ ] Flyway migra automáticamente en startup
- [ ] Health check retorna UP con DB verificada

### CI/CD
- [ ] GitHub Actions ejecuta tests en cada PR
- [ ] Deploy automático al merge a main
- [ ] PRs sin deploy automático

### Observabilidad
- [ ] Health check con verificación de DB
- [ ] Logs accesibles en Render dashboard

---

## 6. Orden de Ejecución

```
Etapa 1 (Seguridad)        ← PRIMERO, breaking changes
    ↓
Etapa 2 (Variables env)    ← Configuración limpia
    ↓
Etapa 3 (HTTP headers)     ← Mejoras no destructivas
    ↓
Etapa 4 (CI/CD)            ← Pipeline
    ↓
Etapa 5 (Deploy)           ← Infraestructura real
    ↓
Etapa 6 (Observabilidad)   ← Nice to have
```

**Cada etapa se aprueba antes de avanzar a la siguiente.**

---

## 7. Estimación de Costos

### Opción A: Todo Free Tier (para empezar)
| Servicio | Costo | Notas |
|----------|-------|-------|
| Render Backend | $0 | Free tier (cold starts) |
| Render PostgreSQL | $0 | Free tier (expires 30 días) |
| Netlify Frontend | $0 | Free tier |
| GitHub Actions | $0 | 2000 min/mes gratis |
| **Total** | **$0/mes** | Limitado, solo para demo |

### Opción B: Starter (recomendado para portfolio)
| Servicio | Costo | Notas |
|----------|-------|-------|
| Render Backend | $7/mes | Starter (always on) |
| Render PostgreSQL | $7/mes | Starter (1GB) |
| Netlify Frontend | $0 | Free tier funciona |
| GitHub Actions | $0 | 2000 min/mes gratis |
| **Total** | **$14/mes** | Production-ready |

### Opción C: Professional
| Servicio | Costo | Notas |
|----------|-------|-------|
| Render Backend | $25/mes | Standard (1 CPU, 2GB) |
| Render PostgreSQL | $19/mes | Standard (10GB) |
| Netlify Frontend | $0 | Free tier |
| Dominio personalizado | ~$12/año | Opcional |
| **Total** | **~$44/mes** | Para tráfico real |

---

*Documento generado el 2026-08-23*
*Syncria v1.1.0 → v1.2.0*
*Estado: LISTO PARA REVISIÓN Y APROBACIÓN*
