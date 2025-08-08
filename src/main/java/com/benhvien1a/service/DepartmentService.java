package com.benhvien1a.service;

import com.benhvien1a.dto.DepartmentDTO;
import com.benhvien1a.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DepartmentService {
    Page<Department> getAllDepartments(Pageable pageable);
    Page<Department> getAllActiveDepartments(Pageable pageable);
    Page<Department> getFilteredDepartments(String name, Boolean isActive, Pageable pageable);
    Department getDepartmentById(Long id);
    Department getDepartmentBySlug(String slug);
    Department createDepartment(DepartmentDTO request);
    Department updateDepartment(Long id, DepartmentDTO request);
    void deleteDepartment(Long id);
    void hideDepartment(Long id);
}