import { useState, useEffect } from 'react'
import type { Appointment, AppointmentRequest } from '../types/appointmentTypes'
import type { Pet } from '../../pets/types/petTypes'
import type { Contact } from '../../contacts/types/types'
import Button from '../../../components/ui/Button'
import Input from '../../../components/ui/Input'

interface AppointmentFormModalProps {
  appointment: Appointment | null
  pets: Pet[]
  contacts: Contact[]
  onSubmit: (data: AppointmentRequest) => Promise<void>
  onCancel: () => void
}

export default function AppointmentFormModal({
  appointment,
  pets,
  contacts,
  onSubmit,
  onCancel,
}: AppointmentFormModalProps) {
  const isEditing = Boolean(appointment)

  const [petId, setPetId] = useState<number>(appointment?.petId || 0)
  const [contactId, setContactId] = useState<number>(appointment?.contactId || 0)
  const [title, setTitle] = useState(appointment?.title || '')
  const [reason, setReason] = useState(appointment?.reason || '')
  const [appointmentDate, setAppointmentDate] = useState(appointment?.appointmentDate || '')
  const [startTime, setStartTime] = useState(appointment?.startTime?.substring(0, 5) ?? '09:00')
  const [endTime, setEndTime] = useState(appointment?.endTime?.substring(0, 5) ?? '10:00')
  const [notes, setNotes] = useState(appointment?.notes || '')
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    if (!appointmentDate) {
      const today = new Date()
      setAppointmentDate(today.toISOString().split('T')[0] ?? '')
    }
  }, [appointmentDate])

  const validate = () => {
    const newErrors: Record<string, string> = {}
    if (!petId) newErrors.petId = 'Pet is required'
    if (!contactId) newErrors.contactId = 'Contact is required'
    if (!title.trim()) newErrors.title = 'Title is required'
    if (!appointmentDate) newErrors.appointmentDate = 'Date is required'
    if (!startTime) newErrors.startTime = 'Start time is required'
    if (!endTime) newErrors.endTime = 'End time is required'
    if (startTime && endTime && startTime >= endTime) newErrors.endTime = 'End time must be after start time'
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validate()) return

    setLoading(true)
    await onSubmit({
      petId,
      contactId,
      title: title.trim(),
      reason: reason || undefined,
      appointmentDate,
      startTime,
      endTime,
      notes: notes || undefined,
    })
    setLoading(false)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
      <div className="bg-white rounded-lg shadow-xl w-full max-w-2xl max-h-[90vh] overflow-y-auto">
        <div className="p-6">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">
            {isEditing ? 'Edit Appointment' : 'New Appointment'}
          </h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-1">
              <label className="block text-sm font-medium text-gray-700">Pet</label>
              <select
                className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
                value={petId || ''}
                onChange={(e) => setPetId(Number(e.target.value))}
              >
                <option value="">Select a pet...</option>
                {pets.map((p) => (
                  <option key={p.id} value={p.id}>{p.name} ({p.species})</option>
                ))}
              </select>
              {errors.petId && <p className="text-sm text-red-600">{errors.petId}</p>}
            </div>
            <div className="space-y-1">
              <label className="block text-sm font-medium text-gray-700">Contact</label>
              <select
                className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
                value={contactId || ''}
                onChange={(e) => setContactId(Number(e.target.value))}
              >
                <option value="">Select a contact...</option>
                {contacts.map((c) => (
                  <option key={c.id} value={c.id}>{c.name}</option>
                ))}
              </select>
              {errors.contactId && <p className="text-sm text-red-600">{errors.contactId}</p>}
            </div>
            <Input
              label="Title"
              value={title}
              onChange={(e) => setTitle(e.target.value)}
              error={errors.title}
              placeholder="Annual checkup"
            />
            <Input
              label="Reason"
              value={reason}
              onChange={(e) => setReason(e.target.value)}
              placeholder="Routine examination"
            />
            <Input
              label="Date"
              type="date"
              value={appointmentDate}
              onChange={(e) => setAppointmentDate(e.target.value)}
              error={errors.appointmentDate}
            />
            <div className="grid grid-cols-2 gap-4">
              <Input
                label="Start Time"
                type="time"
                value={startTime}
                onChange={(e) => setStartTime(e.target.value)}
                error={errors.startTime}
              />
              <Input
                label="End Time"
                type="time"
                value={endTime}
                onChange={(e) => setEndTime(e.target.value)}
                error={errors.endTime}
              />
            </div>
            <div className="space-y-1">
              <label className="block text-sm font-medium text-gray-700">Notes</label>
              <textarea
                className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm"
                rows={3}
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
                placeholder="Optional notes..."
              />
            </div>
            <div className="flex justify-end gap-3 pt-4">
              <Button variant="secondary" onClick={onCancel} type="button">
                Cancel
              </Button>
              <Button type="submit" disabled={loading}>
                {loading ? 'Saving...' : isEditing ? 'Update' : 'Create'}
              </Button>
            </div>
          </form>
        </div>
      </div>
    </div>
  )
}
