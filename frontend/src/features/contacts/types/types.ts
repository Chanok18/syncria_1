export interface Contact {
  id: number
  companyId: number
  name: string
  email: string
  phone: string | null
  address: string | null
  notes: string | null
  createdAt: string
  updatedAt: string | null
}

export interface ContactRequest {
  name: string
  email: string
  phone?: string
  address?: string
  notes?: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
