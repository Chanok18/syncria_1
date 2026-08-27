import { describe, it, expect, vi, beforeEach } from 'vitest'
import { useContactStore } from './contactStore'

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

describe('contactStore', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    useContactStore.setState({
      contacts: [],
      selectedContact: null,
      isLoading: false,
      error: null,
      page: 0,
      totalPages: 0,
      totalElements: 0,
      search: '',
    })
  })

  it('has correct initial state', () => {
    const state = useContactStore.getState()
    expect(state.contacts).toEqual([])
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
    expect(state.page).toBe(0)
    expect(state.totalPages).toBe(0)
    expect(state.totalElements).toBe(0)
    expect(state.search).toBe('')
  })

  it('fetchContacts loads contacts successfully', async () => {
    const mockResponse = {
      data: {
        content: [
          { id: 1, name: 'Juan', email: 'juan@test.com', companyId: 1 },
          { id: 2, name: 'Maria', email: 'maria@test.com', companyId: 1 },
        ],
        totalElements: 2,
        totalPages: 1,
      },
    }
    mockApi.get.mockResolvedValue(mockResponse)

    await useContactStore.getState().fetchContacts(0, '')

    const state = useContactStore.getState()
    expect(state.contacts).toHaveLength(2)
    expect(state.totalElements).toBe(2)
    expect(state.totalPages).toBe(1)
    expect(state.isLoading).toBe(false)
    expect(state.error).toBeNull()
  })

  it('fetchContacts handles error', async () => {
    mockApi.get.mockRejectedValue(new Error('Network error'))

    await useContactStore.getState().fetchContacts(0, '')

    const state = useContactStore.getState()
    expect(state.error).toBe('Failed to load contacts')
    expect(state.isLoading).toBe(false)
  })

  it('createContact creates and refetches', async () => {
    mockApi.post.mockResolvedValue({})
    mockApi.get.mockResolvedValue({
      data: { content: [{ id: 1, name: 'New' }], totalElements: 1, totalPages: 1 },
    })

    await useContactStore.getState().createContact({ name: 'New', email: 'new@test.com' })

    expect(mockApi.post).toHaveBeenCalledWith('/contacts', { name: 'New', email: 'new@test.com' })
    expect(mockApi.get).toHaveBeenCalled()
  })

  it('createContact handles error', async () => {
    mockApi.post.mockRejectedValue(new Error('Duplicate'))

    await expect(
      useContactStore.getState().createContact({ name: 'Dup', email: 'dup@test.com' })
    ).rejects.toThrow('Failed to create contact')

    const state = useContactStore.getState()
    expect(state.error).toBe('Failed to create contact')
  })

  it('updateContact updates and refetches', async () => {
    mockApi.put.mockResolvedValue({})
    mockApi.get.mockResolvedValue({
      data: { content: [{ id: 1, name: 'Updated' }], totalElements: 1, totalPages: 1 },
    })

    await useContactStore.getState().updateContact(1, { name: 'Updated', email: 'upd@test.com' })

    expect(mockApi.put).toHaveBeenCalledWith('/contacts/1', { name: 'Updated', email: 'upd@test.com' })
  })

  it('deleteContact deletes and refetches', async () => {
    mockApi.delete.mockResolvedValue({})
    mockApi.get.mockResolvedValue({
      data: { content: [], totalElements: 0, totalPages: 0 },
    })

    await useContactStore.getState().deleteContact(1)

    expect(mockApi.delete).toHaveBeenCalledWith('/contacts/1')
  })

  it('setSearch updates search', () => {
    useContactStore.getState().setSearch('test')
    expect(useContactStore.getState().search).toBe('test')
  })

  it('setPage updates page', () => {
    useContactStore.getState().setPage(3)
    expect(useContactStore.getState().page).toBe(3)
  })

  it('clearError clears error', () => {
    useContactStore.setState({ error: 'some error' })
    useContactStore.getState().clearError()
    expect(useContactStore.getState().error).toBeNull()
  })
})
