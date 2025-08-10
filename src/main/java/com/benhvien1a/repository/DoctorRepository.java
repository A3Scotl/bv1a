package com.benhvien1a.repository;

import com.benhvien1a.model.Doctor;
import com.benhvien1a.model.Position;
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

    @Query("""
    SELECT d FROM Doctor d
    WHERE (:fullName IS NULL OR LOWER(d.fullName) LIKE LOWER(CONCAT('%', :fullName, '%')))
    AND (:isActive IS NULL OR d.isActive = :isActive)
    AND (:departmentId IS NULL OR d.department.id = :departmentId)
    AND (:position IS NULL OR d.position = :position)
""")
    Page<Doctor> findByFullNameAndIsActiveAndPositionAndDepartmentId(
            @org.springframework.lang.Nullable String fullName,
            @org.springframework.lang.Nullable Boolean isActive,
            @org.springframework.lang.Nullable Long departmentId,
            @org.springframework.lang.Nullable Position position,
            Pageable pageable);

    @Query("SELECT d FROM Doctor d WHERE d.department.slug = :slug AND d.isActive = true")
    Page<Doctor> findByDepartmentSlug(String slug, Pageable pageable);
}