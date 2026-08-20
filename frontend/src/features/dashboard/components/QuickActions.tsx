import { useNavigate } from 'react-router-dom'

interface QuickAction {
  label: string
  path: string
  icon: React.ReactNode
  color: string
}

const actions: QuickAction[] = [
  {
    label: 'Nueva Cita',
    path: '/appointments',
    icon: (
      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M12 6v6m0 0v6m0-6h6m-6 0H6" />
      </svg>
    ),
    color: 'bg-blue-600 hover:bg-blue-700',
  },
  {
    label: 'Nuevo Cliente',
    path: '/contacts/new',
    icon: (
      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
      </svg>
    ),
    color: 'bg-green-600 hover:bg-green-700',
  },
  {
    label: 'Nueva Mascota',
    path: '/pets/new',
    icon: (
      <svg className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
      </svg>
    ),
    color: 'bg-purple-600 hover:bg-purple-700',
  },
]

export default function QuickActions() {
  const navigate = useNavigate()

  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6">
      <h3 className="text-lg font-semibold text-gray-900 mb-4">Accesos Rapidos</h3>
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
        {actions.map((action) => (
          <button
            key={action.label}
            onClick={() => navigate(action.path)}
            className={`flex items-center justify-center gap-2 px-4 py-3 rounded-lg text-white font-medium transition-colors ${action.color}`}
          >
            {action.icon}
            {action.label}
          </button>
        ))}
      </div>
    </div>
  )
}
