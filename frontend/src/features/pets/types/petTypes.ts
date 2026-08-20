export interface Pet {
  id: number
  companyId: number
  contactId: number
  name: string
  species: string
  breed: string | null
  birthDate: string | null
  gender: string | null
  notes: string | null
  createdAt: string
  updatedAt: string | null
}

export interface PetRequest {
  contactId: number
  name: string
  species: string
  breed?: string
  birthDate?: string
  gender?: string
  notes?: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
