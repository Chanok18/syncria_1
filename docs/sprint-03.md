# Sprint 03 - Contactos

## Informacion del Sprint

| Campo | Valor |
|-------|-------|
| **Nombre** | Sprint 03: Contactos |
| **Duracion** | 2 semanas (10 dias laborales) |
| **Fecha Inicio** | Por definir |
| **Fecha Fin** | Por definir |
| **Puntos de Historia** | 15 + 4 (tech debt) = 19 |
| **Capacidad** | 40 horas |
| **Sprint anterior** | Sprint 02: Autenticacion (v0.2.0) |

## Objetivo del Sprint

> "Entregar el modulo completo de Contactos: CRUD con paginacion, busqueda, y aislamiento multi-tenant. Resolver tech debt critico del Sprint 02."

## Definition of Done (DoD)

- [ ] Codigo commiteado en rama `feature/sprint-3-contacts`
- [ ] Tests unitarios pasando (>80% backend, >70% frontend)
- [ ] Linting sin errores
- [ ] Build exitoso (backend + frontend)
- [ ] API funcional con paginacion y busqueda
- [ ] UI funcional con todas las operaciones CRUD
- [ ] Multi-tenant funcional (companyId del JWT)
- [ ] Documentacion actualizada

---

## Tech Debt del Sprint 02 (RESOLVER PRIMERO)

Estos issues fueron identificados en la auditoria del Sprint 02 y deben resolverse antes de implementar el CRUD de contactos, ya que afectan la arquitectura multi-tenant.

### TD-001: companyId funcional [HIGH]

**Problema**: `AuthService.java:33` hardcodea `.companyId(1L)`. Todos los usuarios se registran en la misma empresa.

**Solucion**:
- Opcion A: Crear empresa por defecto al registrar usuario
- Opcion B: Pedir companyId en el registro (futuro: invitacion)
- **Decision**: Opcion A por ahora. Auto-crear empresa "Mi Clinica" al registrar.

**Archivos a modificar**:
- `AuthService.java` — crear Company antes de User
- `Company.java` — nueva entity (extiende BaseEntity)
- `CompanyRepository.java` — nuevo repository
- `V2__create_company_table.sql` — nueva migracion

### TD-002: JWT cache [HIGH]

**Problema**: `JwtAuthenticationFilter.java:37` consulta `userRepository.findByEmail()` en cada request.

**Solucion**: Agregar cache simple con ConcurrentHashMap o Spring Cache.

**Archivos a modificar**:
- `JwtAuthenticationFilter.java` — usar cache

### TD-003: Soft-delete en JWT filter [HIGH]

**Problema**: Usuarios con `is_deleted=true` pueden autenticarse.

**Solucion**: Verificar `deleted = false` en el query del filter.

**Archivos a modificar**:
- `JwtAuthenticationFilter.java` — agregar check de deleted
- `UserRepository.java` — agregar query con is_deleted check

### TD-004: Session persistence (frontend) [HIGH]

**Problema**: Despues de F5, `user` es null en el store.

**Solucion**: Usar Zustand `persist` middleware o decodificar JWT.

**Archivos a modificar**:
- `authStore.ts` — agregar persist middleware

---

## Historias de Usuario

### US-003: Crear Contacto

| Campo | Valor |
|-------|-------|
| **ID** | US-003 |
| **Prioridad** | Must Have |
| **Estimacion** | 5 puntos |
| **Sprint** | 3 |

**Descripcion**:
COMO veterinario
QUIERO registrar un nuevo contacto (dueno de mascota)
PARA mantener un registro de mis contactos

**Criterios de Aceptacion**:
1. Campos obligatorios: nombre, email, telefono
2. El email debe ser unico por contacto
3. Se muestra mensaje de exito
4. Se redirige a la lista de contactos
5. Se valida formato de email
6. Se valida formato de telefono
7. El contacto se asocia al companyId del JWT

**Tarea tecnica**:
- Crear endpoint POST /api/v1/contacts
- Implementar validaciones de negocio
- Crear DTOs de request/response
- Crear componente de formulario en React
- Implementar manejo de errores

---

### US-004: Listar Contactos

| Campo | Valor |
|-------|-------|
| **ID** | US-004 |
| **Prioridad** | Must Have |
| **Estimacion** | 5 puntos |
| **Sprint** | 3 |

**Descripcion**:
COMO veterinario
QUIERO ver la lista de todos mis contactos
PARA encontrar rapidamente un contacto

**Criterios de Aceptacion**:
1. Se muestra tabla con nombre, email, telefono
2. Paginacion de 20 elementos
3. Busqueda por nombre en tiempo real
4. Ordenamiento por nombre o fecha de creacion
5. Loading state mientras se cargan datos
6. Mensaje si no hay contactos
7. Solo se muestran contactos del companyId del JWT

**Tarea tecnica**:
- Crear endpoint GET /api/v1/contacts con paginacion
- Implementar busqueda con query parameter
- Crear componente de tabla con paginacion
- Implementar debounce en busqueda
- Crear Zustand store para contactos

---

### US-005: Editar Contacto

| Campo | Valor |
|-------|-------|
| **ID** | US-005 |
| **Prioridad** | Must Have |
| **Estimacion** | 3 puntos |
| **Sprint** | 3 |

**Descripcion**:
COMO veterinario
QUIERO editar la informacion de un contacto
PARA mantener los datos actualizados

**Criterios de Aceptacion**:
1. Se carga el formulario con datos actuales
2. Se validan los campos obligatorios
3. Se muestra mensaje de exito
4. Los cambios se reflejan inmediatamente
5. Se puede cancelar la edicion
6. Solo se pueden editar contactos del companyId del JWT

**Tarea tecnica**:
- Crear endpoint PUT /api/v1/contacts/{id}
- Implementar carga de datos iniciales
- Reutilizar componente de formulario
- Implementar optimistic updates

---

### US-006: Eliminar Contacto

| Campo | Valor |
|-------|-------|
| **ID** | US-006 |
| **Prioridad** | Must Have |
| **Estimacion** | 2 puntos |
| **Sprint** | 3 |

**Descripcion**:
COMO veterinario
QUIERO eliminar un contacto
PARA mantener mi lista limpia

**Criterios de Aceptacion**:
1. Se muestra confirmacion antes de eliminar
2. La eliminacion es logica (soft delete)
3. Se oculta de la lista
4. No se pierden datos historicos
5. Solo se pueden eliminar contactos del companyId del JWT

**Tarea tecnica**:
- Crear endpoint DELETE /api/v1/contacts/{id}
- Implementar soft delete en entity
- Crear componente de confirmacion
- Actualizar lista despues de eliminar

---

## Tareas Tecnicas del Sprint

### Fase 0: Tech Debt (4 horas)

| # | Tarea | Archivos | Estimacion |
|---|-------|----------|------------|
| 0.1 | Resolver companyId (Company entity + migracion) | Company.java, V2, AuthService | 2h |
| 0.2 | JWT cache en JwtAuthenticationFilter | JwtAuthenticationFilter | 1h |
| 0.3 | Soft-delete check en JWT filter | JwtAuthenticationFilter, UserRepository | 0.5h |
| 0.4 | Session persistence (Zustand persist) | authStore.ts | 0.5h |

### Fase 1: Backend Contactos (12 horas)

| # | Tarea | Archivos | Estimacion |
|---|-------|----------|------------|
| 1.1 | Contact entity | Contact.java | 1h |
| 1.2 | ContactRepository | ContactRepository.java | 1h |
| 1.3 | ContactService | ContactService.java | 2h |
| 1.4 | ContactController | ContactController.java | 2h |
| 1.5 | DTOs (Request/Response) | ContactRequestDTO, ContactResponseDTO | 1h |
| 1.6 | Migracion SQL | V2__create_contact_table.sql (o附到 V2 si Company ya lo creo) | 1h |
| 1.7 | Validaciones y excepciones | ContactNotFoundException, validations | 1h |
| 1.8 | Tests unitarios | ContactServiceTest | 2h |
| 1.9 | Tests de integracion | ContactControllerIntegrationTest | 1h |

### Fase 2: Frontend Contactos (12 horas)

| # | Tarea | Archivos | Estimacion |
|---|-------|----------|------------|
| 2.1 | Contact types | features/contacts/types/types.ts | 0.5h |
| 2.2 | contactStore (Zustand) | features/contacts/store/contactStore.ts | 1.5h |
| 2.3 | useContacts hook | features/contacts/hooks/useContacts.ts | 0.5h |
| 2.4 | ContactListPage (tabla + paginacion) | features/contacts/components/ContactListPage.tsx | 3h |
| 2.5 | ContactFormPage (crear/editar) | features/contacts/components/ContactFormPage.tsx | 2.5h |
| 2.6 | ContactDeleteModal | features/contacts/components/ContactDeleteModal.tsx | 1h |
| 2.7 | UI components (Table, Pagination, SearchInput) | components/ui/ | 2h |
| 2.8 | Integrar rutas en App.tsx | App.tsx | 0.5h |
| 2.9 | Tests unitarios | contactStore.test, ContactListPage.test | 1h |

### Fase 3: Integracion y Polish (4 horas)

| # | Tarea | Estimacion |
|---|-------|------------|
| 3.1 | Verificar flujo completo CRUD | 1h |
| 3.2 | Verificar multi-tenant (companyId) | 1h |
| 3.3 | Verificar paginacion y busqueda | 0.5h |
| 3.4 | Actualizar documentacion | 0.5h |
| 3.5 | Code review y fixes | 1h |

---

## Estimacion de Tiempo

| Fase | Horas |
|------|-------|
| Tech Debt (Sprint 02) | 4h |
| Backend Contactos | 12h |
| Frontend Contactos | 12h |
| Integracion y Polish | 4h |
| Buffer (imprevistos) | 4h |
| **Total** | **36h / 40h** |

---

## Arquitectura del Modulo

### Backend

```
module/contact/
├── controller/
│   └── ContactController.java      # CRUD endpoints
├── service/
│   └── ContactService.java         # Logica de negocio
├── repository/
│   └── ContactRepository.java      # JPA +Specifications
├── entity/
│   └── Contact.java                # Extiende BaseEntity
├── dto/
│   ├── ContactRequestDTO.java      # Validaciones
│   └── ContactResponseDTO.java     # Response
├── mapper/
│   └── ContactMapper.java          # Entity <-> DTO
└── exception/
    └── ContactNotFoundException.java
```

### Frontend

```
features/contacts/
├── components/
│   ├── ContactListPage.tsx         # Tabla + paginacion + busqueda
│   ├── ContactFormPage.tsx         # Formulario crear/editar
│   ├── ContactDetailPage.tsx       # Vista detalle (futuro)
│   └── ContactDeleteModal.tsx      # Confirmacion
├── hooks/
│   └── useContacts.ts             # CRUD operations
├── store/
│   └── contactStore.ts            # Zustand store
└── types/
    └── types.ts                   # Contact, ContactRequest, ContactResponse
```

### API Endpoints

| Metodo | Ruta | Descripcion | Paginacion |
|--------|------|-------------|------------|
| GET | /api/v1/contacts | Listar contactos | Si (?page, ?size, ?search) |
| GET | /api/v1/contacts/{id} | Detalle de contacto | No |
| POST | /api/v1/contacts | Crear contacto | No |
| PUT | /api/v1/contacts/{id} | Editar contacto | No |
| DELETE | /api/v1/contacts/{id} | Eliminar contacto (soft) | No |

### Base de Datos

```sql
CREATE TABLE contacts (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(50) NOT NULL,
    address VARCHAR(500),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(company_id, email)
);

CREATE INDEX idx_contacts_company_id ON contacts(company_id);
CREATE INDEX idx_contacts_name ON contacts(company_id, name);
```

---

## Riesgos del Sprint

| Riesgo | Probabilidad | Impacto | Mitigacion |
|--------|--------------|---------|------------|
| companyId complejo de implementar | Media | Alto | Empezar con empresa por defecto, simplificar |
| Paginacion con busqueda puede ser lento | Baja | Medio | Usar indexes, paginacion del lado del servidor |
| Forms complejos en frontend | Baja | Bajo | Reutilizar componentes existentes |
| Tests toman mucho tiempo | Media | Medio | Priorizar tests criticos, cubrir en buffer |

---

## Entregables del Sprint

1. **Contact entity** con campos completos
2. **API REST** de contactos con CRUD + paginacion + busqueda
3. **Multi-tenant** funcional (companyId del JWT)
4. **UI completa**: lista, formulario, eliminacion
5. **Tests** unitarios y de integracion
6. **Tech debt** del Sprint 02 resuelto
7. **Documentacion** actualizada

---

## Notas para Kevin

### Comandos utiles durante este sprint:

```bash
# Backend
cd backend
./mvnw spring-boot:run
./mvnw test
./mvnw test -Dtest=ContactServiceTest

# Frontend
cd frontend
npm run dev
npm run build
npm run lint
npm run typecheck

# Docker
docker-compose up -d postgres
```

### Orden recomendado de implementacion:

1. Resolver TD-001 (companyId) — PRIMERO
2. Contact entity + migracion
3. ContactRepository + ContactService
4. ContactController + DTOs
5. Tests backend
6. Contact types + store frontend
7. ContactFormPage (crear/editar)
8. ContactListPage (tabla + paginacion)
9. ContactDeleteModal
10. Integrar rutas
11. Tests frontend
12. Polish y verificacion

### Criterios de aceptacion para el Sprint:

- [ ] Un usuario puede crear un contacto
- [ ] Un usuario puede listar sus contactos con paginacion
- [ ] Un usuario puede buscar contactos por nombre
- [ ] Un usuario puede editar un contacto
- [ ] Un usuario puede eliminar un contacto (soft delete)
- [ ] Solo ve contactos de su empresa (multi-tenant)
- [ ] Los tests pasan
- [ ] El build es exitoso

---

*Sprint 03 Plan - Syncria*
*Fecha: 2026-07-29*
*Estado: COMPLETADO — v0.3.0 STABLE*

### Resultado Final

| Historia | Puntos | Estado |
|----------|--------|--------|
| US-003: Crear Contacto | 5 | ✅ COMPLETADO |
| US-004: Listar Contactos | 5 | ✅ COMPLETADO |
| US-005: Editar Contacto | 3 | ✅ COMPLETADO |
| US-006: Eliminar Contacto | 2 | ✅ COMPLETADO |
| Tests backend (ContactService) | - | ✅ 10 tests |
| Tests frontend (UI) | - | ✅ 6 tests |
| **Total** | **15** | **STABLE** |

### Veredicto
Sprint 03 completado exitosamente. Backend 22/22 tests, Frontend 6/6 tests, lint/typecheck/build clean, multi-tenant verificado.
