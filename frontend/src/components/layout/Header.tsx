import { useAuth } from '../../features/auth/hooks/useAuth'
import { useNavigate } from 'react-router-dom'

export default function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold text-primary-600">Syncria</h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-gray-600">
            {user?.fullName || 'Usuario'}
          </span>
          <button
            onClick={handleLogout}
            className="text-sm text-gray-500 hover:text-red-600 transition-colors"
          >
            Cerrar sesion
          </button>
        </div>
      </div>
    </header>
  )
}
