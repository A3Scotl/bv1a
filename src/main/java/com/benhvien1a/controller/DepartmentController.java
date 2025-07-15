/*
 * @ (#) DepartmentController.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.controller;

import com.benhvien1a.dto.response.ApiResponse;
import com.benhvien1a.dto.DepartmentDTO;
import com.benhvien1a.model.Department;
import com.benhvien1a.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.print.attribute.standard.Media;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

/*
 * @description: Controller cho việc quản lý các phòng ban
 * @author: Nguyen Truong An
 * @date: 7/12/2025
 * @version: 1.0
 */
@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    private final DepartmentService departmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@Valid @ModelAttribute DepartmentDTO request) {
        logger.info("Nhận yêu cầu tạo phòng ban: {}", request.getName());
        try {
            Department department = departmentService.createDepartment(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Tạo phòng ban thành công",
                    department,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments"
            ));
        } catch (Exception e) {
            logger.error("Tạo phòng ban thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Tạo phòng ban thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments"
            ));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> updateDepartment(@PathVariable Long id, @Valid @ModelAttribute DepartmentDTO request) {
        logger.info("Nhận yêu cầu cập nhật phòng ban ID: {}", id);
        try {
            Department department = departmentService.updateDepartment(id, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật phòng ban thành công",
                    department,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id
            ));
        } catch (Exception e) {
            logger.error("Cập nhật phòng ban thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Cập nhật phòng ban thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        logger.info("Nhận yêu cầu xóa phòng ban ID: {}", id);
        try {
            departmentService.deleteDepartment(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Xóa phòng ban thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id
            ));
        } catch (Exception e) {
            logger.error("Xóa phòng ban thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Xóa phòng ban thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> getDepartmentById(@PathVariable Long id) {
        logger.info("Nhận yêu cầu lấy phòng ban ID: {}", id);
        try {
            Department department = departmentService.getDepartmentById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy phòng ban thành công",
                    department,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id
            ));
        } catch (Exception e) {
            logger.error("Lấy phòng ban thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy phòng ban thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id
            ));
        }
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> getDepartmentBySlug(@PathVariable String slug) {
        logger.info("Nhận yêu cầu lấy phòng ban với slug: {}", slug);
        try {
            Department department = departmentService.getDepartmentBySlug(slug);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy phòng ban thành công",
                    department,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/by-slug/" + slug
            ));
        } catch (Exception e) {
            logger.error("Lấy phòng ban thất bại với slug {}: {}", slug, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Lấy phòng ban thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/by-slug/" + slug
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Department>>> getAllDepartments() {
        logger.info("Nhận yêu cầu lấy tất cả phòng ban");
        try {
            List<Department> departments = departmentService.getAllDepartments();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách phòng ban thành công",
                    departments,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments"
            ));
        } catch (Exception e) {
            logger.error("Lấy danh sách phòng ban thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    "Lấy danh sách phòng ban thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments"
            ));
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hideDepartment(@PathVariable Long id) {
        logger.info("Nhận yêu cầu Thay đổi trạng thái phong ban ID: {}", id);
        try {
            departmentService.hideDepartment(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Thay đổi trạng thái thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id + "/hide"
            ));
        } catch (Exception e) {
            logger.error("Thay đổi trạng thái phòng ban thất bại ID {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Thay đổi trạng thái phòng ban thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/" + id + "/hide"
            ));
        }
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<Department>>> getAllActiveDepartments() {
        logger.info("Nhận yêu cầu lấy danh sách phòng ban đang hoạt động");
        try {
            List<Department> departments = departmentService.getAllDepartments()
                    .stream()
                    .filter(Department::isActive)
                    .toList();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy danh sách phòng ban đang hoạt động thành công",
                    departments,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/public"
            ));
        } catch (Exception e) {
            logger.error("Lấy danh sách phòng ban đang hoạt động thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    "Lấy danh sách phòng ban đang hoạt động thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/departments/public"
            ));
        }
    }
}