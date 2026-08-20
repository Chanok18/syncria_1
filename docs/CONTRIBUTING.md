# Contribuir a Syncria

Gracias por tu interes en contribuir a Syncria! Este documento explica como participar.

---

## Codigo de Conducta

Este proyecto usa el Codigo de Conducta del Contributor Covenant. Al participar, aceptas respetar a todos los participantes.

---

## Como Contribuir

### 1. Fork el repositorio

```bash
git clone https://github.com/kevin-syncria/syncria.git
cd syncria
git checkout develop
```

### 2. Crear una rama

```bash
git checkout -b feature/nueva-funcionalidad
```

Nomenclatura:
- `feature/` — Nueva funcionalidad
- `bugfix/` — Correccion de bug
- `hotfix/` — Fix urgente en produccion
- `docs/` — Documentacion

### 3. Hacer cambios

- Seguir la guia de estilo del proyecto
- Escribir tests para nueva funcionalidad
- Actualizar documentacion si es necesario

### 4. Commit

Usar [Conventional Commits](https://www.conventionalcommits.org/):

```bash
git commit -m "feat: agregar busqueda de mascotas por especie"
git commit -m "fix: corregir conflict detection en citas"
git commit -m "docs: actualizar guia de instalacion"
git commit -m "test: agregar tests para DashboardService"
```

### 5. Push y Pull Request

```bash
git push origin feature/nueva-funcionalidad
```

Crear Pull Request contra `develop`.

---

## Guia de Estilo

### Backend (Java)

```java
// Services: @Service, @RequiredArgsConstructor, @Transactional
@Service
@RequiredArgsConstructor
@Transactional
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactMapper contactMapper;

    // Methods: verb + noun
    public ContactResponseDTO create(ContactRequestDTO request, Long companyId) {
        // ...
    }

    @Transactional(readOnly = true)
    public Page<ContactResponseDTO> findAll(Long companyId, String search, Pageable pageable) {
        // ...
    }
}

// DTOs: Records (immutables)
public record ContactResponseDTO(
        Long id,
        String name,
        String email
) {}

// Exceptions: custom per module
public class ContactNotFoundException extends NotFoundException {
    public ContactNotFoundException(Long id) {
        super("Contact not found with id: " + id);
    }
}
```

### Frontend (TypeScript)

```typescript
// Components: Functional, PascalCase
export default function ContactCard({ contact }: ContactCardProps) {
  return <div>{contact.name}</div>
}

// Stores: Zustand, one per feature
export const useContactStore = create<ContactState>((set) => ({
  contacts: [],
  isLoading: false,
  fetchContacts: async () => { /* ... */ },
}))

// Types: interfaces in separate files
export interface Contact {
  id: number
  name: string
  email: string
}
```

---

## Estructura de Commits

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

Types:
- `feat`: Nueva funcionalidad
- `fix`: Correccion de bug
- `docs`: Documentacion
- `style`: Formato (no afecta logica)
- `refactor`: Refactorizacion
- `test`: Tests
- `chore`: Mantenimiento

---

## Testing

### Backend

```bash
cd backend
./mvnw test                     # Todos los tests
./mvnw test -Dtest=ContactServiceTest  # Test especifico
```

### Frontend

```bash
cd frontend
npm run test                    # Todos los tests
npm run test:watch              # Watch mode
```

### Cobertura

```bash
# Backend
cd backend
./mvnw verify jacoco:report
# Reporte en: target/site/jacoco/index.html

# Frontend
cd frontend
npm run test:coverage
```

---

## Pull Request Checklist

- [ ] Codigo compila sin errores
- [ ] Tests pasan
- [ ] Linting sin errores
- [ ] Typecheck sin errores
- [ ] Tests escritos para nueva funcionalidad
- [ ] Documentacion actualizada (si aplica)
- [ ] Commit messages siguen Conventional Commits
- [ ] Branch esta basada en `develop`

---

## Preguntas Frecuentes

### Como ejecutar el proyecto?

Ver [docs/INSTALLATION.md](INSTALLATION.md).

### Como reportar bugs?

Abrir un [GitHub Issue](https://github.com/kevin-syncria/syncria/issues) con:
- Descripcion del problema
- Pasos para reproducir
- Comportamiento esperado
- Comportamiento actual

### Como sugerir funcionalidades?

Abrir un [GitHub Issue](https://github.com/kevin-syncria/syncria/issues) con la etiqueta `enhancement`.

---

*Contribuir a Syncria — v1.0.0*
