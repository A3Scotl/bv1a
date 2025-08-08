package com.benhvien1a.repository;

import com.benhvien1a.model.Appointment;
import com.benhvien1a.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findAll(Pageable pageable);

    @Query("SELECT a FROM Appointment a WHERE (:status IS NULL OR a.status = :status) AND (:fullName IS NULL OR a.fullName LIKE %:fullName%)")
    Page<Appointment> findByStatusAndFullName(AppointmentStatus status, String fullName, Pageable pageable);
}