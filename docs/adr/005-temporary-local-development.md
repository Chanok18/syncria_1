# ADR-005: Estrategia de Desarrollo Local Temporal

## Estado
**Aprobado** - 2026

## Contexto
El entorno de desarrollo actual presenta problemas recurrentes con Docker Desktop, WSL y configuración de registros en Windows. Se espera una nueva máquina de desarrollo en las próximas semanas.

Los problemas incluyen:
1. Docker Desktop no inicia correctamente
2. WSL2 presenta conflictos de configuración
3. Imágenes Docker no se descargan por problemas de red/registro
4. Tiempos de espera excesivos en operaciones de contenedores

## Decisión
Implementar una **estrategia de desarrollo local temporal** donde:
1. PostgreSQL se ejecuta localmente (no en Docker)
2. Spring Boot se ejecuta localmente
3. React se ejecuta localmente
4. Se mantienen todos los archivos Docker para migración futura

### Configuración Local Requerida

| Servicio | Versión | Puerto |
|----------|---------|--------|
| PostgreSQL | 16+ | 5432 |
| Java | 21+ | - |
| Node.js | 20+ | - |
| Spring Boot | 3.x | 8080 |
| React (Vite) | 5.x | 5173 |

### Variables de Entorno Locales

```bash
# PostgreSQL
DATABASE_URL=jdbc:postgresql://localhost:5432/syncria_db
DATABASE_USERNAME=syncria_user
DATABASE_PASSWORD=secret

# Spring Boot
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080

# React
VITE_API_URL=http://localhost:8080
```

## Alternativas Consideradas

### 1. Forzar Docker en Windows
- **Pros**: Consistencia con producción
- **Contras**: Problemas recurrentes, bloquea desarrollo
- **Veredicto**: No viable actualmente

### 2. Usar solo H2 (sin PostgreSQL)
- **Pros**: Sin instalación externa
- **Contras**: Diferencias de comportamiento, no viable para producción
- **Veredicto**: Riesgo de deuda técnica

### 3. Desarrollo Local Temporal (Elegida)
- **Pros**: Sin bloqueos, permite avanzar
- **Contras**: Diferencia temporal con producción
- **Veredicto**: Mejor opción bajo las circunstancias

### 4. Usar otra máquina con Docker
- **Pros**: Consistencia completa
- **Contras**: Requiere configuración adicional
- **Veredicto**: Opción para futuro

## Consecuencias

### Positivas
- Desarrollo sin bloqueos por infraestructura
- Velocidad de iteración rápida
- Menos complejidad operacional durante desarrollo

### Negativas
- Diferencia entre entorno local y Docker
- Posibles diferencias de comportamiento de PostgreSQL
- Necesidad de mantener Docker files actualizados

### Mitigaciones
1. Documentar claramente los prerrequisitos locales
2. Mantener docker-compose.yml actualizado
3. Validar Docker después de nueva máquina
4. No usar features específicas de H2 en tests

## Referencias
- [PostgreSQL Local Installation](https://www.postgresql.org/download/)
- [Spring Boot Profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.profiles)
- [Docker Desktop WSL Issues](https://docs.docker.com/desktop/troubleshooting/)

---

*ADR-005 - Syncria*
*Creado: 2026*
