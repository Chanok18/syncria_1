import { create } from 'zustand'
import api from '../../../lib/api'
import type { Pet, PetRequest, PaginatedResponse } from '../types/petTypes'

interface PetState {
  pets: Pet[]
  selectedPet: Pet | null
  isLoading: boolean
  error: string | null
  page: number
  totalPages: number
  totalElements: number
  search: string

  fetchPets: (page?: number, search?: string) => Promise<void>
  createPet: (data: PetRequest) => Promise<void>
  updatePet: (id: number, data: PetRequest) => Promise<void>
  deletePet: (id: number) => Promise<void>
  setSearch: (search: string) => void
  setPage: (page: number) => void
  clearError: () => void
}

export const usePetStore = create<PetState>((set, get) => ({
  pets: [],
  selectedPet: null,
  isLoading: false,
  error: null,
  page: 0,
  totalPages: 0,
  totalElements: 0,
  search: '',

  fetchPets: async (page?: number, search?: string) => {
    const currentPage = page ?? get().page
    const currentSearch = search ?? get().search
    set({ isLoading: true, error: null, page: currentPage })
    try {
      const params: Record<string, string | number> = { page: currentPage, size: 20 }
      if (currentSearch) params.search = currentSearch
      const response = await api.get<PaginatedResponse<Pet>>('/pets', { params })
      set({
        pets: response.data.content,
        totalPages: response.data.totalPages,
        totalElements: response.data.totalElements,
      })
    } catch {
      set({ error: 'Failed to load pets' })
    } finally {
      set({ isLoading: false })
    }
  },

  createPet: async (data) => {
    set({ isLoading: true, error: null })
    try {
      await api.post('/pets', data)
      await get().fetchPets(0)
    } catch {
      set({ error: 'Failed to create pet' })
      throw new Error('Failed to create pet')
    } finally {
      set({ isLoading: false })
    }
  },

  updatePet: async (id, data) => {
    set({ isLoading: true, error: null })
    try {
      await api.put(`/pets/${id}`, data)
      await get().fetchPets()
    } catch {
      set({ error: 'Failed to update pet' })
      throw new Error('Failed to update pet')
    } finally {
      set({ isLoading: false })
    }
  },

  deletePet: async (id) => {
    set({ isLoading: true, error: null })
    try {
      await api.delete(`/pets/${id}`)
      await get().fetchPets()
    } catch {
      set({ error: 'Failed to delete pet' })
      throw new Error('Failed to delete pet')
    } finally {
      set({ isLoading: false })
    }
  },

  setSearch: (search) => set({ search }),
  setPage: (page) => set({ page }),
  clearError: () => set({ error: null }),
}))
