import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { useContacts } from '../hooks/useContacts'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'
import Spinner from '../../../components/ui/Spinner'
import api from '../../../lib/api'
import type { Contact } from '../types/types'
import toast from 'react-hot-toast'

export default function ContactFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { createContact, updateContact, isLoading } = useContacts()
  const isEditing = Boolean(id)

  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [phone, setPhone] = useState('')
  const [address, setAddress] = useState('')
  const [notes, setNotes] = useState('')
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [pageLoading, setPageLoading] = useState(isEditing)

  useEffect(() => {
    if (!id) return
    api.get<Contact>(`/contacts/${id}`)
      .then((res) => {
        setName(res.data.name)
        setEmail(res.data.email)
        setPhone(res.data.phone ?? '')
        setAddress(res.data.address ?? '')
        setNotes(res.data.notes ?? '')
      })
      .catch(() => {
        toast.error('Contact not found')
        navigate('/contacts')
      })
      .finally(() => setPageLoading(false))
  }, [id, navigate])

  const validate = () => {
    const newErrors: Record<string, string> = {}
    if (!name.trim()) newErrors.name = 'Name is required'
    if (!email.trim()) newErrors.email = 'Email is required'
    else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) newErrors.email = 'Invalid email format'
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validate()) return

    const data = { name: name.trim(), email: email.trim(), phone: phone || undefined, address: address || undefined, notes: notes || undefined }

    try {
      if (isEditing && id) {
        await updateContact(Number(id), data)
        toast.success('Contact updated successfully')
      } else {
        await createContact(data)
        toast.success('Contact created successfully')
      }
      navigate('/contacts')
    } catch {
      toast.error(isEditing ? 'Failed to update contact' : 'Failed to create contact')
    }
  }

  if (pageLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Spinner size="lg" />
      </div>
    )
  }

  return (
    <div className="mx-auto max-w-2xl">
      <h1 className="mb-6 text-2xl font-semibold text-gray-900">
        {isEditing ? 'Edit Contact' : 'New Contact'}
      </h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <Input
          label="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          error={errors.name}
          placeholder="John Doe"
        />
        <Input
          label="Email"
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          error={errors.email}
          placeholder="john@example.com"
        />
        <Input
          label="Phone"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          placeholder="+1 234 567 890"
        />
        <Input
          label="Address"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
          placeholder="123 Main St, City"
        />
        <div className="space-y-1">
          <label className="block text-sm font-medium text-gray-700">Notes</label>
          <textarea
            className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm transition-colors focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
            rows={3}
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Optional notes..."
          />
        </div>

        <div className="flex justify-end gap-3">
          <Button variant="secondary" onClick={() => navigate('/contacts')}>
            Cancel
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? 'Saving...' : isEditing ? 'Update Contact' : 'Create Contact'}
          </Button>
        </div>
      </form>
    </div>
  )
}
