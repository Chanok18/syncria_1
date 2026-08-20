import { useAppointmentStore } from '../store/appointmentStore'

export function useAppointments() {
  const store = useAppointmentStore()

  return {
    appointments: store.appointments,
    selectedAppointment: store.selectedAppointment,
    isLoading: store.isLoading,
    error: store.error,
    dateRange: store.dateRange,
    statusFilter: store.statusFilter,
    fetchAppointments: store.fetchAppointments,
    createAppointment: store.createAppointment,
    updateAppointment: store.updateAppointment,
    updateStatus: store.updateStatus,
    deleteAppointment: store.deleteAppointment,
    setDateRange: store.setDateRange,
    setStatusFilter: store.setStatusFilter,
    setSelectedAppointment: store.setSelectedAppointment,
    clearError: store.clearError,
  }
}
