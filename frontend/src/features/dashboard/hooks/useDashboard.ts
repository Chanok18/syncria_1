import { useEffect } from 'react'
import { useDashboardStore } from '../store/dashboardStore'

export function useDashboard() {
  const { data, isLoading, error, fetchDashboard, clearError } = useDashboardStore()

  useEffect(() => {
    fetchDashboard()
  }, [fetchDashboard])

  return { data, isLoading, error, clearError }
}
