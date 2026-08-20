import type { Appointment } from '../types/appointmentTypes'
import { appointmentColors } from '../utils/calendarStyles'
import Button from '../../../components/ui/Button'

interface AppointmentDetailModalProps {
  appointment: Appointment
  onEdit: () => void
  onStatusChange: (status: 'COMPLETED' | 'CANCELLED') => void
  onDelete: () => void
  onCancel: () => void
}

export default function AppointmentDetailModal({
  appointment,
  onEdit,
  onStatusChange,
  onDelete,
  onCancel,
}: AppointmentDetailModalProps) {
  const statusColor = appointmentColors[appointment.status] ?? appointmentColors.SCHEDULED
  const bgColor = statusColor?.bg ?? '#dbeafe'
  const textColor = statusColor?.text ?? '#1e40af'

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-md">
        <div className="p-6">
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-semibold text-gray-900">Appointment Details</h2>
            <span
              className="px-3 py-1 rounded-full text-sm font-medium"
              style={{ backgroundColor: bgColor, color: textColor }}
            >
              {appointment.status}
            </span>
          </div>

          <div className="space-y-3">
            <div>
              <p className="text-sm text-gray-500">Pet</p>
              <p className="font-medium">{appointment.petName}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Contact</p>
              <p className="font-medium">{appointment.contactName}</p>
            </div>
            <div>
              <p className="text-sm text-gray-500">Title</p>
              <p className="font-medium">{appointment.title}</p>
            </div>
            {appointment.reason && (
              <div>
                <p className="text-sm text-gray-500">Reason</p>
                <p className="font-medium">{appointment.reason}</p>
              </div>
            )}
            <div className="grid grid-cols-2 gap-4">
              <div>
                <p className="text-sm text-gray-500">Date</p>
                <p className="font-medium">{appointment.appointmentDate}</p>
              </div>
              <div>
                <p className="text-sm text-gray-500">Time</p>
                <p className="font-medium">{appointment.startTime} - {appointment.endTime}</p>
              </div>
            </div>
            {appointment.notes && (
              <div>
                <p className="text-sm text-gray-500">Notes</p>
                <p className="font-medium">{appointment.notes}</p>
              </div>
            )}
          </div>

          <div className="flex flex-wrap gap-2 mt-6 pt-4 border-t">
            {appointment.status === 'SCHEDULED' && (
              <>
                <Button variant="secondary" onClick={onEdit}>Edit</Button>
                <Button onClick={() => onStatusChange('COMPLETED')}>Complete</Button>
                <Button variant="danger" onClick={() => onStatusChange('CANCELLED')}>Cancel Apt</Button>
              </>
            )}
            <Button variant="danger" onClick={onDelete}>Delete</Button>
            <Button variant="secondary" onClick={onCancel}>Close</Button>
          </div>
        </div>
      </div>
    </div>
  )
}
