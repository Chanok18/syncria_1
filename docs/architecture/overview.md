# Arquitectura General - Syncria

## Resumen de Arquitectura

Syncria utiliza una **arquitectura modular por capas** con separación clara entre backend y frontend. El sistema está diseñado para ser escalable, mantenible y preparado para evolucionar a SaaS multi-tenant.

## Principios de Arquitectura

1. **Modularidad**: Cada功能 del negocio es un módulo independiente
2. **Separación de Responsabilidades**: Capas claras con roles definidos
3. **Dependencias Dirigidas**: Las dependencias van hacia adentro
4. **Multi-tenant por Diseño**: Campo `companyId` en todas las entidades
5. **API First**: Backend expone API REST, frontend consume

---

## Vista de Alto Nivel

```
┌─────────────────────────────────────────────────────────────┐
│                      CLIENTES                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐        │
│  │  Browser    │  │  Mobile     │  │  Future     │        │
│  │  (React)    │  │  (Future)   │  │  (API)      │        │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘        │
└─────────┼────────────────┼────────────────┼─────────────────┘
          │                │                │
          ▼                ▼                ▼
┌─────────────────────────────────────────────────────────────┐
│                    LOAD BALANCER                            │
│                    (Nginx/Docker)                           │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                   API GATEWAY                               │
│                  (Spring Boot)                              │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              SECURITY LAYER                         │   │
│  │  - JWT Authentication                               │   │
│  │  - CORS Configuration                               │   │
│  │  - Rate Limiting                                     │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              MODULE LAYER                           │   │
│  │                                                     │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐           │   │
│  │  │   Auth   │ │  Client  │ │   Pet    │           │   │
│  │  │  Module  │ │  Module  │ │  Module  │           │   │
│  │  └──────────┘ └──────────┘ └──────────┘           │   │
│  │                                                     │   │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐           │   │
│  │  │Appointment│ │ Dashboard│ │ Company  │           │   │
│  │  │  Module  │ │  Module  │ │  Module  │           │   │
│  │  └──────────┘ └──────────┘ └──────────┘           │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              SHARED LAYER                           │   │
│  │  - BaseEntity    - TenantContext                    │   │
│  │  - Exceptions    - Utils                           │   │
│  └─────────────────────────────────────────────────────┘   │
└────────────────────────────┬────────────────────────────────┘
                             │
                             ▼
┌─────────────────────────────────────────────────────────────┐
│                  DATA LAYER                                │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              PostgreSQL                             │   │
│  │  - syncria_db (Principal)                           │   │
│  │  - syncria_test (Testing)                           │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              Flyway                                 │   │
│  │  - Migraciones versionadas                          │   │
│  │  - Scripts SQL                                      │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

---

## Arquitectura Backend

### Estructura de Módulos

```
backend/src/main/java/com/syncria/
├── SyncriaApplication.java
├── config/
│   ├── SecurityConfig.java
│   ├── DatabaseConfig.java
│   ├── CorsConfig.java
│   └── TenantConfig.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetailsService.java
├── shared/
│   ├── entity/
│   │   └── BaseEntity.java
│   ├── exception/
│   │   ├── ResourceNotFoundException.java
│   │   ├── BadRequestException.java
│   │   └── GlobalExceptionHandler.java
│   └── utils/
│       └── TenantUtils.java
├── module/
│   ├── auth/
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   └── dto/
│   │   │       ├── LoginRequest.java
│   │   │       ├── RegisterRequest.java
│   │   │       └── AuthResponse.java
│   │   ├── service/
│   │   │   └── AuthService.java
│   │   └── repository/
│   │       └── UserRepository.java
│   ├── company/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   └── entity/
│   │       └── Company.java
│   ├── client/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   │   └── Client.java
│   │   ├── dto/
│   │   └── mapper/
│   ├── pet/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   │   └── Pet.java
│   │   ├── dto/
│   │   └── mapper/
│   └── appointment/
│       ├── controller/
│       ├── service/
│       ├── repository/
│       ├── entity/
│       │   └── Appointment.java
│       ├── dto/
│       └── mapper/
└── resources/
    ├── application.yml
    ├── application-dev.yml
    ├── application-test.yml
    └── db/migration/
        ├── V1__create_company_table.sql
        ├── V2__create_user_table.sql
        ├── V3__create_client_table.sql
        ├── V4__create_pet_table.sql
        └── V5__create_appointment_table.sql
```

### Patrón por Módulo

Cada módulo sigue el mismo patrón:

```java
// Entity
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends BaseEntity {
    private String name;
    private String email;
    private String phone;
    // ...
}

// Repository
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findByNameContainingIgnoreCase(String name, Pageable pageable);
    boolean existsByEmail(String email);
}

// Service
@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    public ClientResponseDTO create(ClientRequestDTO request) {
        // Validations
        // Business logic
        // Save
        // Return DTO
    }
}

// Controller
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management endpoints")
public class ClientController {
    private final ClientService clientService;
    
    @PostMapping
    @Operation(summary = "Create a new client")
    public ResponseEntity<ClientResponseDTO> create(
            @Valid @RequestBody ClientRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.create(request));
    }
}

// DTOs
public record ClientRequestDTO(
    @NotBlank String name,
    @Email @NotBlank String email,
    @NotBlank String phone
) {}

public record ClientResponseDTO(
    Long id,
    String name,
    String email,
    String phone,
    LocalDateTime createdAt
) {}

// Mapper
@Mapper(componentModel = "spring")
public interface ClientMapper {
    ClientResponseDTO toResponse(Client client);
    Client toEntity(ClientRequestDTO dto);
}
```

---

## Arquitectura Frontend

### Estructura de Módulos

```
frontend/src/
├── main.tsx
├── App.tsx
├── index.css
├── components/
│   ├── ui/                    # Componentes genéricos
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Modal.tsx
│   │   ├── Table.tsx
│   │   └── Loading.tsx
│   └── layout/                # Layout components
│       ├── MainLayout.tsx
│       ├── AuthLayout.tsx
│       ├── Sidebar.tsx
│       └── Header.tsx
├── features/                  # Feature modules
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.tsx
│   │   │   └── RegisterForm.tsx
│   │   ├── hooks/
│   │   │   └── useAuth.ts
│   │   ├── store/
│   │   │   └── authStore.ts
│   │   ├── types/
│   │   │   └── index.ts
│   │   └── api/
│   │       └── authApi.ts
│   ├── clients/
│   │   ├── components/
│   │   │   ├── ClientList.tsx
│   │   │   ├── ClientForm.tsx
│   │   │   └── ClientCard.tsx
│   │   ├── hooks/
│   │   │   └── useClients.ts
│   │   ├── store/
│   │   │   └── clientStore.ts
│   │   ├── types/
│   │   │   └── index.ts
│   │   └── api/
│   │       └── clientApi.ts
│   ├── pets/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── store/
│   │   ├── types/
│   │   └── api/
│   ├── appointments/
│   │   ├── components/
│   │   ├── hooks/
│   │   ├── store/
│   │   ├── types/
│   │   └── api/
│   └── dashboard/
│       ├── components/
│       ├── hooks/
│       ├── store/
│       ├── types/
│       └── api/
├── hooks/                     # Shared hooks
│   ├── useDebounce.ts
│   ├── usePagination.ts
│   └── useLocalStorage.ts
├── lib/                       # Utilities
│   ├── api.ts                 # Axios instance
│   ├── utils.ts
│   └── constants.ts
├── stores/                    # Global stores
│   └── uiStore.ts
└── types/                     # Shared types
    └── index.ts
```

### Patrón Zustand Store

```typescript
// store/clientStore.ts
import { create } from 'zustand';
import { clientApi } from '../api/clientApi';

interface ClientState {
  clients: Client[];
  selectedClient: Client | null;
  loading: boolean;
  error: string | null;
  pagination: Pagination;
  
  // Actions
  fetchClients: (page: number, search?: string) => Promise<void>;
  createClient: (data: CreateClientDTO) => Promise<void>;
  updateClient: (id: number, data: UpdateClientDTO) => Promise<void>;
  deleteClient: (id: number) => Promise<void>;
  selectClient: (client: Client | null) => void;
}

export const useClientStore = create<ClientState>((set, get) => ({
  clients: [],
  selectedClient: null,
  loading: false,
  error: null,
  pagination: { page: 1, size: 20, total: 0 },
  
  fetchClients: async (page, search) => {
    set({ loading: true, error: null });
    try {
      const response = await clientApi.getAll(page, search);
      set({ 
        clients: response.content,
        pagination: { 
          page, 
          size: 20, 
          total: response.totalElements 
        }
      });
    } catch (error) {
      set({ error: error.message });
    } finally {
      set({ loading: false });
    }
  },
  
  createClient: async (data) => {
    set({ loading: true, error: null });
    try {
      const newClient = await clientApi.create(data);
      set((state) => ({ 
        clients: [...state.clients, newClient] 
      }));
    } catch (error) {
      set({ error: error.message });
      throw error;
    } finally {
      set({ loading: false });
    }
  },
  
  // ... other actions
}));
```

---

## Flujo de Petición

```
1. Usuario realiza acción en UI
         │
         ▼
2. Componente React llama a Zustand Store
         │
         ▼
3. Store llama a API Client (Axios)
         │
         ▼
4. Axios envía HTTP Request a Backend
         │
         ▼
5. Spring Security valida JWT
         │
         ▼
6. Controller recibe Request
         │
         ▼
7. Service ejecuta lógica de negocio
         │
         ▼
8. Repository accede a PostgreSQL
         │
         ▼
9. Response viaja de vuelta
         │
         ▼
10. Store actualiza estado
         │
         ▼
11. UI se re-renderiza
```

---

## Estrategia Multi-tenant

### Modelo de Datos

Todas las entidades principales incluyen:

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false)
    private Long companyId;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;
}
```

### Filtro JPA

```java
@Component
@RequiredArgsConstructor
public class TenantFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        Long companyId = extractCompanyIdFromRequest((HttpServletRequest) request);
        TenantContext.setCurrentCompanyId(companyId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
}
```

### Repository con Filtro

```java
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId AND c.deleted = false")
    Page<Client> findAllByCompanyId(@Param("companyId") Long companyId, Pageable pageable);
}
```

---

## Seguridad

### Autenticación JWT

```
1. Usuario envía credenciales (email + password)
         │
         ▼
2. Backend valida credenciales
         │
         ▼
3. Backend genera JWT token (24h expiry)
         │
         ▼
4. Frontend almacena token en localStorage
         │
         ▼
5. Frontend envía token en header Authorization
         │
         ▼
6. Backend valida token en cada request
         │
         ▼
7. Si token válido → procesa request
   Si token inválido → 401 Unauthorized
```

### Permisos (Futuro)

```java
@PreAuthorize("hasRole('ADMIN') or @tenantSecurity.isSameCompany(#companyId)")
public ResponseEntity<Client> getClient(@PathVariable Long id) {
    // ...
}
```

---

## Despliegue

### Docker Compose (Desarrollo)

```yaml
services:
  postgres:
    image: postgres:16-alpine
    ports: ["5432:5432"]
    
  pgadmin:
    image: dpage/pgadmin4
    ports: ["5050:80"]
    
  backend:
    build: ./backend
    ports: ["8080:8080"]
    depends_on: [postgres]
    
  frontend:
    build: ./frontend
    ports: ["5173:5173"]
    depends_on: [backend]
    
  n8n:
    image: n8nio/n8n
    ports: ["5678:5678"]
```

### Producción (Futuro)

```
┌─────────────────────────────────────────┐
│           Kubernetes Cluster            │
│                                         │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐ │
│  │Backend  │  │Backend  │  │Backend  │ │
│  │Pod 1    │  │Pod 2    │  │Pod 3    │ │
│  └────┬────┘  └────┬────┘  └────┬────┘ │
│       │            │            │       │
│       └────────────┼────────────┘       │
│                    │                    │
│              ┌─────┴─────┐              │
│              │ PostgreSQL │              │
│              │  Cluster   │              │
│              └───────────┘              │
│                                         │
│  ┌─────────────────────────────────┐   │
│  │         Load Balancer           │   │
│  └─────────────────────────────────┘   │
└─────────────────────────────────────────┘
```

---

## Decisiones de Arquitectura

Ver `/docs/adr/` para decisiones documentadas:

1. **[ADR-001](adr/001-architecture.md)**: Arquitectura Modular por Capas
2. **[ADR-002](adr/002-modular-layered.md)**: Patrón de Módulo
3. **[ADR-003](adr/003-multi-tenant.md)**: Estrategia Multi-tenant
4. **[ADR-004](adr/004-state-management.md)**: Gestión de Estado Frontend

---

*Documento de Arquitectura - Syncria v1.0*
*Última actualización: 2026*
