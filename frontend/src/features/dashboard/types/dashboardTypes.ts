export type AppointmentStatus = 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'

export interface RecentAppointment {
  id: number
  petName: string
  contactName: string
  title: string
  appointmentDate: string
  startTime: string
  endTime: string
  status: AppointmentStatus
}

export interface SpeciesCount {
  species: string
  count: number
}

export interface DashboardData {
  totalClients: number
  totalPets: number
  todayAppointments: number
  weekAppointments: number
  scheduledAppointments: number
  completedAppointments: number
  cancelledAppointments: number
  recentAppointments: RecentAppointment[]
  speciesDistribution: SpeciesCount[]
}
