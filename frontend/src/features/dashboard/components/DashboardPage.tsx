import { useNavigate } from 'react-router-dom'
import { useDashboard } from '../hooks/useDashboard'
import StatCard from './StatCard'
import TodayAppointments from './TodayAppointments'
import QuickActions from './QuickActions'
import Badge from '../../../components/ui/Badge'

const statusColor: Record<string, 'info' | 'success' | 'danger'> = {
  SCHEDULED: 'info',
  COMPLETED: 'success',
  CANCELLED: 'danger',
}

const statusLabel: Record<string, string> = {
  SCHEDULED: 'Programadas',
  COMPLETED: 'Completadas',
  CANCELLED: 'Canceladas',
}

export default function DashboardPage() {
  const { data, isLoading, error } = useDashboard()
  const navigate = useNavigate()

  if (error) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="text-center">
          <p className="text-red-600 font-medium">Error loading dashboard</p>
          <p className="text-sm text-gray-500 mt-1">{error}</p>
        </div>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
          <p className="mt-1 text-sm text-gray-500">Resumen de tu clinica veterinaria</p>
        </div>
        <button
          onClick={() => navigate('/appointments')}
          className="px-4 py-2 bg-primary-600 text-white rounded-lg hover:bg-primary-700 transition-colors font-medium text-sm"
        >
          Ver Calendario
        </button>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Clientes"
          value={isLoading ? '...' : data?.totalClients ?? 0}
          color="blue"
          icon={
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
          }
        />
        <StatCard
          title="Mascotas"
          value={isLoading ? '...' : data?.totalPets ?? 0}
          color="green"
          icon={
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
            </svg>
          }
        />
        <StatCard
          title="Citas Hoy"
          value={isLoading ? '...' : data?.todayAppointments ?? 0}
          color="purple"
          icon={
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
            </svg>
          }
        />
        <StatCard
          title="Citas Semana"
          value={isLoading ? '...' : data?.weekAppointments ?? 0}
          color="orange"
          icon={
            <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-6 9l2 2 4-4" />
            </svg>
          }
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2">
          <TodayAppointments
            appointments={data?.recentAppointments ?? []}
            isLoading={isLoading}
          />
        </div>

        <div className="space-y-6">
          <div className="bg-white rounded-xl border border-gray-200 p-6">
            <h3 className="text-lg font-semibold text-gray-900 mb-4">Estado de Citas</h3>
            {isLoading ? (
              <div className="space-y-3">
                {[...Array(3)].map((_, i) => (
                  <div key={i} className="animate-pulse flex items-center justify-between p-3 rounded-lg bg-gray-50">
                    <div className="h-4 bg-gray-200 rounded w-24" />
                    <div className="h-4 bg-gray-200 rounded w-12" />
                  </div>
                ))}
              </div>
            ) : (
              <div className="space-y-3">
                {(['SCHEDULED', 'COMPLETED', 'CANCELLED'] as const).map((status) => {
                  const count = status === 'SCHEDULED' ? data?.scheduledAppointments
                    : status === 'COMPLETED' ? data?.completedAppointments
                    : data?.cancelledAppointments
                  return (
                    <div key={status} className="flex items-center justify-between p-3 rounded-lg bg-gray-50">
                      <div className="flex items-center gap-3">
                        <Badge variant={statusColor[status]}>
                          {statusLabel[status]}
                        </Badge>
                      </div>
                      <span className="text-lg font-bold text-gray-900">{count ?? 0}</span>
                    </div>
                  )
                })}
              </div>
            )}
          </div>

          {data?.speciesDistribution && data.speciesDistribution.length > 0 && (
            <div className="bg-white rounded-xl border border-gray-200 p-6">
              <h3 className="text-lg font-semibold text-gray-900 mb-4">Mascotas por Especie</h3>
              <div className="space-y-3">
                {data.speciesDistribution.map((item) => {
                  const total = data.totalPets || 1
                  const percentage = Math.round((item.count / total) * 100)
                  return (
                    <div key={item.species}>
                      <div className="flex items-center justify-between mb-1">
                        <span className="text-sm font-medium text-gray-700">{item.species}</span>
                        <span className="text-sm text-gray-500">{item.count} ({percentage}%)</span>
                      </div>
                      <div className="w-full bg-gray-200 rounded-full h-2">
                        <div
                          className="bg-primary-600 h-2 rounded-full transition-all duration-500"
                          style={{ width: `${percentage}%` }}
                        />
                      </div>
                    </div>
                  )
                })}
              </div>
            </div>
          )}
        </div>
      </div>

      <QuickActions />
    </div>
  )
}
