interface StatCardProps {
  title: string
  value: number | string
  icon: React.ReactNode
  color?: 'blue' | 'green' | 'purple' | 'orange' | 'red'
  trend?: {
    value: number
    isPositive: boolean
  }
}

const colorMap = {
  blue: 'bg-blue-50 text-blue-600',
  green: 'bg-green-50 text-green-600',
  purple: 'bg-purple-50 text-purple-600',
  orange: 'bg-orange-50 text-orange-600',
  red: 'bg-red-50 text-red-600',
}

export default function StatCard({ title, value, icon, color = 'blue', trend }: StatCardProps) {
  return (
    <div className="bg-white rounded-xl border border-gray-200 p-6 hover:shadow-md transition-shadow">
      <div className="flex items-center justify-between">
        <div className="flex-1">
          <p className="text-sm font-medium text-gray-500 uppercase tracking-wide">{title}</p>
          <p className="mt-2 text-3xl font-bold text-gray-900">{value}</p>
          {trend && (
            <p className={`mt-1 text-sm font-medium ${trend.isPositive ? 'text-green-600' : 'text-red-600'}`}>
              {trend.isPositive ? '+' : ''}{trend.value}% vs semana anterior
            </p>
          )}
        </div>
        <div className={`flex-shrink-0 p-3 rounded-full ${colorMap[color]}`}>
          {icon}
        </div>
      </div>
    </div>
  )
}
