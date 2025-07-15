/*
 * @ (#) DepartmentServiceImpl.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.dto.DepartmentDTO;
import com.benhvien1a.model.Category;
import com.benhvien1a.model.Department;
import com.benhvien1a.repository.CategoryRepository;
import com.benhvien1a.repository.DepartmentRepository;
import com.benhvien1a.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
 * @description: Implementation of DepartmentService for managing Department entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    @Override
    public Department createDepartment(DepartmentDTO request) {
        logger.info("Tạo phòng ban với tên: {}", request.getName());

        String slug = generateSlug(request.getName());
        if (departmentRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy danh mục với ID: {}", request.getCategoryId());
                        return new RuntimeException("Không tìm thấy danh mục");
                    });
        }

        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }

        Department department = Department.builder()
                .name(request.getName())
                .slug(slug)
                .category(category)
                .description(request.getDescription())
                .thumbnail(thumbnailUrl)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .isActive(request.isActive())
                .build();

        return departmentRepository.save(department);
    }

    @Override
    public Department updateDepartment(Long id, DepartmentDTO request) {
        logger.info("Cập nhật phòng ban ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy phòng ban với ID: {}", id);
                    return new RuntimeException("Không tìm thấy phòng ban");
                });

        String newSlug = generateSlug(request.getName());
        if (!department.getSlug().equals(newSlug) && departmentRepository.existsBySlug(newSlug)) {
            logger.warn("Slug đã tồn tại: {}", newSlug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy danh mục với ID: {}", request.getCategoryId());
                        return new RuntimeException("Không tìm thấy danh mục");
                    });
        }

        String thumbnailUrl = null;
        if (request.getThumbnail() != null && !request.getThumbnail().isEmpty()) {
            thumbnailUrl = cloudinaryService.uploadFile(request.getThumbnail());
        }


        department.setName(request.getName());
        department.setSlug(newSlug);
        department.setCategory(category);
        department.setDescription(request.getDescription());
        department.setThumbnail(thumbnailUrl);
        department.setUpdateAt(LocalDateTime.now());
        department.setActive(request.isActive());

        return departmentRepository.save(department);
    }

    @Override
    public void deleteDepartment(Long id) {
        logger.info("Xóa phòng ban ID: {}", id);

        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy phòng ban với ID: {}", id);
                    return new RuntimeException("Không tìm thấy phòng ban");
                });

        departmentRepository.delete(department);
        logger.info("Xóa phòng ban thành công: {}", id);
    }

    @Override
    public Department getDepartmentById(Long id) {
        logger.info("Lấy phòng ban ID: {}", id);
        return departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy phòng ban với ID: {}", id);
                    return new RuntimeException("Không tìm thấy phòng ban");
                });
    }

    @Override
    public Department getDepartmentBySlug(String slug) {
        logger.info("Lấy phòng ban với slug: {}", slug);
        return departmentRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy phòng ban với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy phòng ban với slug: " + slug);
                });
    }

    @Override
    public List<Department> getAllDepartments() {
        logger.info("Lấy tất cả phòng ban");
        return departmentRepository.findAll();
    }

    @Override
    public List<Department> getAllActiveDepartments() {
        logger.info("Lấy tất cả phòng ban đang hoạt động");
        return departmentRepository.findAll().stream()
                .filter(Department::isActive)
                .toList();
    }

    @Override
    public void hideDepartment(Long id) {
        logger.info("Ẩn phòng ban ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy phòng ban với ID: {}", id);
                    return new RuntimeException("Không tìm thấy phòng ban");
                });

        department.setActive(!department.isActive());
        department.setUpdateAt(LocalDateTime.now());
        departmentRepository.save(department);
        logger.info("Ẩn phòng ban thành công: {}", id);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}