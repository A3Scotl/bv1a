package com.benhvien1a.repository;

import com.benhvien1a.model.Appointment;
import com.benhvien1a.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Long> {
    boolean existsBySlug(String slug);
    Optional<Appointment> findBySlug(String slug);
}
