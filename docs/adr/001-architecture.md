# ADR-001: Arquitectura Modular por Capas

## Estado
**Aprobado** - 2026

## Contexto
Syncria es una plataforma CRM que necesita soportar múltiples industrias, escalar horizontalmente, y evolucionar a SaaS multi-tenant. Se necesita una arquitectura que permita:

1. Desarrollo paralelo por módulos
2. Mantenimiento a largo plazo
3. Escalabilidad independiente por功能
4. Equipo pequeño (1-3 desarrolladores)

## Decisión
Implementar una **arquitectura modular por capas** donde cada功能 del negocio (clients, pets, appointments) es un módulo independiente con sus propias capas (controller, service, repository, dto, entity).

### Estructura por Módulo
```
module/
├── client/
│   ├── controller/    # Capa de presentación
│   ├── service/       # Capa de negocio
│   ├── repository/    # Capa de acceso a datos
│   ├── dto/          # Data Transfer Objects
│   ├── mapper/       # Conversión entity ↔ dto
│   ├── entity/       # Entidades JPA
│   ├── validation/   # Validaciones
│   └── exception/    # Excepciones del módulo
```

### Capas Compartidas
```
shared/
├── entity/           # BaseEntity, auditoría
├── exception/        # Excepciones globales
├── utils/            # Utilidades
└── config/           # Configuración compartida
```

## Alternativas Consideradas

### 1. Clean Architecture Pura
- **Pros**: Separación perfecta de dependencias, altamente testeable
- **Contras**: Over-engineering para el tamaño del equipo, curva de aprendizaje alta, mucho boilerplate
- **Veredicto**: Demasiado complejo para un equipo pequeño

### 2. Arquitectura Hexagonal
- **Pros**: Puertos y adaptadores, flexibilidad
- **Contras**: Complejidad conceptual, más código de lo necesario
- **Veredicto**: Buena idea pero innecesaria para este caso

### 3. Microservicios
- **Pros**: Escalabilidad independiente, despliegue separado
- **Contras**: Complejidad operacional, equipo pequeño no puede mantener
- **Veredicto**: Prematuro, monolito modular es mejor punto de partida

### 4. Arquitectura Modular por Capas (Elegida)
- **Pros**: Simple, mantenible, permite crecimiento gradual
- **Contras**: Dependencias entre módulos pueden crecer
- **Veredicto**: Mejor balance para equipo pequeño y crecimiento futuro

## Consecuencias

### Positivas
- Desarrollo paralelo sin conflictos
- Testing aislado por módulo
- Fácil de entender para nuevos desarrolladores
- Permite extraer módulos a microservicios en el futuro

### Negativas
- Riesgo de dependencias circulares entre módulos
- Necesidad de interfaces compartidas en `shared/`
- Puede volverse monolítico si no se respetan las capas

### Mitigaciones
- Regla estricta: módulos NO dependen entre sí directamente
- Usar eventos o interfaces en `shared/` para comunicación
- Code reviews para detectar acoplamiento

## Referencias
- [Modular Monolith](https://www.barneybishop.com/blog/2020/05/modular-monolith/)
- [Spring Boot Modular Architecture](https://spring.io/projects/spring-boot)
- [Clean Architecture - Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)

---

*ADR-001 - Syncria*
*Creado: 2026*
