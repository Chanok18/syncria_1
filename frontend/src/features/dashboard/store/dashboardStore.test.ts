import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useDashboardStore } from './dashboardStore'

vi.mock('../../../lib/api', () => ({
  default: {
    get: vi.fn(),
  },
}))

import api from '../../../lib/api'
const mockApi = vi.mocked(api)

describe('dashboardStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    useDashboardStore.setState({
      data: null,
      isLoading: false,
      error: null,
    })
  })

  it('has correct initial state', () => {
    const state = useDashboardStore.getState()
    expect(state.data).toBeNull()
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
  })

  it('fetchDashboard loads data successfully', async () => {
    const mockData = {
      data: {
        totalClients: 15,
        totalPets: 22,
        todayAppointments: 3,
        weekAppointments: 12,
        scheduledAppointments: 8,
        completedAppointments: 3,
        cancelledAppointments: 1,
        recentAppointments: [
          { id: 1, petName: 'Max', title: 'Vacunacion', status: 'SCHEDULED' },
        ],
        speciesDistribution: [
          { species: 'Perro', count: 12 },
          { species: 'Gato', count: 10 },
        ],
      },
    }
    mockApi.get.mockResolvedValue(mockData)

    await useDashboardStore.getState().fetchDashboard()

    const state = useDashboardStore.getState()
    expect(state.data).not.toBeNull()
    expect(state.data?.totalClients).toBe(15)
    expect(state.data?.totalPets).toBe(22)
    expect(state.data?.todayAppointments).toBe(3)
    expect(state.data?.scheduledAppointments).toBe(8)
    expect(state.data?.recentAppointments).toHaveLength(1)
    expect(state.data?.speciesDistribution).toHaveLength(2)
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
  })

  it('fetchDashboard handles error', async () => {
    mockApi.get.mockRejectedValue(new Error('Network error'))

    await useDashboardStore.getState().fetchDashboard()

    const state = useDashboardStore.getState()
    expect(state.error).toBe('Failed to load dashboard data')
    expect(state.isLoading).toBe(false)
    expect(state.data).toBeNull()
  })

  it('fetchDashboard sets isLoading during fetch', async () => {
    let resolvePromise: (value: unknown) => void
    const pendingPromise = new Promise((resolve) => {
      resolvePromise = resolve
    })
    mockApi.get.mockReturnValue(pendingPromise as never)

    const fetchPromise = useDashboardStore.getState().fetchDashboard()
    expect(useDashboardStore.getState().isLoading).toBe(true)

    resolvePromise!({ data: { totalClients: 0 } })
    await fetchPromise

    expect(useDashboardStore.getState().isLoading).toBe(false)
  })

  it('clearError clears error', () => {
    useDashboardStore.setState({ error: 'some error' })
    useDashboardStore.getState().clearError()
    expect(useDashboardStore.getState().error).toBeNull()
  })
})
