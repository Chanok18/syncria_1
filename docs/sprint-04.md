# Sprint 04 — Mascotas (Pets)

## Informacion del Sprint

| Campo | Valor |
|-------|-------|
| **Nombre** | Sprint 04: Mascotas |
| **Duracion** | 2 semanas (10 dias laborales) |
| **Fecha Inicio** | 2026-07-29 |
| **Fecha Fin** | 2026-07-29 |
| **Puntos de Historia** | 8 |
| **Capacidad** | 40 horas |
| **Sprint anterior** | Sprint 03: Contactos (v0.3.0) |

## Objetivo del Sprint

> "Entregar el modulo de Mascotas: CRUD vinculado a contactos, con listado, busqueda, y aislamiento multi-tenant."

---

## Historias de Usuario

### US-007: Registrar Mascota

| Campo | Valor |
|-------|-------|
| **ID** | US-007 |
| **Prioridad** | Must Have |
| **Estimacion** | 5 puntos |

**Descripcion**:
COMO veterinario
QUIERO registrar una mascota vinculada a un dueño (contacto)
PARA tener el historial completo

**Criterios de Aceptacion**:
1. Campos: nombre, especie, raza, fecha nacimiento, sexo
2. Se vincula a un contacto existente (companyId del JWT)
3. Se muestra en el perfil del contacto
4. Se validan campos obligatorios (nombre, especie)
5. Solo se pueden ver mascotas del companyId del JWT

**Tarea tecnica**:
- Crear endpoint POST /api/v1/pets
- Crear endpoint GET /api/v1/pets (listar)
- Crear endpoint GET /api/v1/pets/{id}
- Crear endpoint PUT /api/v1/pets/{id}
- Crear endpoint DELETE /api/v1/pets/{id}
- Vincular con modulo de contacts

---

### US-008: Ver Mascotas del Contacto

| Campo | Valor |
|-------|-------|
| **ID** | US-008 |
| **Prioridad** | Must Have |
| **Estimacion** | 3 puntos |

**Descripcion**:
COMO veterinario
QUIERO ver todas las mascotas de un contacto
PARA tener la informacion completa del hogar

**Criterios de Aceptacion**:
1. Se muestra lista de mascotas del contacto
2. Cada mascota muestra nombre, especie, raza
3. Se puede hacer click para ver detalles
4. Boton para agregar nueva mascota
5. Loading state mientras se cargan

**Tarea tecnica**:
- Crear endpoint GET /api/v1/contacts/{id}/pets
- Crear componente de lista de mascotas en frontend
- Integrar con ContactDetailPage o como sub-seccion

---

## Tareas Tecnicas del Sprint

### Fase 1: Backend (12h)

| # | Tarea | Archivos | Estimacion |
|---|-------|----------|------------|
| 1.1 | Pet entity (extends BaseEntity) | Pet.java | 1h |
| 1.2 | PetRepository (JPA + Specification) | PetRepository.java | 1h |
| 1.3 | PetService (CRUD + multi-tenant) | PetService.java | 2h |
| 1.4 | PetController (REST endpoints) | PetController.java | 2h |
| 1.5 | DTOs (PetRequestDTO, PetResponseDTO) | dto/ | 1h |
| 1.6 | PetMapper (MapStruct) | PetMapper.java | 0.5h |
| 1.7 | PetNotFoundException + validaciones | exception/ | 0.5h |
| 1.8 | V3__create_pet_table.sql (Flyway) | db/migration/ | 1h |
| 1.9 | Tests (PetServiceTest) | PetServiceTest.java | 2h |
| 1.10 | Compilar y ejecutar tests | | 1h |

### Fase 2: Frontend (10h)

| # | Tarea | Archivos | Estimacion |
|---|-------|----------|------------|
| 2.1 | Pet types (Pet, PetRequest, PetResponse) | types/petTypes.ts | 0.5h |
| 2.2 | petStore (Zustand) | store/petStore.ts | 1.5h |
| 2.3 | usePets hook | hooks/usePets.ts | 0.5h |
| 2.4 | PetListPage (tabla + paginacion) | components/PetListPage.tsx | 2h |
| 2.5 | PetFormPage (crear/editar) | components/PetFormPage.tsx | 2h |
| 2.6 | PetDeleteModal | components/PetDeleteModal.tsx | 0.5h |
| 2.7 | Integrar rutas en App.tsx | App.tsx | 0.5h |
| 2.8 | Actualizar Sidebar (Mascotas link) | Sidebar.tsx | 0.5h |
| 2.9 | Tests frontend (petStore, PetListPage) | tests/ | 1.5h |

### Fase 3: Integracion (4h)

| # | Tarea | Estimacion |
|---|-------|------------|
| 3.1 | Verificar flujo completo CRUD | 1h |
| 3.2 | Verificar multi-tenant (companyId) | 1h |
| 3.3 | Verificar vinculo contacto-mascota | 0.5h |
| 3.4 | Actualizar documentacion | 0.5h |
| 3.5 | Code review y fixes | 1h |

---

## Estimacion de Tiempo

| Fase | Horas |
|------|-------|
| Backend | 12h |
| Frontend | 10h |
| Integracion | 4h |
| Buffer | 4h |
| **Total** | **30h / 40h** |

---

## Arquitectura del Modulo

### Backend

```
module/pet/
├── controller/PetController.java
├── service/PetService.java
├── repository/PetRepository.java
├── entity/Pet.java
├── dto/PetRequestDTO.java
├── dto/PetResponseDTO.java
├── mapper/PetMapper.java
└── exception/PetNotFoundException.java
```

### Frontend

```
features/pets/
├── components/
│   ├── PetListPage.tsx
│   ├── PetFormPage.tsx
│   └── PetDeleteModal.tsx
├── hooks/usePets.ts
├── store/petStore.ts
└── types/petTypes.ts
```

### Base de Datos

```sql
CREATE TABLE pets (
    id BIGSERIAL PRIMARY KEY,
    company_id BIGINT NOT NULL REFERENCES companies(id),
    contact_id BIGINT NOT NULL REFERENCES contacts(id),
    name VARCHAR(255) NOT NULL,
    species VARCHAR(100) NOT NULL,
    breed VARCHAR(100),
    birth_date DATE,
    gender VARCHAR(20),
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_pets_company ON pets(company_id);
CREATE INDEX idx_pets_contact ON pets(contact_id);
```

### API Endpoints

| Metodo | Ruta | Descripcion |
|--------|------|-------------|
| GET | /api/v1/pets | Listar mascotas (paginado + busqueda) |
| GET | /api/v1/pets/{id} | Detalle de mascota |
| POST | /api/v1/pets | Crear mascota |
| PUT | /api/v1/pets/{id} | Editar mascota |
| DELETE | /api/v1/pets/{id} | Eliminar mascota (soft delete) |
| GET | /api/v1/contacts/{id}/pets | Mascotas de un contacto |

---

## Criterios de Aceptacion para el Sprint

- [x] Un usuario puede crear una mascota vinculada a un contacto
- [x] Un usuario puede listar sus mascotas con paginacion
- [x] Un usuario puede buscar mascotas por nombre
- [x] Un usuario puede editar una mascota
- [x] Un usuario puede eliminar una mascota (soft delete)
- [x] Un usuario puede ver las mascotas de un contacto especifico
- [x] Solo ve mascotas de su empresa (multi-tenant)
- [x] Los tests pasan
- [x] El build es exitoso

---

*Sprint 04 Plan — Syncria*
*Fecha: 2026-07-29*
*Estado: COMPLETADO*
