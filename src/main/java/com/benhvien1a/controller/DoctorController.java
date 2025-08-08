package com.benhvien1a.controller;

import com.benhvien1a.dto.DoctorDTO;
import com.benhvien1a.response.ApiResponse;
import com.benhvien1a.model.Doctor;
import com.benhvien1a.service.DoctorService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    private final DoctorService doctorService;

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Page<Doctor>>> getAllActiveDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get all active doctors with page: {}, size: {}", page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Doctor> doctors = doctorService.getAllActiveDoctors(pageable);
            return ApiResponseUtil.buildResponse(true, "All active doctors retrieved successfully", doctors, "/api/v1/doctors/public");
        } catch (Exception e) {
            logger.error("Failed to retrieve active doctors: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to retrieve active doctors: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/public");
        }
    }

    @GetMapping("/public/{departmentSlug}")
    public ResponseEntity<ApiResponse<Page<Doctor>>> getDoctorsByDepartmentSlug(
            @PathVariable String departmentSlug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get doctors by department slug: {} with page: {}, size: {}", departmentSlug, page, size);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Doctor> doctors = doctorService.getDoctorsByDepartmentSlug(departmentSlug, pageable);
            return ApiResponseUtil.buildResponse(true, "Doctors retrieved successfully", doctors, "/api/v1/doctors/public/" + departmentSlug);
        } catch (Exception e) {
            logger.error("Failed to retrieve doctors by department slug: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to retrieve doctors: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/public/" + departmentSlug);
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Doctor>> createDoctor(@Valid @ModelAttribute DoctorDTO request) {
        logger.info("Received request to create doctor: {}", request.getFullName());
        try {
            Doctor doctor = doctorService.createDoctor(request);
            return ApiResponseUtil.buildResponse(true, "Doctor created successfully", doctor, "/api/v1/doctors");
        } catch (Exception e) {
            logger.error("Failed to create doctor: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create doctor: " + e.getMessage(), e.getMessage(), "/api/v1/doctors");
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(@PathVariable Long id, @Valid @ModelAttribute DoctorDTO request) {
        logger.info("Received request to update doctor with ID: {}", id);
        try {
            Doctor doctor = doctorService.updateDoctor(id, request);
            return ApiResponseUtil.buildResponse(true, "Doctor updated successfully", doctor, "/api/v1/doctors/" + id);
        } catch (Exception e) {
            logger.error("Failed to update doctor: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update doctor: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/" + id);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> deleteDoctor(@PathVariable Long id) {
        logger.info("Received request to delete doctor with ID: {}", id);
        try {
            doctorService.deleteDoctor(id);
            return ApiResponseUtil.buildResponse(true, "Doctor deleted successfully", null, "/api/v1/doctors/" + id);
        } catch (Exception e) {
            logger.error("Failed to delete doctor: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to delete doctor: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/" + id);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable Long id) {
        logger.info("Received request to get doctor with ID: {}", id);
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            return ApiResponseUtil.buildResponse(true, "Doctor retrieved successfully", doctor, "/api/v1/doctors/" + id);
        } catch (Exception e) {
            logger.error("Failed to retrieve doctor: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to retrieve doctor: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/" + id);
        }
    }

    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorBySlug(@PathVariable String slug) {
        logger.info("Received request to get doctor with slug: {}", slug);
        try {
            Doctor doctor = doctorService.getDoctorBySlug(slug);
            return ApiResponseUtil.buildResponse(true, "Doctor retrieved successfully", doctor, "/api/v1/doctors/by-slug/" + slug);
        } catch (Exception e) {
            logger.error("Failed to retrieve doctor: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to retrieve doctor: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/by-slug/" + slug);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Page<Doctor>>> getAllDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) Boolean isActive) {
        logger.info("Received request to get all doctors with page: {}, size: {}, fullName: {}, isActive: {}", page, size, fullName, isActive);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Doctor> doctors = fullName != null || isActive != null
                    ? doctorService.getFilteredDoctors(fullName, isActive, pageable)
                    : doctorService.getAllDoctors(pageable);
            return ApiResponseUtil.buildResponse(true, "All doctors retrieved successfully", doctors, "/api/v1/doctors");
        } catch (Exception e) {
            logger.error("Failed to retrieve all doctors: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to retrieve all doctors: " + e.getMessage(), e.getMessage(), "/api/v1/doctors");
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> hideDoctor(@PathVariable Long id) {
        logger.info("Received request to toggle active status for doctor with ID: {}", id);
        try {
            doctorService.hideDoctor(id);
            return ApiResponseUtil.buildResponse(true, "Doctor status toggled successfully", null, "/api/v1/doctors/" + id + "/hide");
        } catch (Exception e) {
            logger.error("Failed to toggle doctor status: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to toggle doctor status: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/" + id + "/hide");
        }
    }

    @GetMapping("/positions")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<String>>> getAllPositions() {
        logger.info("Received request to get all doctor positions");
        try {
            List<String> positions = doctorService.getAllPositions();
            return ApiResponseUtil.buildResponse(true, "All doctor positions retrieved successfully", positions, "/api/v1/doctors/positions");
        } catch (Exception e) {
            logger.error("Failed to retrieve doctor positions: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to retrieve doctor positions: " + e.getMessage(), e.getMessage(), "/api/v1/doctors/positions");
        }
    }
}