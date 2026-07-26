# ADR-002: Patrón de Módulo - Modular Layered Architecture

## Estado
**Aprobado** - 2026

## Contexto
Cada功能 del CRM (clientes, mascotas, citas) necesita:
1. API REST para comunicación
2. Lógica de negocio
3. Acceso a datos
4. Validaciones
5. Manejo de errores

Se necesita un patrón consistente que todos los módulos sigan.

## Decisión
Implementar un **patrón de módulo** donde cada功能 sigue la misma estructura de carpetas y responsabilidades.

### Estructura del Módulo

```
module/{nombre_modulo}/
├── controller/
│   ├── {Module}Controller.java      # Endpoints REST
│   └── dto/
│       ├── {Entity}RequestDTO.java   # Request body
│       └── {Entity}ResponseDTO.java  # Response body
├── service/
│   ├── {Module}Service.java         # Interface
│   └── {Module}ServiceImpl.java     # Implementación
├── repository/
│   └── {Module}Repository.java      # Interface JPA
├── entity/
│   └── {Entity}.java                # JPA Entity
├── mapper/
│   └── {Entity}Mapper.java          # MapStruct o manual
├── validation/
│   └── {Entity}Validator.java       # Validaciones custom
└── exception/
    └── {Module}NotFoundException.java
```

### Responsabilidades por Capa

#### Controller
- Recibe HTTP requests
- Valida DTOs con `@Valid`
- Llama a Service
- Retorna HTTP responses
- **NO contiene lógica de negocio**

#### Service
- Contiene lógica de negocio
- Valida reglas de negocio
- Transforma datos
- Llama a Repository
- Maneja transacciones

#### Repository
- Acceso a datos
- Queries personalizadas
- Paginación
- **NO contiene lógica de negocio**

#### Entity
- Representación de tabla en BD
- Relaciones JPA
- Anotaciones de auditoría
- **NO expuesta al cliente**

#### DTOs
- Datos de transferencia
- Validaciones de formato
- Sin lógica de negocio
- **Separados por operación (Request/Response)**

#### Mapper
- Convierte Entity ↔ DTO
- Puede ser MapStruct o manual
- **Sin lógica de negocio**

### Ejemplo Completo

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
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String phone;
    
    private String address;
    private String city;
    private String notes;
    
    @Column(name = "is_active", nullable = false)
    private Boolean active = true;
}

// Repository
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Page<Client> findByCompanyIdAndDeletedFalse(
        Long companyId, Pageable pageable);
    
    Page<Client> findByCompanyIdAndFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
        Long companyId, String firstName, String lastName, Pageable pageable);
    
    boolean existsByEmailAndCompanyId(String email, Long companyId);
}

// Service Interface
public interface ClientService {
    ClientResponseDTO create(ClientRequestDTO request);
    ClientResponseDTO getById(Long id);
    Page<ClientResponseDTO> getAll(int page, int size, String search);
    ClientResponseDTO update(Long id, ClientRequestDTO request);
    void delete(Long id);
}

// Service Implementation
@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    
    @Override
    public ClientResponseDTO create(ClientRequestDTO request) {
        // Business validations
        if (clientRepository.existsByEmailAndCompanyId(
                request.email(), TenantUtils.getCurrentCompanyId())) {
            throw new BadRequestException("Email already exists");
        }
        
        // Map to entity
        Client client = clientMapper.toEntity(request);
        client.setCompanyId(TenantUtils.getCurrentCompanyId());
        
        // Save
        Client saved = clientRepository.save(client);
        
        // Return DTO
        return clientMapper.toResponse(saved);
    }
    
    @Override
    @Transactional(readOnly = true)
    public ClientResponseDTO getById(Long id) {
        Client client = clientRepository.findById(id)
            .filter(c -> c.getCompanyId().equals(TenantUtils.getCurrentCompanyId()))
            .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        
        return clientMapper.toResponse(client);
    }
    
    // ... other methods
}

// Controller
@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Client management")
public class ClientController {
    
    private final ClientService clientService;
    
    @PostMapping
    @Operation(summary = "Create a new client")
    public ResponseEntity<ClientResponseDTO> create(
            @Valid @RequestBody ClientRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.create(request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID")
    public ResponseEntity<ClientResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getById(id));
    }
    
    @GetMapping
    @Operation(summary = "Get all clients")
    public ResponseEntity<Page<ClientResponseDTO>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(clientService.getAll(page, size, search));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update client")
    public ResponseEntity<ClientResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ClientRequestDTO request) {
        return ResponseEntity.ok(clientService.update(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete client")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        clientService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

// DTOs
public record ClientRequestDTO(
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100)
    String firstName,
    
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100)
    String lastName,
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,
    
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\+?[0-9\\s\\-]{10,15}$", message = "Invalid phone")
    String phone,
    
    String address,
    String city,
    String notes
) {}

public record ClientResponseDTO(
    Long id,
    String firstName,
    String lastName,
    String email,
    String phone,
    String address,
    String city,
    String notes,
    Boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

// Mapper (MapStruct)
@Mapper(componentModel = "spring")
public interface ClientMapper {
    
    ClientResponseDTO toResponse(Client client);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "companyId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    Client toEntity(ClientRequestDTO dto);
}
```

## Alternativas Consideradas

### 1. Services compartidos
- **Pro**: Menos código
- **Contras**: Violación de SRP, difícil de mantener
- **Veredicto**: Rechazado

### 2. Un solo Controller gigante
- **Pro**: Menos archivos
- **Contras**: Archivo enorme, difícil de mantener
- **Veredicto**: Rechazado

### 3. Controller + Service sin Repository
- **Pro**: Menos capas
- **Contras**: Acceso directo a BD, difícil de testear
- **Veredicto**: Rechazado

## Consecuencias

### Positivas
- Consistencia en todo el proyecto
- Fácil de encontrar código
- Testing aislado por capa
- Onboarding rápido de nuevos devs

### Negativas
- Más archivos que una arquitectura simple
- Puede parecer over-engineering al inicio
- Necesidad de mappers

### Mitigaciones
- Usar IDE con shortcuts
- Documentar patrón en AGENTS.md
- Usar MapStruct para reducir boilerplate

## Referencias
- [Spring Boot Reference](https://spring.io/projects/spring-boot)
- [MapStruct](https://mapstruct.org/)
- [Testing Spring Boot](https://spring.io/guides/gs/testing-web)

---

*ADR-002 - Syncria*
*Creado: 2026*
