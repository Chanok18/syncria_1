import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { usePets } from '../hooks/usePets'
import Input from '../../../components/ui/Input'
import Button from '../../../components/ui/Button'
import Spinner from '../../../components/ui/Spinner'
import api from '../../../lib/api'
import type { Pet } from '../types/petTypes'
import type { Contact } from '../../contacts/types/types'
import toast from 'react-hot-toast'

export default function PetFormPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { createPet, updatePet, isLoading } = usePets()
  const isEditing = Boolean(id)

  const [contactId, setContactId] = useState<number>(0)
  const [name, setName] = useState('')
  const [species, setSpecies] = useState('')
  const [breed, setBreed] = useState('')
  const [birthDate, setBirthDate] = useState('')
  const [gender, setGender] = useState('')
  const [notes, setNotes] = useState('')
  const [errors, setErrors] = useState<Record<string, string>>({})
  const [pageLoading, setPageLoading] = useState(isEditing)
  const [contacts, setContacts] = useState<Contact[]>([])

  useEffect(() => {
    api.get('/contacts', { params: { size: 100 } })
      .then((res) => setContacts(res.data.content))
      .catch(() => {})
  }, [])

  useEffect(() => {
    if (!id) return
    api.get<Pet>(`/pets/${id}`)
      .then((res) => {
        setContactId(res.data.contactId)
        setName(res.data.name)
        setSpecies(res.data.species)
        setBreed(res.data.breed ?? '')
        setBirthDate(res.data.birthDate ?? '')
        setGender(res.data.gender ?? '')
        setNotes(res.data.notes ?? '')
      })
      .catch(() => {
        toast.error('Pet not found')
        navigate('/pets')
      })
      .finally(() => setPageLoading(false))
  }, [id, navigate])

  const validate = () => {
    const newErrors: Record<string, string> = {}
    if (!contactId) newErrors.contactId = 'Owner is required'
    if (!name.trim()) newErrors.name = 'Name is required'
    if (!species.trim()) newErrors.species = 'Species is required'
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!validate()) return

    const data = {
      contactId,
      name: name.trim(),
      species: species.trim(),
      breed: breed || undefined,
      birthDate: birthDate || undefined,
      gender: gender || undefined,
      notes: notes || undefined,
    }

    try {
      if (isEditing && id) {
        await updatePet(Number(id), data)
        toast.success('Pet updated successfully')
      } else {
        await createPet(data)
        toast.success('Pet created successfully')
      }
      navigate('/pets')
    } catch {
      toast.error(isEditing ? 'Failed to update pet' : 'Failed to create pet')
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
        {isEditing ? 'Edit Pet' : 'New Pet'}
      </h1>

      <form onSubmit={handleSubmit} className="space-y-4">
        <div className="space-y-1">
          <label className="block text-sm font-medium text-gray-700">Owner</label>
          <select
            className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm transition-colors focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
            value={contactId || ''}
            onChange={(e) => setContactId(Number(e.target.value))}
          >
            <option value={0}>Select an owner...</option>
            {contacts.map((c) => (
              <option key={c.id} value={c.id}>{c.name}</option>
            ))}
          </select>
          {errors.contactId && <p className="text-sm text-red-600">{errors.contactId}</p>}
        </div>
        <Input
          label="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          error={errors.name}
          placeholder="Buddy"
        />
        <Input
          label="Species"
          value={species}
          onChange={(e) => setSpecies(e.target.value)}
          error={errors.species}
          placeholder="Dog, Cat, Bird..."
        />
        <Input
          label="Breed"
          value={breed}
          onChange={(e) => setBreed(e.target.value)}
          placeholder="Golden Retriever"
        />
        <Input
          label="Birth Date"
          type="date"
          value={birthDate}
          onChange={(e) => setBirthDate(e.target.value)}
        />
        <div className="space-y-1">
          <label className="block text-sm font-medium text-gray-700">Gender</label>
          <select
            className="block w-full rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm transition-colors focus:border-primary-500 focus:outline-none focus:ring-1 focus:ring-primary-500"
            value={gender}
            onChange={(e) => setGender(e.target.value)}
          >
            <option value="">Select gender...</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
          </select>
        </div>
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
          <Button variant="secondary" onClick={() => navigate('/pets')}>
            Cancel
          </Button>
          <Button type="submit" disabled={isLoading}>
            {isLoading ? 'Saving...' : isEditing ? 'Update Pet' : 'Create Pet'}
          </Button>
        </div>
      </form>
    </div>
  )
}
