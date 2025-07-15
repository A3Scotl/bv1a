package com.benhvien1a.repository;

import com.benhvien1a.model.Article;
import com.benhvien1a.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor,Long> {
    boolean existsBySlug(String slug);
    Optional<Doctor> findBySlug(String slug);
}
