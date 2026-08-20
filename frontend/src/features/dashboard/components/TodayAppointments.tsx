import { useNavigate } from 'react-router-dom'
import type { RecentAppointment } from '../types/dashboardTypes'
import Badge from '../../../components/ui/Badge'

interface TodayAppointmentsProps {
  appointments: RecentAppointment[]
  isLoading: boolean
}

const statusBadgeVariant: Record<string, 'info' | 'success' | 'danger'> = {
  SCHEDULED: 'info',
  COMPLETED: 'success',
  CANCELLED: 'danger',
}

const statusLabel: Record<string, string> = {
  SCHEDULED: 'Programada',
  COMPLETED: 'Completada',
  CANCELLED: 'Cancelada',
}

export default function TodayAppointments({ appointments, isLoading }: TodayAppointmentsProps) {
  const navigate = useNavigate()

  if (isLoading) {
    return (
      <div className="bg-white rounded-xl border border-gray-200 p-6">
        <h3 className="text-lg font-semibold text-gray-900 mb-4">Citas de Hoy</h3>
        <div className="space-y-3">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="animate-pulse flex items-center gap-4 p-3 rounded-lg bg-gray-50">
              <div className="h-4 bg-gray-200 rounded w-16" />
              <div className="h-4 bg-gray-200 rounded w-32" />
              <div className="h-4 bg-gray-200 rounded w-24" />
            </div>
          ))}
        </div>
      </div>
    )
  }

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-lg font-semibold text-gray-900">Citas de Hoy</h3>
        <button
          onClick={() => navigate('/appointments')}
          className="text-sm text-primary-600 hover:text-primary-700 font-medium"
        >
          Ver todas
        </button>
      </div>

      {appointments.length === 0 ? (
        <div className="text-center py-8">
          <svg className="mx-auto h-12 w-12 text-gray-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <p className="mt-2 text-sm text-gray-500">No hay citas programadas para hoy</p>
        </div>
      ) : (
        <div className="space-y-3">
          {appointments.map((apt) => (
            <div
              key={apt.id}
              className="flex items-center gap-4 p-3 rounded-lg bg-gray-50 hover:bg-gray-100 transition-colors cursor-pointer"
              onClick={() => navigate('/appointments')}
            >
              <div className="flex-shrink-0 text-sm font-mono text-gray-600 w-16">
                {apt.startTime?.substring(0, 5)}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">{apt.title}</p>
                <p className="text-xs text-gray-500">{apt.petName} - {apt.contactName}</p>
              </div>
              <Badge variant={statusBadgeVariant[apt.status] || 'default'}>
                {statusLabel[apt.status] || apt.status}
              </Badge>
            </div>
          ))}
        </div>
      )}
    </div>
  )
}
