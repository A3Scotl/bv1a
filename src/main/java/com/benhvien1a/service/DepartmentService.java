/*
 * @ (#) DepartmentService.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service;

import com.benhvien1a.dto.DepartmentDTO;
import com.benhvien1a.model.Department;

import java.util.List;

/*
 * @description: Service interface for managing Department entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
public interface DepartmentService {
    Department createDepartment(DepartmentDTO request);
    Department updateDepartment(Long id, DepartmentDTO request);
    void deleteDepartment(Long id);
    Department getDepartmentById(Long id);
    Department getDepartmentBySlug(String slug);
    List<Department> getAllDepartments();
    List<Department> getAllActiveDepartments();
    void hideDepartment(Long id);
}