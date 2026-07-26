# ADR-004: Gestión de Estado Frontend - Zustand

## Estado
**Aprobado** - 2026

## Contexto
El frontend de Syncria necesita:
1. Estado global para autenticación
2. Estado por feature (clientes, mascotas, citas)
3. Manejo de API calls con loading/error states
4. Persistencia de某些状态 (token, preferencias)
5. Performance sin re-renders innecesarios

## Decisión
Usar **Zustand** como librería de gestión de estado global.

### Por qué Zustand

1. **Simplicidad**: Sin reducers, actions, o boilerplate
2. **Performance**: Selectores optimizados por defecto
3. **Bundle size**: ~2KB (mínimo)
4. **TypeScript**: Soporte nativo excelente
5. **Persistencia**: Middleware integrado
6. **DevTools**: Soporte para Redux DevTools

### Estructura de Stores

```
frontend/src/
├── features/
│   ├── auth/
│   │   └── store/
│   │       └── authStore.ts
│   ├── clients/
│   │   └── store/
│   │       └── clientStore.ts
│   ├── pets/
│   │   └── store/
│   │       └── petStore.ts
│   └── appointments/
│       └── store/
│           └── appointmentStore.ts
└── stores/
    └── uiStore.ts
```

### Patrón de Store

```typescript
// features/auth/store/authStore.ts
import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { authApi } from '../api/authApi';
import type { User, LoginCredentials, AuthResponse } from '../types';

interface AuthState {
    // State
    user: User | null;
    token: string | null;
    isAuthenticated: boolean;
    loading: boolean;
    error: string | null;
    
    // Actions
    login: (credentials: LoginCredentials) => Promise<void>;
    logout: () => void;
    checkAuth: () => Promise<void>;
    clearError: () => void;
}

export const useAuthStore = create<AuthState>()(
    persist(
        (set, get) => ({
            // Initial state
            user: null,
            token: null,
            isAuthenticated: false,
            loading: false,
            error: null,
            
            // Actions
            login: async (credentials) => {
                set({ loading: true, error: null });
                try {
                    const response: AuthResponse = await authApi.login(credentials);
                    set({
                        user: response.user,
                        token: response.token,
                        isAuthenticated: true,
                        loading: false,
                    });
                } catch (error) {
                    set({
                        error: error.response?.data?.message || 'Login failed',
                        loading: false,
                    });
                    throw error;
                }
            },
            
            logout: () => {
                set({
                    user: null,
                    token: null,
                    isAuthenticated: false,
                });
            },
            
            checkAuth: async () => {
                const { token } = get();
                if (!token) {
                    set({ isAuthenticated: false });
                    return;
                }
                
                try {
                    const user = await authApi.getCurrentUser(token);
                    set({ user, isAuthenticated: true });
                } catch {
                    set({
                        user: null,
                        token: null,
                        isAuthenticated: false,
                    });
                }
            },
            
            clearError: () => set({ error: null }),
        }),
        {
            name: 'auth-storage', // unique name for localStorage
            partialize: (state) => ({
                token: state.token, // only persist token
            }),
        }
    )
);
```

### Store de Clientes

```typescript
// features/clients/store/clientStore.ts
import { create } from 'zustand';
import { clientApi } from '../api/clientApi';
import type { Client, CreateClientDTO, UpdateClientDTO, PaginatedResponse } from '../types';

interface ClientState {
    // State
    clients: Client[];
    selectedClient: Client | null;
    loading: boolean;
    error: string | null;
    pagination: {
        page: number;
        size: number;
        total: number;
    };
    
    // Actions
    fetchClients: (page?: number, search?: string) => Promise<void>;
    fetchClientById: (id: number) => Promise<void>;
    createClient: (data: CreateClientDTO) => Promise<Client>;
    updateClient: (id: number, data: UpdateClientDTO) => Promise<void>;
    deleteClient: (id: number) => Promise<void>;
    selectClient: (client: Client | null) => void;
    clearError: () => void;
}

export const useClientStore = create<ClientState>((set, get) => ({
    // Initial state
    clients: [],
    selectedClient: null,
    loading: false,
    error: null,
    pagination: {
        page: 0,
        size: 20,
        total: 0,
    },
    
    // Actions
    fetchClients: async (page = 0, search) => {
        set({ loading: true, error: null });
        try {
            const response: PaginatedResponse<Client> = await clientApi.getAll(page, search);
            set({
                clients: response.content,
                pagination: {
                    page: response.number,
                    size: response.size,
                    total: response.totalElements,
                },
                loading: false,
            });
        } catch (error) {
            set({
                error: error.response?.data?.message || 'Failed to fetch clients',
                loading: false,
            });
        }
    },
    
    fetchClientById: async (id) => {
        set({ loading: true, error: null });
        try {
            const client = await clientApi.getById(id);
            set({ selectedClient: client, loading: false });
        } catch (error) {
            set({
                error: error.response?.data?.message || 'Failed to fetch client',
                loading: false,
            });
        }
    },
    
    createClient: async (data) => {
        set({ loading: true, error: null });
        try {
            const newClient = await clientApi.create(data);
            set((state) => ({
                clients: [...state.clients, newClient],
                loading: false,
            }));
            return newClient;
        } catch (error) {
            set({
                error: error.response?.data?.message || 'Failed to create client',
                loading: false,
            });
            throw error;
        }
    },
    
    updateClient: async (id, data) => {
        set({ loading: true, error: null });
        try {
            const updatedClient = await clientApi.update(id, data);
            set((state) => ({
                clients: state.clients.map((c) =>
                    c.id === id ? updatedClient : c
                ),
                selectedClient: state.selectedClient?.id === id ? updatedClient : state.selectedClient,
                loading: false,
            }));
        } catch (error) {
            set({
                error: error.response?.data?.message || 'Failed to update client',
                loading: false,
            });
            throw error;
        }
    },
    
    deleteClient: async (id) => {
        set({ loading: true, error: null });
        try {
            await clientApi.delete(id);
            set((state) => ({
                clients: state.clients.filter((c) => c.id !== id),
                selectedClient: state.selectedClient?.id === id ? null : state.selectedClient,
                loading: false,
            }));
        } catch (error) {
            set({
                error: error.response?.data?.message || 'Failed to delete client',
                loading: false,
            });
            throw error;
        }
    },
    
    selectClient: (client) => set({ selectedClient: client }),
    clearError: () => set({ error: null }),
}));
```

### Uso en Componentes

```typescript
// features/clients/components/ClientList.tsx
import { useEffect } from 'react';
import { useClientStore } from '../store/clientStore';

export function ClientList() {
    const { 
        clients, 
        loading, 
        error, 
        pagination,
        fetchClients 
    } = useClientStore();
    
    useEffect(() => {
        fetchClients();
    }, [fetchClients]);
    
    if (loading) return <Loading />;
    if (error) return <Error message={error} />;
    
    return (
        <div>
            {clients.map((client) => (
                <ClientCard key={client.id} client={client} />
            ))}
            <Pagination 
                total={pagination.total}
                page={pagination.page}
                size={pagination.size}
                onPageChange={(page) => fetchClients(page)}
            />
        </div>
    );
}

// features/clients/components/ClientForm.tsx
import { useState } from 'react';
import { useClientStore } from '../store/clientStore';
import type { CreateClientDTO } from '../types';

export function ClientForm() {
    const [formData, setFormData] = useState<CreateClientDTO>({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
    });
    
    const { createClient, loading, error } = useClientStore();
    
    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await createClient(formData);
            // Reset form or redirect
        } catch (err) {
            // Error is already in store
        }
    };
    
    return (
        <form onSubmit={handleSubmit}>
            {error && <ErrorMessage message={error} />}
            {/* Form fields */}
            <Button type="submit" disabled={loading}>
                {loading ? 'Creating...' : 'Create Client'}
            </Button>
        </form>
    );
}
```

## Alternativas Consideradas

### 1. Redux Toolkit
- **Pros**: Ecosistema grande, DevTools excelente, patrón establecido
- **Contras**: Boilerplate, más complejo, bundle size mayor
- **Veredicto**: Over-engineering para este proyecto

### 2. Context API + useReducer
- **Pros**: Sin dependencias externas, built-in React
- **Contras**: Performance issues, re-renders innecesarios, boilerplate
- **Veredicto**: No escala bien

### 3. MobX
- **Pros**: Reactivo, menos boilerplate que Redux
- **Contras**: Más complejo, menor comunidad que Zustand
- **Veredicto**: Zustand es más simple

### 4. Jotai
- **Pros**: Atómico, gran performance
- **Contras**: Paradigma diferente, más learning curve
- **Veredicto**: Zustand es más intuitivo

### 5. Zustand (Elegida)
- **Pros**: Simplicidad, performance, TypeScript, bundle size
- **Contras**: Menos features que Redux
- **Veredicto**: Balance perfecto

## Consecuencias

### Positivas
- Menos boilerplate que Redux
- Mejor performance que Context API
- TypeScript first
- Fácil de aprender
- DevTools support

### Negativas
- Menos community resources que Redux
- Algunas features avanzadas requieren middleware
- Patrón menos establecido en equipos grandes

### Mitigaciones
- Documentar patrones en AGENTS.md
- Usar persist middleware para auth
- Separar stores por feature
- Usar selectors para performance

## Referencias
- [Zustand Documentation](https://github.com/pmndrs/zustand)
- [Zustand vs Redux](https://www.reddit.com/r/reactjs/comments/um44bb/zustand_vs_redux/)
- [Zustand Best Practices](https://www.gitclear.com/blog/zustand_best_practices)

---

*ADR-004 - Syncria*
*Creado: 2026*
