package com.syncria.module.pet.repository;

import com.syncria.module.pet.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    Page<Pet> findByCompanyIdAndDeletedFalse(Long companyId, Pageable pageable);

    @Query("""
            SELECT p FROM Pet p
            WHERE p.companyId = :companyId
            AND p.deleted = false
            AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(p.species) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(p.breed) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Pet> searchByCompanyId(@Param("companyId") Long companyId,
                                @Param("search") String search,
                                Pageable pageable);

    Optional<Pet> findByIdAndCompanyIdAndDeletedFalse(Long id, Long companyId);

    List<Pet> findByContactIdAndCompanyIdAndDeletedFalse(Long contactId, Long companyId);

    List<Pet> findByCompanyIdAndDeletedFalse(Long companyId);

    long countByCompanyIdAndDeletedFalse(Long companyId);
}
