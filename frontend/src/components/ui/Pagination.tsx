import Button from './Button'

interface PaginationProps {
  page: number
  totalPages: number
  totalElements: number
  pageSize: number
  onPageChange: (page: number) => void
}

export default function Pagination({
  page,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
}: PaginationProps) {
  if (totalPages <= 1) return null

  const startItem = page * pageSize + 1
  const endItem = Math.min((page + 1) * pageSize, totalElements)

  return (
    <div className="flex items-center justify-between border-t border-gray-200 px-4 py-3">
      <p className="text-sm text-gray-700">
        Showing <span className="font-medium">{startItem}</span> to{' '}
        <span className="font-medium">{endItem}</span> of{' '}
        <span className="font-medium">{totalElements}</span> results
      </p>
      <div className="flex gap-2">
        <Button
          variant="secondary"
          onClick={() => onPageChange(page - 1)}
          disabled={page === 0}
        >
          Previous
        </Button>
        <Button
          variant="secondary"
          onClick={() => onPageChange(page + 1)}
          disabled={page >= totalPages - 1}
        >
          Next
        </Button>
      </div>
    </div>
  )
}
