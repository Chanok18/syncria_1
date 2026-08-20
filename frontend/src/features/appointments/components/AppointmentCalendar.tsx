import { useEffect, useState, useMemo, useCallback } from 'react'
import { Calendar, dateFnsLocalizer, type View } from 'react-big-calendar'
import { format, startOfWeek, addDays, addMonths, subMonths, startOfMonth, endOfMonth, endOfWeek } from 'date-fns'
import { es } from 'date-fns/locale'
import 'react-big-calendar/lib/css/react-big-calendar.css'
import { useAppointments } from '../hooks/useAppointments'
import { getEventStyle } from '../utils/calendarStyles'
import AppointmentFormModal from './AppointmentFormModal'
import AppointmentDetailModal from './AppointmentDetailModal'
import Button from '../../../components/ui/Button'
import type { Appointment, CalendarEvent, AppointmentRequest } from '../types/appointmentTypes'
import toast from 'react-hot-toast'
import api from '../../../lib/api'
import type { Pet } from '../../pets/types/petTypes'
import type { Contact } from '../../contacts/types/types'

const locales = { 'es': es }
const localizer = dateFnsLocalizer({
  format,
  startOfWeek,
  getDay: (date: Date) => date.getDay(),
  locales,
})

export default function AppointmentCalendar() {
  const {
    appointments,
    isLoading,
    error,
    fetchAppointments,
    createAppointment,
    updateAppointment,
    updateStatus,
    deleteAppointment,
    clearError,
  } = useAppointments()

  const [currentDate, setCurrentDate] = useState(new Date())
  const [currentView, setCurrentView] = useState<View>('month')
  const [showForm, setShowForm] = useState(false)
  const [editTarget, setEditTarget] = useState<Appointment | null>(null)
  const [detailTarget, setDetailTarget] = useState<Appointment | null>(null)
  const [pets, setPets] = useState<Pet[]>([])
  const [contacts, setContacts] = useState<Contact[]>([])

  const getDateRange = useCallback((date: Date, view: View) => {
    let start: Date, end: Date
    if (view === 'month') {
      start = startOfWeek(startOfMonth(date), { weekStartsOn: 1 })
      end = endOfWeek(endOfMonth(date), { weekStartsOn: 1 })
    } else if (view === 'week') {
      start = startOfWeek(date, { weekStartsOn: 1 })
      end = addDays(start, 6)
    } else {
      start = date
      end = date
    }
    return { start, end }
  }, [])

  useEffect(() => {
    const { start, end } = getDateRange(currentDate, currentView)
    fetchAppointments(start, end)
  }, [currentDate, currentView, fetchAppointments, getDateRange])

  useEffect(() => {
    if (error) {
      toast.error(error)
      clearError()
    }
  }, [error, clearError])

  useEffect(() => {
    api.get('/pets', { params: { size: 100 } }).then((res) => setPets(res.data.content))
    api.get('/contacts', { params: { size: 100 } }).then((res) => setContacts(res.data.content))
  }, [])

  const events: CalendarEvent[] = useMemo(() =>
    appointments.map((apt: Appointment) => ({
      id: apt.id,
      title: `${apt.petName} - ${apt.title}`,
      start: new Date(`${apt.appointmentDate}T${apt.startTime}`),
      end: new Date(`${apt.appointmentDate}T${apt.endTime}`),
      status: apt.status,
      petName: apt.petName,
      contactName: apt.contactName,
      reason: apt.reason,
    })),
    [appointments]
  )

  const handleSelectEvent = useCallback((event: CalendarEvent) => {
    const apt = appointments.find((a) => a.id === event.id)
    if (apt) setDetailTarget(apt)
  }, [appointments])

  const handleSelectSlot = useCallback(() => {
    setEditTarget(null)
    setShowForm(true)
  }, [])

  const handleNavigate = (action: 'prev' | 'next' | 'today') => {
    if (action === 'prev') {
      setCurrentDate(currentView === 'month' ? subMonths(currentDate, 1) : addDays(currentDate, -1))
    } else if (action === 'next') {
      setCurrentDate(currentView === 'month' ? addMonths(currentDate, 1) : addDays(currentDate, 1))
    } else {
      setCurrentDate(new Date())
    }
  }

  const handleFormSubmit = async (data: AppointmentRequest) => {
    try {
      if (editTarget) {
        await updateAppointment(editTarget.id, data)
        toast.success('Appointment updated successfully')
      } else {
        await createAppointment(data)
        toast.success('Appointment created successfully')
      }
      setShowForm(false)
      setEditTarget(null)
    } catch {
      toast.error(editTarget ? 'Failed to update appointment' : 'Failed to create appointment')
    }
  }

  const handleEdit = (apt: Appointment) => {
    setDetailTarget(null)
    setEditTarget(apt)
    setShowForm(true)
  }

  const handleStatusChange = async (apt: Appointment, status: 'COMPLETED' | 'CANCELLED') => {
    try {
      await updateStatus(apt.id, status)
      toast.success(`Appointment ${status.toLowerCase()}`)
      setDetailTarget(null)
    } catch {
      toast.error('Failed to update status')
    }
  }

  const handleDelete = async (apt: Appointment) => {
    try {
      await deleteAppointment(apt.id)
      toast.success('Appointment deleted')
      setDetailTarget(null)
    } catch {
      toast.error('Failed to delete appointment')
    }
  }

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-gray-900">Appointments</h1>
        <Button onClick={() => { setEditTarget(null); setShowForm(true) }}>
          New Appointment
        </Button>
      </div>

      <div className="flex items-center justify-between bg-white rounded-lg border border-gray-200 p-4">
        <div className="flex items-center gap-2">
          <Button variant="secondary" onClick={() => handleNavigate('prev')}>Prev</Button>
          <Button variant="secondary" onClick={() => handleNavigate('today')}>Today</Button>
          <Button variant="secondary" onClick={() => handleNavigate('next')}>Next</Button>
          <span className="ml-4 text-lg font-medium text-gray-900">
            {format(currentDate, 'MMMM yyyy', { locale: es })}
          </span>
        </div>
        <div className="flex gap-2">
          {(['month', 'week', 'day'] as View[]).map((view) => (
            <Button
              key={view}
              variant={currentView === view ? 'primary' : 'secondary'}
              onClick={() => setCurrentView(view)}
            >
              {view.charAt(0).toUpperCase() + view.slice(1)}
            </Button>
          ))}
        </div>
      </div>

      <div className="flex gap-4 mb-4">
        <div className="flex items-center gap-2">
          <span className="w-3 h-3 rounded" style={{ backgroundColor: '#dbeafe', borderLeft: '3px solid #3b82f6' }} />
          <span className="text-sm text-gray-600">Scheduled</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-3 h-3 rounded" style={{ backgroundColor: '#dcfce7', borderLeft: '3px solid #22c55e' }} />
          <span className="text-sm text-gray-600">Completed</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-3 h-3 rounded" style={{ backgroundColor: '#fee2e2', borderLeft: '3px solid #ef4444' }} />
          <span className="text-sm text-gray-600">Cancelled</span>
        </div>
      </div>

      <div style={{ height: '600px' }} className="bg-white rounded-lg border border-gray-200 p-4">
        {isLoading ? (
          <div className="flex items-center justify-center h-full">
            <div className="text-gray-500">Loading appointments...</div>
          </div>
        ) : (
          <Calendar
            localizer={localizer}
            events={events}
            startAccessor="start"
            endAccessor="end"
            view={currentView}
            date={currentDate}
            onNavigate={(date: Date) => setCurrentDate(date)}
            onView={(view: View) => setCurrentView(view)}
            onSelectEvent={handleSelectEvent}
            onSelectSlot={handleSelectSlot}
            selectable
            eventPropGetter={getEventStyle}
            culture="es"
            messages={{
              today: 'Hoy',
              previous: 'Anterior',
              next: 'Siguiente',
              month: 'Mes',
              week: 'Semana',
              day: 'Día',
              agenda: 'Agenda',
              noEventsInRange: 'No hay citas en este rango',
            }}
          />
        )}
      </div>

      {showForm && (
        <AppointmentFormModal
          appointment={editTarget}
          pets={pets}
          contacts={contacts}
          onSubmit={handleFormSubmit}
          onCancel={() => { setShowForm(false); setEditTarget(null) }}
        />
      )}

      {detailTarget && (
        <AppointmentDetailModal
          appointment={detailTarget}
          onEdit={() => handleEdit(detailTarget)}
          onStatusChange={(status) => handleStatusChange(detailTarget, status)}
          onDelete={() => handleDelete(detailTarget)}
          onCancel={() => setDetailTarget(null)}
        />
      )}
    </div>
  )
}
