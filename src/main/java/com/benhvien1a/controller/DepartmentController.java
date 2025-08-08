package com.benhvien1a.controller;

import com.benhvien1a.dto.DepartmentDTO;
import com.benhvien1a.response.ApiResponse;
import com.benhvien1a.model.Department;
import com.benhvien1a.service.DepartmentService;
import com.benhvien1a.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);
    private final DepartmentService departmentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> createDepartment(@Valid @ModelAttribute DepartmentDTO request) {
        logger.info("Received request to create department: {}", request.getName());
        try {
            Department department = departmentService.createDepartment(request);
            return ApiResponseUtil.buildResponse(true, "Department created successfully", department, "/api/v1/departments");
        } catch (Exception e) {
            logger.error("Failed to create department: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create department: " + e.getMessage(), e.getMessage(), "/api/v1/departments");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> updateDepartment(@PathVariable Long id, @Valid @ModelAttribute DepartmentDTO request) {
        logger.info("Received request to update department with ID: {}", id);
        try {
            Department department = departmentService.updateDepartment(id, request);
            return ApiResponseUtil.buildResponse(true, "Department updated successfully", department, "/api/v1/departments/" + id);
        } catch (Exception e) {
            logger.error("Failed to update department: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update department: " + e.getMessage(), e.getMessage(), "/api/v1/departments/" + id);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        logger.info("Received request to delete department with ID: {}", id);
        try {
            departmentService.deleteDepartment(id);
            return ApiResponseUtil.buildResponse(true, "Department deleted successfully", null, "/api/v1/departments/" + id);
        } catch (Exception e) {
            logger.error("Failed to delete department: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to delete department: " + e.getMessage(), e.getMessage(), "/api/v1/departments/" + id);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Department>> getDepartmentById(@PathVariable Long id) {
        logger.info("Received request to get department with ID: {}", id);
        try {
            Department department = departmentService.getDepartmentById(id);
            return ApiResponseUtil.buildResponse(true, "Department retrieved successfully", department, "/api/v1/departments/" + id);
        } catch (Exception e) {
            logger.error("Failed to retrieve department: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.NOT_FOUND, "Failed to retrieve department: " + e.getMessage(), e.getMessage(), "/api/v1/departments/" + id);
        }
    }

    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<ApiResponse<Department>> getDepartmentBySlug(@PathVariable String slug) {
        logger.info("Received request to get department with slug: {}", slug);
        try {
            Department department = departmentService.getDepartmentBySlug(slug);
            return ApiResponseUtil.buildResponse(true, "Department retrieved successfully", department, "/api/v1/departments/by-slug/" + slug);
        } catch (Exception e) {
            logger.error("Failed to retrieve department with slug {}: {}", slug, e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.NOT_FOUND, "Failed to retrieve department: " + e.getMessage(), e.getMessage(), "/api/v1/departments/by-slug/" + slug);
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Page<Department>>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isActive) {
        logger.info("Received request to get all departments with page: {}, size: {}, name: {}, isActive: {}", page, size, name, isActive);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Department> departments = name != null || isActive != null
                    ? departmentService.getFilteredDepartments(name, isActive, pageable)
                    : departmentService.getAllDepartments(pageable);
            return ApiResponseUtil.buildResponse(true, "All departments retrieved successfully", departments, "/api/v1/departments");
        } catch (Exception e) {
            logger.error("Failed to retrieve all departments: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve all departments: " + e.getMessage(), e.getMessage(), "/api/v1/departments");
        }
    }

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<Department>>> getAllActiveDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get all active departments with page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Department> departments = departmentService.getAllActiveDepartments(pageable);
            return ApiResponseUtil.buildResponse(true, "All active departments retrieved successfully", departments, "/api/v1/departments/public");
        } catch (Exception e) {
            logger.error("Failed to retrieve all active departments: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve all active departments: " + e.getMessage(), e.getMessage(), "/api/v1/departments/public");
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> hideDepartment(@PathVariable Long id) {
        logger.info("Received request to toggle active status for department with ID: {}", id);
        try {
            departmentService.hideDepartment(id);
            return ApiResponseUtil.buildResponse(true, "Department status toggled successfully", null, "/api/v1/departments/" + id + "/hide");
        } catch (Exception e) {
            logger.error("Failed to toggle department status: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to toggle department status: " + e.getMessage(), e.getMessage(), "/api/v1/departments/" + id + "/hide");
        }
    }
}