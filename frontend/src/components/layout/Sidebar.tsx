export default function Sidebar() {
  return (
    <aside className="w-64 bg-white border-r border-gray-200 min-h-[calc(100vh-64px)]">
      <nav className="p-4">
        <ul className="space-y-2">
          <li>
            <a
              href="/"
              className="block px-4 py-2 text-gray-700 hover:bg-primary-50 rounded-md"
            >
              Inicio
            </a>
          </li>
          <li>
            <a
              href="/clients"
              className="block px-4 py-2 text-gray-700 hover:bg-primary-50 rounded-md"
            >
              Clientes
            </a>
          </li>
          <li>
            <a
              href="/pets"
              className="block px-4 py-2 text-gray-700 hover:bg-primary-50 rounded-md"
            >
              Mascotas
            </a>
          </li>
          <li>
            <a
              href="/appointments"
              className="block px-4 py-2 text-gray-700 hover:bg-primary-50 rounded-md"
            >
              Citas
            </a>
          </li>
        </ul>
      </nav>
    </aside>
  )
}
