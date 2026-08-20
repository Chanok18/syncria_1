import { NavLink } from 'react-router-dom'

export default function Sidebar() {
  const linkClass = ({ isActive }: { isActive: boolean }) =>
    `block px-4 py-2 rounded-md transition-colors ${
      isActive
        ? 'bg-primary-50 text-primary-700 font-medium'
        : 'text-gray-700 hover:bg-primary-50'
    }`

  return (
    <aside className="w-64 bg-white border-r border-gray-200 min-h-[calc(100vh-64px)]">
      <nav className="p-4">
        <ul className="space-y-2">
          <li>
            <NavLink to="/" end className={linkClass}>
              Dashboard
            </NavLink>
          </li>
          <li>
            <NavLink to="/contacts" className={linkClass}>
              Contacts
            </NavLink>
          </li>
          <li>
            <NavLink to="/pets" className={linkClass}>
              Mascotas
            </NavLink>
          </li>
          <li>
            <NavLink to="/appointments" className={linkClass}>
              Citas
            </NavLink>
          </li>
        </ul>
      </nav>
    </aside>
  )
}
