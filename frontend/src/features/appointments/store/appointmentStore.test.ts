import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useAppointmentStore } from './appointmentStore'

vi.mock('../../../lib/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

import api from '../../../lib/api'
const mockApi = vi.mocked(api)

describe('appointmentStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    useAppointmentStore.setState({
      appointments: [],
      selectedAppointment: null,
      isLoading: false,
      error: null,
      dateRange: { start: new Date('2026-08-01'), end: new Date('2026-08-31') },
      statusFilter: null,
    })
  })

  it('has correct initial state', () => {
    const state = useAppointmentStore.getState()
    expect(state.appointments).toEqual([])
    expect(state.selectedAppointment).toBeNull()
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
    expect(state.statusFilter).toBeNull()
  })

  it('fetchAppointments loads appointments successfully', async () => {
    const mockResponse = {
      data: [
        { id: 1, title: 'Vacunacion', status: 'SCHEDULED', petName: 'Max', companyId: 1 },
        { id: 2, title: 'Control', status: 'COMPLETED', petName: 'Luna', companyId: 1 },
      ],
    }
    mockApi.get.mockResolvedValue(mockResponse)

    const start = new Date('2026-08-01')
    const end = new Date('2026-08-31')
    await useAppointmentStore.getState().fetchAppointments(start, end)

    const state = useAppointmentStore.getState()
    expect(state.appointments).toHaveLength(2)
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
  })

  it('fetchAppointments handles error', async () => {
    mockApi.get.mockRejectedValue(new Error('Network error'))

    await useAppointmentStore.getState().fetchAppointments(new Date(), new Date())

    const state = useAppointmentStore.getState()
    expect(state.error).toBe('Failed to load appointments')
    expect(state.isLoading).toBe(false)
  })

  it('createAppointment creates and refetches', async () => {
    mockApi.post.mockResolvedValue({})
    mockApi.get.mockResolvedValue({
      data: [{ id: 1, title: 'New Appt' }],
    })

    await useAppointmentStore.getState().createAppointment({
      petId: 1,
      contactId: 1,
      title: 'New Appt',
      appointmentDate: '2026-08-25',
      startTime: '10:00:00',
      endTime: '10:30:00',
    })

    expect(mockApi.post).toHaveBeenCalledWith('/appointments', {
      petId: 1,
      contactId: 1,
      title: 'New Appt',
      appointmentDate: '2026-08-25',
      startTime: '10:00:00',
      endTime: '10:30:00',
    })
  })

  it('createAppointment handles conflict error', async () => {
    mockApi.post.mockRejectedValue({
      response: { data: { message: 'Appointment conflict' } },
    })

    await expect(
      useAppointmentStore.getState().createAppointment({
        petId: 1,
        contactId: 1,
        title: 'Conflict',
        appointmentDate: '2026-08-25',
        startTime: '10:00:00',
        endTime: '10:30:00',
      })
    ).rejects.toThrow('Appointment conflict')

    const state = useAppointmentStore.getState()
    expect(state.error).toBe('Appointment conflict')
  })

  it('updateAppointment updates and refetches', async () => {
    mockApi.put.mockResolvedValue({})
    mockApi.get.mockResolvedValue({ data: [] })

    await useAppointmentStore.getState().updateAppointment(1, {
      petId: 1,
      contactId: 1,
      title: 'Updated',
      appointmentDate: '2026-08-25',
      startTime: '11:00:00',
      endTime: '11:30:00',
    })

    expect(mockApi.put).toHaveBeenCalledWith('/appointments/1', {
      petId: 1,
      contactId: 1,
      title: 'Updated',
      appointmentDate: '2026-08-25',
      startTime: '11:00:00',
      endTime: '11:30:00',
    })
  })

  it('updateStatus updates status and refetches', async () => {
    mockApi.put.mockResolvedValue({})
    mockApi.get.mockResolvedValue({ data: [] })

    await useAppointmentStore.getState().updateStatus(1, 'COMPLETED')

    expect(mockApi.put).toHaveBeenCalledWith('/appointments/1/status', { status: 'COMPLETED' })
  })

  it('deleteAppointment deletes and refetches', async () => {
    mockApi.delete.mockResolvedValue({})
    mockApi.get.mockResolvedValue({ data: [] })

    await useAppointmentStore.getState().deleteAppointment(1)

    expect(mockApi.delete).toHaveBeenCalledWith('/appointments/1')
  })

  it('setDateRange updates date range', () => {
    const start = new Date('2026-09-01')
    const end = new Date('2026-09-30')
    useAppointmentStore.getState().setDateRange(start, end)
    expect(useAppointmentStore.getState().dateRange).toEqual({ start, end })
  })

  it('setStatusFilter updates status filter', () => {
    useAppointmentStore.getState().setStatusFilter('SCHEDULED')
    expect(useAppointmentStore.getState().statusFilter).toBe('SCHEDULED')
  })

  it('setSelectedAppointment updates selected appointment', () => {
    const appt = { id: 1, title: 'Test' }
    useAppointmentStore.getState().setSelectedAppointment(appt as never)
    expect(useAppointmentStore.getState().selectedAppointment).toEqual(appt)
  })

  it('clearError clears error', () => {
    useAppointmentStore.setState({ error: 'some error' })
    useAppointmentStore.getState().clearError()
    expect(useAppointmentStore.getState().error).toBeNull()
  })
})
