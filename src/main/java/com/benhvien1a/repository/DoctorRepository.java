package com.benhvien1a.repository;

import com.benhvien1a.model.Doctor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    boolean existsBySlug(String slug);
    Optional<Doctor> findBySlug(String slug);

    Page<Doctor> findAll(Pageable pageable);
    Page<Doctor> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE (:fullName IS NULL OR d.fullName LIKE %:fullName%) AND (:isActive IS NULL OR d.isActive = :isActive)")
    Page<Doctor> findByFullNameAndIsActive(String fullName, Boolean isActive, Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.department.slug = :slug AND d.isActive = true")
    Page<Doctor> findByDepartmentSlug(String slug, Pageable pageable);
}