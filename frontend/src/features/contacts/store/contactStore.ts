import { create } from 'zustand'
import api from '../../../lib/api'
import type { Contact, ContactRequest, PaginatedResponse } from '../types/types'

interface ContactState {
  contacts: Contact[]
  selectedContact: Contact | null
  isLoading: boolean
  error: string | null
  page: number
  totalPages: number
  totalElements: number
  search: string

  fetchContacts: (page?: number, search?: string) => Promise<void>
  createContact: (data: ContactRequest) => Promise<void>
  updateContact: (id: number, data: ContactRequest) => Promise<void>
  deleteContact: (id: number) => Promise<void>
  setSearch: (search: string) => void
  setPage: (page: number) => void
  clearError: () => void
}

export const useContactStore = create<ContactState>((set, get) => ({
  contacts: [],
  selectedContact: null,
  isLoading: false,
  error: null,
  page: 0,
  totalPages: 0,
  totalElements: 0,
  search: '',

  fetchContacts: async (page?: number, search?: string) => {
    const currentPage = page ?? get().page
    const currentSearch = search ?? get().search
    set({ isLoading: true, error: null, page: currentPage })
    try {
      const params: Record<string, string | number> = { page: currentPage, size: 20 }
      if (currentSearch) params.search = currentSearch
      const response = await api.get<PaginatedResponse<Contact>>('/contacts', { params })
      set({
        contacts: response.data.content,
        totalPages: response.data.totalPages,
        totalElements: response.data.totalElements,
      })
    } catch {
      set({ error: 'Failed to load contacts' })
    } finally {
      set({ isLoading: false })
    }
  },

  createContact: async (data) => {
    set({ isLoading: true, error: null })
    try {
      await api.post('/contacts', data)
      await get().fetchContacts(0)
    } catch {
      set({ error: 'Failed to create contact' })
      throw new Error('Failed to create contact')
    } finally {
      set({ isLoading: false })
    }
  },

  updateContact: async (id, data) => {
    set({ isLoading: true, error: null })
    try {
      await api.put(`/contacts/${id}`, data)
      await get().fetchContacts()
    } catch {
      set({ error: 'Failed to update contact' })
      throw new Error('Failed to update contact')
    } finally {
      set({ isLoading: false })
    }
  },

  deleteContact: async (id) => {
    set({ isLoading: true, error: null })
    try {
      await api.delete(`/contacts/${id}`)
      await get().fetchContacts()
    } catch {
      set({ error: 'Failed to delete contact' })
      throw new Error('Failed to delete contact')
    } finally {
      set({ isLoading: false })
    }
  },

  setSearch: (search) => set({ search }),
  setPage: (page) => set({ page }),
  clearError: () => set({ error: null }),
}))
