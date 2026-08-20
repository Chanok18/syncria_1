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
    } catch {
      set({ error: 'Failed to load dashboard data' })
    } finally {
      set({ isLoading: false })
    }
  },

  clearError: () => set({ error: null }),
}))
