package com.syncria.module.contact.repository;

import com.syncria.module.contact.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query(
        value = """
                SELECT c FROM Contact c
                WHERE c.companyId = :companyId
                AND c.deleted = false
                AND (:search IS NULL
                     OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                     OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))
                """,
        countQuery = """
                SELECT COUNT(c) FROM Contact c
                WHERE c.companyId = :companyId
                AND c.deleted = false
                AND (:search IS NULL
                     OR LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%'))
                     OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%')))
                """
    )
    Page<Contact> findByCompanyIdAndSearch(
            @Param("companyId") Long companyId,
            @Param("search") String search,
            Pageable pageable);

    Optional<Contact> findByIdAndCompanyIdAndDeletedFalse(Long id, Long companyId);

    boolean existsByCompanyIdAndEmailAndDeletedFalse(Long companyId, String email);

    boolean existsByCompanyIdAndEmailAndIdNotAndDeletedFalse(Long companyId, String email, Long id);

    long countByCompanyIdAndDeletedFalse(Long companyId);
}
