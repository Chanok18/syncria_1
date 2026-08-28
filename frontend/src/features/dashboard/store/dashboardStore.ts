import { create } from 'zustand'
import api from '../../../lib/api'
import type { DashboardData } from '../types/dashboardTypes'

interface DashboardState {
  data: DashboardData | null
  isLoading: boolean
  error: string | null

  fetchDashboard: () => Promise<void>
  clearError: () => void
}

export const useDashboardStore = create<DashboardState>((set) => ({
  data: null,
  isLoading: false,
  error: null,

  fetchDashboard: async () => {
    set({ isLoading: true, error: null })
    try {
      const response = await api.get<DashboardData>('/dashboard')
      set({ data: response.data })
    } catch (err: unknown) {
      let msg = 'Failed to load dashboard data'
      if (err instanceof Object && err !== null && 'response' in err) {
        const axiosErr = err as { response?: { status?: number, data?: unknown } }
        msg = `Dashboard error ${axiosErr.response?.status || ''}`
      }
      set({ error: msg })
    } finally {
      set({ isLoading: false })
    }
  },

  clearError: () => set({ error: null }),
}))
