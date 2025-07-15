/*
 * @ (#) CategoryServiceImpl.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.dto.CategoryDTO;
import com.benhvien1a.model.Category;
import com.benhvien1a.repository.ArticleRepository;
import com.benhvien1a.repository.CategoryRepository;
import com.benhvien1a.repository.DepartmentRepository;
import com.benhvien1a.repository.ServiceRepository;
import com.benhvien1a.service.CategoryService;
import com.benhvien1a.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/*
 * @description: Implementation of CategoryService for managing Category entities
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryRepository categoryRepository;
    private final ArticleRepository articleRepository;
    private final DepartmentRepository departmentRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public Category createCategory(CategoryDTO request) {
        logger.info("Tạo danh mục với tên: {}", request.getName());

        String slug = SlugUtils.generateSlug(request.getName());
        if (categoryRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Category category = Category.builder()
                .name(request.getName())
                .slug(slug)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .isActive(request.isActive())
                .build();

        return categoryRepository.save(category);
    }

    @Override
    public Category updateCategory(Long id, CategoryDTO request) {
        logger.info("Cập nhật danh mục ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy danh mục với ID: {}", id);
                    return new RuntimeException("Không tìm thấy danh mục");
                });

        String newSlug;

        // Nếu người dùng sửa slug khác với slug hiện tại → dùng slug mới
        if (request.getSlug() != null && !request.getSlug().isBlank() && !request.getSlug().equals(category.getSlug())) {
            newSlug = request.getSlug();
        }
        // Nếu slug không đổi nhưng name thay đổi → generate slug từ name
        else if (!request.getName().equals(category.getName())) {
            newSlug = SlugUtils.generateSlug(request.getName());
        }
        // Không đổi gì → giữ nguyên slug cũ
        else {
            newSlug = category.getSlug();
        }



        if (!category.getSlug().equals(newSlug) && categoryRepository.existsBySlug(newSlug)) {
            logger.warn("Slug đã tồn tại: {}", newSlug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        category.setName(request.getName());
        category.setSlug(newSlug);
        category.setUpdateAt(LocalDateTime.now());
        category.setActive(request.isActive());

        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long id) {
        logger.info("Xóa danh mục ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy danh mục với ID: {}", id);
                    return new RuntimeException("Không tìm thấy danh mục");
                });

        if (articleRepository.existsByCategoryId(id) ||
                departmentRepository.existsByCategoryId(id) ||
                serviceRepository.existsByCategoryId(id)) {
            logger.warn("Danh mục ID {} đang được sử dụng bởi bài viết, phòng ban hoặc dịch vụ", id);
            throw new RuntimeException("Không thể xóa danh mục vì đang được sử dụng");
        }

        categoryRepository.delete(category);
        logger.info("Xóa danh mục thành công: {}", id);
    }

    @Override
    public Category getCategoryById(Long id) {
        logger.info("Lấy danh mục ID: {}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy danh mục với ID: {}", id);
                    return new RuntimeException("Không tìm thấy danh mục");
                });
    }

    @Override
    public Category getCategoryBySlug(String slug) {
        logger.info("Lấy danh mục với slug: {}", slug);
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy danh mục với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy danh mục với slug: " + slug);
                });
    }

    @Override
    public List<Category> getAllCategories() {
        logger.info("Lấy tất cả danh mục");
        return categoryRepository.findAll();
    }

    @Override
    public void hideCategory(Long id) {
        logger.info("Ẩn danh mục ID: {}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy danh mục với ID: {}", id);
                    return new RuntimeException("Không tìm thấy danh mục");
                });

        if (articleRepository.existsByCategoryId(id) ||
                departmentRepository.existsByCategoryId(id) ||
                serviceRepository.existsByCategoryId(id)) {
            logger.warn("Danh mục ID {} đang được sử dụng bởi bài viết, phòng ban hoặc dịch vụ", id);
            throw new RuntimeException("Không thể ẩn danh mục vì đang được sử dụng");
        }

        category.setActive(!category.isActive());
        category.setUpdateAt(LocalDateTime.now());
        categoryRepository.save(category);
        logger.info("Ẩn danh mục thành công: {}", id);
    }
}