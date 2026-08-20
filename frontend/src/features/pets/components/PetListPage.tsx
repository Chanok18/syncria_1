import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { usePets } from '../hooks/usePets'
import Table, { type Column } from '../../../components/ui/Table'
import Button from '../../../components/ui/Button'
import Pagination from '../../../components/ui/Pagination'
import Input from '../../../components/ui/Input'
import PetDeleteModal from './PetDeleteModal'
import type { Pet } from '../types/petTypes'
import toast from 'react-hot-toast'

export default function PetListPage() {
  const navigate = useNavigate()
  const {
    pets,
    isLoading,
    error,
    page,
    totalPages,
    totalElements,
    search,
    fetchPets,
    setSearch,
    setPage,
    deletePet,
    clearError,
  } = usePets()

  const [searchInput, setSearchInput] = useState(search)
  const [deleteTarget, setDeleteTarget] = useState<Pet | null>(null)
  const [deleteLoading, setDeleteLoading] = useState(false)

  useEffect(() => {
    fetchPets(page)
  }, [page, fetchPets])

  useEffect(() => {
    if (error) {
      toast.error(error)
      clearError()
    }
  }, [error, clearError])

  const handleSearch = () => {
    setSearch(searchInput)
    setPage(0)
    fetchPets(0, searchInput)
  }

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') handleSearch()
  }

  const handleDelete = async () => {
    if (!deleteTarget) return
    setDeleteLoading(true)
    try {
      await deletePet(deleteTarget.id)
      toast.success('Pet deleted successfully')
      setDeleteTarget(null)
    } catch {
      toast.error('Failed to delete pet')
    } finally {
      setDeleteLoading(false)
    }
  }

  const columns: Column<Pet>[] = [
    { key: 'name', header: 'Name' },
    { key: 'species', header: 'Species' },
    { key: 'breed', header: 'Breed', render: (p) => p.breed ?? '-' },
    { key: 'gender', header: 'Gender', render: (p) => p.gender ?? '-' },
    {
      key: 'actions',
      header: 'Actions',
      render: (pet) => (
        <div className="flex gap-2" onClick={(e) => e.stopPropagation()}>
          <Button
            variant="secondary"
            onClick={() => navigate(`/pets/${pet.id}/edit`)}
          >
            Edit
          </Button>
          <Button
            variant="danger"
            onClick={() => setDeleteTarget(pet)}
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
        <h1 className="text-2xl font-semibold text-gray-900">Pets</h1>
        <Link to="/pets/new">
          <Button>New Pet</Button>
        </Link>
      </div>

      <div className="flex gap-3">
        <div className="flex-1">
          <Input
            placeholder="Search by name, species or breed..."
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
        data={pets}
        keyExtractor={(p) => p.id}
        loading={isLoading}
        emptyMessage="No pets found"
      />

      <Pagination
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={20}
        onPageChange={setPage}
      />

      <PetDeleteModal
        pet={deleteTarget}
        loading={deleteLoading}
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  )
}
