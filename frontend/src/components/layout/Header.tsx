export default function Header() {
  return (
    <header className="bg-white border-b border-gray-200 px-6 py-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-bold text-primary-600">Syncria</h1>
        <div className="flex items-center gap-4">
          <span className="text-sm text-gray-600">Usuario</span>
        </div>
      </div>
    </header>
  )
}
