import { describe, it, expect, vi, beforeEach } from 'vitest'
import { usePetStore } from './petStore'

vi.mock('../../../lib/api', () => ({
  default: {
    get: vi.fn(),
    post: vi.fn(),
    put: vi.fn(),
    delete: vi.fn(),
  },
}))

import api from '../../../lib/api'
const mockApi = vi.mocked(api)

describe('petStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    usePetStore.setState({
      pets: [],
      selectedPet: null,
      isLoading: false,
      error: null,
      page: 0,
      totalPages: 0,
      totalElements: 0,
      search: '',
    })
  })

  it('has correct initial state', () => {
    const state = usePetStore.getState()
    expect(state.pets).toEqual([])
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
    expect(state.page).toBe(0)
    expect(state.totalPages).toBe(0)
    expect(state.totalElements).toBe(0)
    expect(state.search).toBe('')
  })

  it('fetchPets loads pets successfully', async () => {
    const mockResponse = {
      data: {
        content: [
          { id: 1, name: 'Max', species: 'Perro', companyId: 1, contactId: 1 },
          { id: 2, name: 'Luna', species: 'Gato', companyId: 1, contactId: 1 },
        ],
        totalElements: 2,
        totalPages: 1,
      },
    }
    mockApi.get.mockResolvedValue(mockResponse)

    await usePetStore.getState().fetchPets(0, '')

    const state = usePetStore.getState()
    expect(state.pets).toHaveLength(2)
    expect(state.totalElements).toBe(2)
    expect(state.totalPages).toBe(1)
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
  })

  it('fetchPets handles error', async () => {
    mockApi.get.mockRejectedValue(new Error('Network error'))

    await usePetStore.getState().fetchPets(0, '')

    const state = usePetStore.getState()
    expect(state.error).toBe('Failed to load pets')
    expect(state.isLoading).toBe(false)
  })

  it('createPet creates and refetches', async () => {
    mockApi.post.mockResolvedValue({})
    mockApi.get.mockResolvedValue({
      data: { content: [{ id: 1, name: 'NewPet' }], totalElements: 1, totalPages: 1 },
    })

    await usePetStore.getState().createPet({ name: 'NewPet', species: 'Perro', contactId: 1 })

    expect(mockApi.post).toHaveBeenCalledWith('/pets', { name: 'NewPet', species: 'Perro', contactId: 1 })
    expect(mockApi.get).toHaveBeenCalled()
  })

  it('createPet handles error', async () => {
    mockApi.post.mockRejectedValue(new Error('Validation error'))

    await expect(
      usePetStore.getState().createPet({ name: 'Fail', species: 'Perro', contactId: 1 })
    ).rejects.toThrow('Failed to create pet')

    const state = usePetStore.getState()
    expect(state.error).toBe('Failed to create pet')
  })

  it('updatePet updates and refetches', async () => {
    mockApi.put.mockResolvedValue({})
    mockApi.get.mockResolvedValue({
      data: { content: [{ id: 1, name: 'UpdatedPet' }], totalElements: 1, totalPages: 1 },
    })

    await usePetStore.getState().updatePet(1, { name: 'UpdatedPet', species: 'Gato', contactId: 1 })

    expect(mockApi.put).toHaveBeenCalledWith('/pets/1', { name: 'UpdatedPet', species: 'Gato', contactId: 1 })
  })

  it('deletePet deletes and refetches', async () => {
    mockApi.delete.mockResolvedValue({})
    mockApi.get.mockResolvedValue({
      data: { content: [], totalElements: 0, totalPages: 0 },
    })

    await usePetStore.getState().deletePet(1)

    expect(mockApi.delete).toHaveBeenCalledWith('/pets/1')
  })

  it('setSearch updates search', () => {
    usePetStore.getState().setSearch('labrador')
    expect(usePetStore.getState().search).toBe('labrador')
  })

  it('setPage updates page', () => {
    usePetStore.getState().setPage(2)
    expect(usePetStore.getState().page).toBe(2)
  })

  it('clearError clears error', () => {
    usePetStore.setState({ error: 'some error' })
    usePetStore.getState().clearError()
    expect(usePetStore.getState().error).toBeNull()
  })
})
