export default function Home() {
  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900">
        Bienvenido a Syncria
      </h1>
      <p className="mt-2 text-gray-600">
        Plataforma CRM para clínicas veterinarias
      </p>
      <div className="mt-8 grid grid-cols-1 md:grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <h2 className="text-lg font-semibold text-gray-800">Clientes</h2>
          <p className="text-gray-600">Gestiona tus clientes</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <h2 className="text-lg font-semibold text-gray-800">Mascotas</h2>
          <p className="text-gray-600">Registra mascotas</p>
        </div>
        <div className="bg-white p-6 rounded-lg shadow-sm border border-gray-200">
          <h2 className="text-lg font-semibold text-gray-800">Citas</h2>
          <p className="text-gray-600">Agenda tu día</p>
        </div>
      </div>
    </div>
  )
}
