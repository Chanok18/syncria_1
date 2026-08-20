import ConfirmDialog from '../../../components/ui/ConfirmDialog'
import type { Pet } from '../types/petTypes'

interface PetDeleteModalProps {
  pet: Pet | null
  loading: boolean
  onConfirm: () => void
  onCancel: () => void
}

export default function PetDeleteModal({
  pet,
  loading,
  onConfirm,
  onCancel,
}: PetDeleteModalProps) {
  if (!pet) return null

  return (
    <ConfirmDialog
      open={true}
      title="Delete Pet"
      message={`Are you sure you want to delete "${pet.name}"? This action cannot be undone.`}
      confirmLabel="Delete"
      variant="danger"
      loading={loading}
      onConfirm={onConfirm}
      onCancel={onCancel}
    />
  )
}
