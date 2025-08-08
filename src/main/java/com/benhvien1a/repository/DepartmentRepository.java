package com.benhvien1a.repository;

import com.benhvien1a.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsBySlug(String slug);
    Optional<Department> findBySlug(String slug);

    Page<Department> findAll(Pageable pageable);

    Page<Department> findByIsActiveTrue(Pageable pageable);

    @Query("SELECT d FROM Department d WHERE (:name IS NULL OR d.name LIKE %:name%) AND (:isActive IS NULL OR d.isActive = :isActive)")
    Page<Department> findByNameAndIsActive(String name, Boolean isActive, Pageable pageable);
}