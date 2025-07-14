/*
 * @ (#) CategoryController.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */
package com.benhvien1a.controller;

import com.benhvien1a.dto.response.ApiResponse;
import com.benhvien1a.dto.CategoryDTO;
import com.benhvien1a.model.Category;
import com.benhvien1a.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/*
 * @description: Controller cho việc quản lý các danh mục
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> createCategory(@Valid @RequestBody CategoryDTO request) {
        logger.info("Nhận yêu cầu tạo danh mục: {}", request.getName());
        try {
            Category category = categoryService.createCategory(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Tạo danh mục thành công",
                    category,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories"
            ));
        } catch (Exception e) {
            logger.error("Tạo danh mục thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Tạo danh mục thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories"
            ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO request) {
        logger.info("Nhận yêu cầu cập nhật danh mục ID: {}", id);
        try {
            Category category = categoryService.updateCategory(id, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật danh mục thành công",
                    category,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id
            ));
        } catch (Exception e) {
            logger.error("Cập nhật danh mục thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Cập nhật danh mục thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        logger.info("Nhận yêu cầu xóa danh mục ID: {}", id);
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Xóa danh mục thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id
            ));
        } catch (Exception e) {
            logger.error("Xóa danh mục thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Xóa danh mục thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        logger.info("Nhận yêu cầu lấy danh mục ID: {}", id);
        try {
            Category category = categoryService.getCategoryById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh mục thành công",
                    category,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id
            ));
        } catch (Exception e) {
            logger.error("Lấy danh mục thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy danh mục thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id
            ));
        }
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Category>> getCategoryBySlug(@PathVariable String slug) {
        logger.info("Nhận yêu cầu lấy danh mục với slug: {}", slug);
        try {
            Category category = categoryService.getCategoryBySlug(slug);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh mục thành công",
                    category,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/by-slug/" + slug
            ));
        } catch (Exception e) {
            logger.error("Lấy danh mục thất bại với slug {}: {}", slug, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy danh mục thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/by-slug/" + slug
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        logger.info("Nhận yêu cầu lấy tất cả danh mục");
        try {
            List<Category> categories = categoryService.getAllCategories();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách danh mục thành công",
                    categories,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories"
            ));
        } catch (Exception e) {
            logger.error("Lấy danh sách danh mục thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    "Lấy danh sách danh mục thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories"
            ));
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hideCategory(@PathVariable Long id) {
        logger.info("Nhận yêu cầu ẩn danh mục ID: {}", id);
        try {
            categoryService.hideCategory(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Ẩn danh mục thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id + "/hide"
            ));
        } catch (Exception e) {
            logger.error("Ẩn danh mục thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Ẩn danh mục thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/categories/" + id + "/hide"
            ));
        }
    }
}