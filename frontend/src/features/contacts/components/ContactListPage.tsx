import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useContacts } from '../hooks/useContacts'
import Table, { type Column } from '../../../components/ui/Table'
import Button from '../../../components/ui/Button'
import Pagination from '../../../components/ui/Pagination'
import Input from '../../../components/ui/Input'
import ContactDeleteModal from './ContactDeleteModal'
import type { Contact } from '../types/types'
import toast from 'react-hot-toast'

export default function ContactListPage() {
  const navigate = useNavigate()
  const {
    contacts,
    isLoading,
    error,
    page,
    totalPages,
    totalElements,
    search,
    fetchContacts,
    setSearch,
    setPage,
    deleteContact,
    clearError,
  } = useContacts()

  const [searchInput, setSearchInput] = useState(search)
  const [deleteTarget, setDeleteTarget] = useState<Contact | null>(null)
  const [deleteLoading, setDeleteLoading] = useState(false)

  useEffect(() => {
    fetchContacts(page)
  }, [page, fetchContacts])

  useEffect(() => {
    if (error) {
      toast.error(error)
      clearError()
    }
  }, [error, clearError])

  const handleSearch = () => {
    setSearch(searchInput)
    setPage(0)
    fetchContacts(0, searchInput)
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') handleSearch()
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    setDeleteLoading(true)
    try {
      await deleteContact(deleteTarget.id)
      toast.success('Contact deleted successfully')
      setDeleteTarget(null)
    } catch {
      toast.error('Failed to delete contact')
    } finally {
      setDeleteLoading(false)
    }
  }

  const columns: Column<Contact>[] = [
    { key: 'name', header: 'Name' },
    { key: 'email', header: 'Email' },
    { key: 'phone', header: 'Phone', render: (c) => c.phone ?? '-' },
    {
      key: 'actions',
      header: 'Actions',
      render: (contact) => (
        <div className="flex gap-2" onClick={(e) => e.stopPropagation()}>
          <Button
            variant="secondary"
            onClick={() => navigate(`/contacts/${contact.id}/edit`)}
          >
            Edit
          </Button>
          <Button
            variant="danger"
            onClick={() => setDeleteTarget(contact)}
          >
            Delete
          </Button>
        </div>
      ),
    },
  ]

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold text-gray-900">Contacts</h1>
        <Link to="/contacts/new">
          <Button>New Contact</Button>
        </Link>
      </div>

      <div className="flex gap-3">
        <div className="flex-1">
          <Input
            placeholder="Search by name or email..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            onKeyDown={handleKeyDown}
          />
        </div>
        <Button variant="secondary" onClick={handleSearch}>
          Search
        </Button>
      </div>

      <Table
        columns={columns}
        data={contacts}
        keyExtractor={(c) => c.id}
        loading={isLoading}
        emptyMessage="No contacts found"
      />

      <Pagination
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={20}
        onPageChange={setPage}
      />

      <ContactDeleteModal
        contact={deleteTarget}
        loading={deleteLoading}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
