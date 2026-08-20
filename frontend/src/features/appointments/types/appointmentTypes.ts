export type AppointmentStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'

export interface Appointment {
  id: number
  companyId: number
  petId: number
  contactId: number
  petName: string
  contactName: string
  title: string
  reason: string | null
  appointmentDate: string
  startTime: string
  endTime: string
  status: AppointmentStatus
  notes: string | null
  createdAt: string
  updatedAt: string | null
}

export interface AppointmentRequest {
  petId: number
  contactId: number
  title: string
  reason?: string
  appointmentDate: string
  startTime: string
  endTime: string
  status?: AppointmentStatus
  notes?: string
}

export interface CalendarEvent {
  id: number
  title: string
  start: Date
  end: Date
  status: AppointmentStatus
  petName: string
  contactName: string
  reason: string | null
}
