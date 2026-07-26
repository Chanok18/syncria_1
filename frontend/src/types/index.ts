export interface User {
  id: number
  companyId: number
  email: string
  fullName: string
  role: string
  createdAt: string
}

export interface Client {
  id: number
  companyId: number
  name: string
  email: string
  phone: string
  address?: string
  createdAt: string
  updatedAt: string
}

export interface Pet {
  id: number
  companyId: number
  clientId: number
  name: string
  species: string
  breed: string
  birthDate?: string
  gender: string
  createdAt: string
}

export interface Appointment {
  id: number
  companyId: number
  petId: number
  date: string
  time: string
  reason: string
  status: 'SCHEDULED' | 'COMPLETED' | 'CANCELLED'
  createdAt: string
}

export interface PaginatedResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}
