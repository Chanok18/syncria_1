import { create } from 'zustand'
import { persist } from 'zustand/middleware'
import api from '../../../lib/api'
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/types'

interface AuthState {
  user: AuthResponse | null
  token: string | null
  isLoading: boolean
  error: string | null
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => void
  loadSession: () => void
  clearError: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isLoading: false,
      error: null,

      login: async (data) => {
        set({ isLoading: true, error: null })
        try {
          const response = await api.post<AuthResponse>('/auth/login', data)
          const { token, ...user } = response.data
          set({ user: { token, ...user }, token, isLoading: false, error: null })
        } catch (err: unknown) {
          const message =
            (err instanceof Object && err !== null && 'response' in err)
              ? (err as { response?: { data?: { message?: string } } }).response?.data?.message || 'Login failed'
              : 'Login failed'
          set({ error: message, isLoading: false })
          throw err
        }
      },

      register: async (data) => {
        set({ isLoading: true, error: null })
        try {
          const response = await api.post<AuthResponse>('/auth/register', data)
          const { token, ...user } = response.data
          set({ user: { token, ...user }, token, isLoading: false, error: null })
        } catch (err: unknown) {
          const message =
            (err instanceof Object && err !== null && 'response' in err)
              ? (err as { response?: { data?: { message?: string } } }).response?.data?.message || 'Registration failed'
              : 'Registration failed'
          set({ error: message, isLoading: false })
          throw err
        }
      },

      logout: () => {
        set({ user: null, token: null, error: null })
      },

      loadSession: () => {
        const state = useAuthStore.getState()
        if (state.token && !state.user) {
          set({ user: { token: state.token, email: '', fullName: '', role: '' } })
        }
      },

      clearError: () => set({ error: null }),
    }),
    {
      name: 'syncria-auth',
      partialize: (state) => ({
        user: state.user,
        token: state.token,
      }),
    }
  )
)
