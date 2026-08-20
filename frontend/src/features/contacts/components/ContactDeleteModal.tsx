import ConfirmDialog from '../../../components/ui/ConfirmDialog'
import type { Contact } from '../types/types'

interface ContactDeleteModalProps {
  contact: Contact | null
  loading: boolean
  onConfirm: () => void
  onCancel: () => void
}

export default function ContactDeleteModal({
  contact,
  loading,
  onConfirm,
  onCancel,
}: ContactDeleteModalProps) {
  if (!contact) return null

  return (
    <ConfirmDialog
      open={true}
      title="Delete Contact"
      message={`Are you sure you want to delete "${contact.name}"? This action cannot be undone.`}
      confirmLabel="Delete"
      variant="danger"
      loading={loading}
      onConfirm={onConfirm}
      onCancel={onCancel}
    />
  )
}
