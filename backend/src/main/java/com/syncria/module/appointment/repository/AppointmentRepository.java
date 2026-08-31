package com.syncria.module.appointment.repository;

import com.syncria.module.appointment.entity.Appointment;
import com.syncria.module.appointment.entity.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Page<Appointment> findByCompanyIdAndDeletedFalse(Long companyId, Pageable pageable);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.companyId = :companyId
            AND a.deleted = false
            AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(a.reason) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Appointment> searchByCompanyId(@Param("companyId") Long companyId,
                                        @Param("search") String search,
                                        Pageable pageable);

    Page<Appointment> findByCompanyIdAndDeletedFalseAndStatus(Long companyId, AppointmentStatus status, Pageable pageable);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.companyId = :companyId
            AND a.deleted = false
            AND a.status = :status
            AND (LOWER(a.title) LIKE LOWER(CONCAT('%', :search, '%'))
                 OR LOWER(a.reason) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Appointment> searchByCompanyIdAndStatus(@Param("companyId") Long companyId,
                                                  @Param("status") AppointmentStatus status,
                                                  @Param("search") String search,
                                                  Pageable pageable);

    Optional<Appointment> findByIdAndCompanyIdAndDeletedFalse(Long id, Long companyId);

    List<Appointment> findByCompanyIdAndAppointmentDateAndDeletedFalse(Long companyId, LocalDate date);

    List<Appointment> findByCompanyIdAndAppointmentDateBetweenAndDeletedFalse(
            Long companyId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT a FROM Appointment a
            WHERE a.companyId = :companyId
            AND a.petId = :petId
            AND a.appointmentDate = :date
            AND a.deleted = false
            AND a.status = 'SCHEDULED'
            AND a.id <> :excludeId
            AND (
                (a.startTime <= :endTime AND a.endTime > :startTime)
                OR (a.startTime < :endTime AND a.endTime >= :startTime)
            )
            """)
    List<Appointment> findConflicts(
            @Param("companyId") Long companyId,
            @Param("petId") Long petId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime,
            @Param("excludeId") Long excludeId);

    long countByCompanyIdAndDeletedFalse(Long companyId);

    long countByCompanyIdAndAppointmentDateAndDeletedFalse(Long companyId, LocalDate date);

    long countByCompanyIdAndStatusAndDeletedFalse(Long companyId, AppointmentStatus status);
}
