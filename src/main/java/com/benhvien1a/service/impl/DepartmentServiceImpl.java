package com.benhvien1a.service.impl;

import com.benhvien1a.dto.DepartmentDTO;
import com.benhvien1a.model.Department;
import com.benhvien1a.repository.DepartmentRepository;
import com.benhvien1a.service.DepartmentService;
import com.benhvien1a.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);
    private final DepartmentRepository departmentRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    @Transactional(readOnly = true)
    public Page<Department> getAllDepartments(Pageable pageable) {
        logger.info("Fetching all departments with pagination");
        return departmentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Department> getAllActiveDepartments(Pageable pageable) {
        logger.info("Fetching all active departments with pagination");
        return departmentRepository.findByIsActiveTrue(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Department> getFilteredDepartments(String name, Boolean isActive, Pageable pageable) {
        logger.info("Fetching departments with name: {} and isActive: {}", name, isActive);
        return departmentRepository.findByNameAndIsActive(name, isActive, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentById(Long id) {
        logger.info("Fetching department with ID: {}", id);
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Department getDepartmentBySlug(String slug) {
        logger.info("Fetching department with slug: {}", slug);
        return departmentRepository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Department not found with slug: " + slug));
    }

    @Override
    @Transactional
    public Department createDepartment(DepartmentDTO request) {
        logger.info("Creating department with name: {}", request.getName());
        String slug = SlugUtils.generateUniqueSlug(request.getName(), departmentRepository::existsBySlug);

        String thumbnailUrl = request.getThumbnail() != null && !request.getThumbnail().isEmpty()
                ? cloudinaryService.uploadFile(request.getThumbnail())
                : null;

        Department department = Department.builder()
                .name(request.getName())
                .slug(slug)
                .description(request.getDescription())
                .thumbnail(thumbnailUrl)
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();

        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public Department updateDepartment(Long id, DepartmentDTO request) {
        logger.info("Updating department with ID: {}", id);
        Department department = getDepartmentById(id);

        String newSlug = department.getSlug();
        if (request.getName() != null && !request.getName().equals(department.getName())) {
            newSlug = SlugUtils.generateUniqueSlug(request.getName(), departmentRepository::existsBySlug);
        }

        String thumbnailUrl = request.getThumbnail() != null && !request.getThumbnail().isEmpty()
                ? cloudinaryService.uploadFile(request.getThumbnail())
                : department.getThumbnail();

        department.setName(request.getName());
        department.setSlug(newSlug);
        department.setDescription(request.getDescription());
        department.setThumbnail(thumbnailUrl);
        department.setIsActive(request.getIsActive() != null ? request.getIsActive() : department.getIsActive());
        department.setUpdateAt(LocalDateTime.now());

        return departmentRepository.save(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        logger.info("Deleting department with ID: {}", id);
        Department department = getDepartmentById(id);
        departmentRepository.delete(department);
    }

    @Override
    @Transactional
    public void hideDepartment(Long id) {
        logger.info("Toggling active status for department with ID: {}", id);
        Department department = getDepartmentById(id);
        department.setIsActive(!department.getIsActive());
        department.setUpdateAt(LocalDateTime.now());
        departmentRepository.save(department);
    }
}