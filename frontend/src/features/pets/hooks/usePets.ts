import { usePetStore } from '../store/petStore'

export function usePets() {
  const store = usePetStore()

  return {
    pets: store.pets,
    isLoading: store.isLoading,
    error: store.error,
    page: store.page,
    totalPages: store.totalPages,
    totalElements: store.totalElements,
    search: store.search,
    fetchPets: store.fetchPets,
    createPet: store.createPet,
    updatePet: store.updatePet,
    deletePet: store.deletePet,
    setSearch: store.setSearch,
    setPage: store.setPage,
    clearError: store.clearError,
  }
}
