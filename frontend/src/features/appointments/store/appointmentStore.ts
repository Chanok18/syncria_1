import { create } from 'zustand'
import api from '../../../lib/api'
import type { Appointment, AppointmentRequest, AppointmentStatus } from '../types/appointmentTypes'

interface AppointmentState {
  appointments: Appointment[]
  selectedAppointment: Appointment | null
  isLoading: boolean
  error: string | null
  dateRange: { start: Date; end: Date }
  statusFilter: AppointmentStatus | null

  fetchAppointments: (start: Date, end: Date) => Promise<void>
  createAppointment: (data: AppointmentRequest) => Promise<void>
  updateAppointment: (id: number, data: AppointmentRequest) => Promise<void>
  updateStatus: (id: number, status: AppointmentStatus) => Promise<void>
  deleteAppointment: (id: number) => Promise<void>
  setDateRange: (start: Date, end: Date) => void
  setStatusFilter: (status: AppointmentStatus | null) => void
  setSelectedAppointment: (appointment: Appointment | null) => void
  clearError: () => void
}

export const useAppointmentStore = create<AppointmentState>((set, get) => ({
  appointments: [],
  selectedAppointment: null,
  isLoading: false,
  error: null,
  dateRange: { start: new Date(), end: new Date() },
  statusFilter: null,

  fetchAppointments: async (start: Date, end: Date) => {
    set({ isLoading: true, error: null, dateRange: { start, end } })
    try {
      const startStr = start.toISOString().split('T')[0] ?? ''
      const endStr = end.toISOString().split('T')[0] ?? ''
      const params = new URLSearchParams({ startDate: startStr, endDate: endStr })
      const response = await api.get<Appointment[]>(`/appointments/range?${params}`)
      set({ appointments: response.data })
    } catch {
      set({ error: 'Failed to load appointments' })
    } finally {
      set({ isLoading: false })
    }
  },

  createAppointment: async (data) => {
    set({ isLoading: true, error: null })
    try {
      await api.post('/appointments', data)
      const { dateRange } = get()
      await get().fetchAppointments(dateRange.start, dateRange.end)
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Failed to create appointment'
      set({ error: message })
      throw new Error(message)
    } finally {
      set({ isLoading: false })
    }
  },

  updateAppointment: async (id, data) => {
    set({ isLoading: true, error: null })
    try {
      await api.put(`/appointments/${id}`, data)
      const { dateRange } = get()
      await get().fetchAppointments(dateRange.start, dateRange.end)
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Failed to update appointment'
      set({ error: message })
      throw new Error(message)
    } finally {
      set({ isLoading: false })
    }
  },

  updateStatus: async (id, status) => {
    set({ isLoading: true, error: null })
    try {
      await api.put(`/appointments/${id}/status`, { status })
      const { dateRange } = get()
      await get().fetchAppointments(dateRange.start, dateRange.end)
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })?.response?.data?.message || 'Failed to update status'
      set({ error: message })
      throw new Error(message)
    } finally {
      set({ isLoading: false })
    }
  },

  deleteAppointment: async (id) => {
    set({ isLoading: true, error: null })
    try {
      await api.delete(`/appointments/${id}`)
      const { dateRange } = get()
      await get().fetchAppointments(dateRange.start, dateRange.end)
    } catch {
      set({ error: 'Failed to delete appointment' })
      throw new Error('Failed to delete appointment')
    } finally {
      set({ isLoading: false })
    }
  },

  setDateRange: (start, end) => set({ dateRange: { start, end } }),
  setStatusFilter: (status) => set({ statusFilter: status }),
  setSelectedAppointment: (appointment) => set({ selectedAppointment: appointment }),
  clearError: () => set({ error: null }),
}))
