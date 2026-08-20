import { useContactStore } from '../store/contactStore'

export function useContacts() {
  const store = useContactStore()

  return {
    contacts: store.contacts,
    isLoading: store.isLoading,
    error: store.error,
    page: store.page,
    totalPages: store.totalPages,
    totalElements: store.totalElements,
    search: store.search,
    fetchContacts: store.fetchContacts,
    createContact: store.createContact,
    updateContact: store.updateContact,
    deleteContact: store.deleteContact,
    setSearch: store.setSearch,
    setPage: store.setPage,
    clearError: store.clearError,
  }
}
