import { create } from 'zustand'
import api from '../../../lib/api'
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/types'

interface AuthState {
  user: AuthResponse | null
  isLoading: boolean
  isInitialized: boolean
  error: string | null
  login: (data: LoginRequest) => Promise<void>
  register: (data: RegisterRequest) => Promise<void>
  logout: () => Promise<void>
  initializeSession: () => Promise<void>
  clearError: () => void
}

export const useAuthStore = create<AuthState>()((set) => ({
  user: null,
  isLoading: false,
  isInitialized: false,
  error: null,

  login: async (data) => {
    set({ isLoading: true, error: null })
    try {
      const response = await api.post<AuthResponse>('/auth/login', data)
      set({ user: response.data, isLoading: false, error: null })
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
      set({ user: response.data, isLoading: false, error: null })
    } catch (err: unknown) {
      let message = 'Registration failed'
      if (err instanceof Object && err !== null && 'response' in err) {
        const axiosErr = err as { response?: { data?: { message?: string }, status?: number } }
        message = axiosErr.response?.data?.message || `Error ${axiosErr.response?.status || 'unknown'}`
      }
      set({ error: message, isLoading: false })
      throw err
    }
  },

  logout: async () => {
    try {
      await api.post('/auth/logout')
    } catch {
      // Cookie cleanup even if request fails
    }
    set({ user: null, error: null })
  },

  initializeSession: async () => {
    try {
      const response = await api.get<AuthResponse>('/auth/me')
      set({ user: response.data, isInitialized: true })
    } catch {
      set({ user: null, isInitialized: true })
    }
  },

  clearError: () => set({ error: null }),
}))
