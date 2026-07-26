# ADR-003: Estrategia Multi-tenant

## Estado
**Aprobado** - 2026

## Contexto
Syncria debe soportar múltiples empresas (tenants) con aislamiento de datos. Actualmente es un MVP para una sola empresa, pero debe estar preparado para evolucionar a SaaS multi-tenant.

## Decisión
Implementar **multi-tenant por base de datos compartida con columna `company_id`**.

### Estrategia

1. **Todas las entidades** incluyen campo `company_id`
2. **TenantContext** (ThreadLocal) almacena company_id actual
3. **Filtro JPA** aplica `WHERE company_id = ?` automáticamente
4. **Header** `X-Company-Id` en requests autenticados
5. **JWT Token** contiene company_id del usuario

### Modelo de Datos

```java
@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "company_id", nullable = false, updatable = false)
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

### TenantContext

```java
public class TenantContext {
    private static final ThreadLocal<Long> CURRENT_COMPANY_ID = new ThreadLocal<>();
    
    public static void setCurrentCompanyId(Long companyId) {
        CURRENT_COMPANY_ID.set(companyId);
    }
    
    public static Long getCurrentCompanyId() {
        return CURRENT_COMPANY_ID.get();
    }
    
    public static void clear() {
        CURRENT_COMPANY_ID.remove();
    }
}
```

### Filtro JPA

```java
@Component
@RequiredArgsConstructor
public class TenantFilter implements Filter {
    
    private final JwtTokenProvider tokenProvider;
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // Extract company ID from JWT or header
        Long companyId = extractCompanyId(httpRequest);
        TenantContext.setCurrentCompanyId(companyId);
        
        try {
            chain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }
    
    private Long extractCompanyId(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && tokenProvider.validateToken(token)) {
            return tokenProvider.getCompanyIdFromToken(token);
        }
        return null;
    }
}
```

### Repository con Filtro

```java
@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    // Explícito: filtra por company_id
    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId AND c.deleted = false")
    Page<Client> findAllByCompanyId(@Param("companyId") Long companyId, Pageable pageable);
    
    // Con search
    @Query("SELECT c FROM Client c WHERE c.companyId = :companyId " +
           "AND c.deleted = false " +
           "AND (LOWER(c.firstName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(c.lastName) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Client> searchByCompanyId(
        @Param("companyId") Long companyId, 
        @Param("search") String search, 
        Pageable pageable);
}
```

### Service

```java
@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    
    private final ClientRepository clientRepository;
    
    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponseDTO> getAll(int page, int size, String search) {
        Long companyId = TenantUtils.getCurrentCompanyId();
        
        Page<Client> clients;
        if (search != null && !search.isBlank()) {
            clients = clientRepository.searchByCompanyId(companyId, search, 
                PageRequest.of(page, size, Sort.by("lastName")));
        } else {
            clients = clientRepository.findAllByCompanyId(companyId, 
                PageRequest.of(page, size, Sort.by("lastName")));
        }
        
        return clients.map(this::toResponse);
    }
}
```

## Alternativas Consideradas

### 1. Base de datos separada por tenant
- **Pros**: Aislamiento completo
- **Contras**: Complejidad operacional, costos altos, difícil de escalar
- **Veredicto**: Prematuro para MVP

### 2. Schema separado por tenant
- **Pros**: Aislamiento medio, mejor que columna
- **Contras**: Complejidad de migraciones, overhead de conexión
- **Veredicto**: Buena opción futura

### 3. Columna company_id (Elegida)
- **Pros**: Simple, escalable, fácil de implementar
- **Contras**: Riesgo de filtrado incorrecto
- **Veredicto**: Mejor para MVP y crecimiento gradual

### 4. Row-level security (PostgreSQL)
- **Pros**: Filtrado a nivel de BD
- **Contras**: Más complejo, harder de debug
- **Veredicto**: Puede implementarse después

## Cómo Evolucionar a SaaS

Cuando Syncria esté listo para SaaS:

1. **Asignar company_id al login**:
   ```java
   // En AuthService
   public AuthResponse login(LoginRequest request) {
       User user = userRepository.findByEmail(request.email());
       // Asignar company_id del usuario
       String token = tokenProvider.generateToken(user, user.getCompanyId());
       return new AuthResponse(token);
   }
   ```

2. **Activar filtro JPA**:
   ```java
   // Ya está implementado, solo necesita activarse
   @Component
   public class TenantFilter implements Filter {
       // Ya filtra por company_id
   }
   ```

3. **Aislar datos por empresa**:
   ```java
   // Ya está implementado en repositories
   // Solo necesita companyId del token
   ```

4. **UI multi-tenant**:
   - Seleccionar empresa al login
   - Cambiar de empresa (si el usuario pertenece a múltiples)
   - Configuración por empresa

## Consecuencias

### Positivas
- Preparado para SaaS sin reescribir
- Simple de implementar en MVP
- Escalable a miles de tenants
- Fácil de migrar a schema separado si es necesario

### Negativas
- Riesgo de filtrado incorrecto si se olvida `company_id`
- Performance puede degradarse con muchos tenants
- Backup y restore son más complejos

### Mitigaciones
- Tests que verifican aislamiento
- Filtro JPA automático (no depende del developer)
- Monitoreo de queries
- Índices en `company_id`

## Referencias
- [Multi-tenancy in SaaS](https://www.saas-journal.com/multi-tenancy-in-saas-architecture/)
- [Hibernate Multi-tenancy](https://docs.jboss.org/hibernate/orm/5.6/userguide/html_single/Hibernate_User_Guide.html#multitenacy)
- [Spring Boot Multi-tenant](https://spring.io/guides/gs/multi-tenant)

---

*ADR-003 - Syncria*
*Creado: 2026*
