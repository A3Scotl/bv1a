package com.benhvien1a.controller;

import com.benhvien1a.dto.DoctorDTO;
import com.benhvien1a.dto.response.ApiResponse;
import com.benhvien1a.model.Article;
import com.benhvien1a.model.Doctor;
import com.benhvien1a.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final DoctorService doctorService;

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllActiveDoctors() {
        logger.info("Nhận yêu cầu lấy tất cả bác sĩ đang hoạt động");
        try {
            List<Doctor> doctors = doctorService.getAllActiveDoctors();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy tất cả bác sĩ đang hoạt động thành công",
                    doctors,
                    null,
                    null,
                    "/api/v1/doctors/public"
            ));
        } catch (Exception e) {
            logger.error("Lấy tất cả bác sĩ đang hoạt động thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Lấy tất cả bác sĩ đang hoạt động thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors/public"
            ));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Doctor>> createDoctor(@Valid @ModelAttribute DoctorDTO request) {
        logger.info("Nhận yêu cầu tạo bác sĩ: {}", request.getFullName());
        try {
            Doctor doctor = doctorService.creaeDoctor(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Tạo bác sĩ thành công",
                    doctor,
                    null,
                    null,
                    "/api/v1/doctors"
            ));
        } catch (Exception e) {
            logger.error("Tạo bác sĩ thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Tạo bác sĩ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors"
            ));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Doctor>> updateDoctor(
            @PathVariable Long id,
            @Valid @ModelAttribute DoctorDTO request) {
        logger.info("Nhận yêu cầu cập nhật bác sĩ với ID: {}", id);
        try {
            Doctor doctor = doctorService.updateDoctor(id, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật bác sĩ thành công",
                    doctor,
                    null,
                    null,
                    "/api/v1/doctors/" + id
            ));
        } catch (Exception e) {
            logger.error("Cập nhật bác sĩ thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Cập nhật bác sĩ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors/" + id
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Doctor>> deleteDoctor(@PathVariable Long id) {
        logger.info("Nhận yêu cầu xóa bác sĩ với ID: {}", id);
        try {
            doctorService.deleteDoctor(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Xóa bác sĩ thành công",
                    null,
                    null,
                    null,
                    "/api/v1/doctors/" + id
            ));
        } catch (Exception e) {
            logger.error("Xóa bác sĩ thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Xóa bác sĩ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors/" + id
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorById(@PathVariable Long id) {
        logger.info("Nhận yêu cầu lấy bác sĩ với ID: {}", id);
        try {
            Doctor doctor = doctorService.getDoctorById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy bác sĩ thành công",
                    doctor,
                    null,
                    null,
                    "/api/v1/doctors/" + id
            ));
        } catch (Exception e) {
            logger.error("Lấy bác sĩ thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Lấy bác sĩ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors/" + id
            ));
        }
    }

    @GetMapping("/by-slug/{slug}")
    public ResponseEntity<ApiResponse<Doctor>> getDoctorBySlug(@PathVariable String slug) {
        logger.info("Nhận yêu cầu lấy bác sĩ với slug: {}", slug);
        try {
            Doctor doctor = doctorService.getDoctorBySlug(slug);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy bác sĩ thành công",
                    doctor,
                    null,
                    null,
                    "/api/v1/doctors/by-slug/" + slug
            ));
        } catch (Exception e) {
            logger.error("Lấy bác sĩ thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Lấy bác sĩ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors/by-slug/" + slug
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<Doctor>>> getAllDoctors() {
        logger.info("Nhận yêu cầu lấy tất cả bác sĩ");
        try {
            List<Doctor> doctors = doctorService.getAllDoctors();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy tất cả bác sĩ thành công",
                    doctors,
                    null,
                    null,
                    "/api/v1/doctors"
            ));
        } catch (Exception e) {
            logger.error("Lấy tất cả bác sĩ thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Lấy tất cả bác sĩ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors"
            ));
        }
    }


    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> hideDoctor(@PathVariable Long id) {
        logger.info("Nhận yêu cầu Thay đổi trạng thái bác sĩ với ID: {}", id);
        try {
            doctorService.hideDoctor(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Thay đổi trạng thái thành công",
                    null,
                    null,
                    null,
                    "/api/v1/doctors/" + id + "/hide"
            ));
        } catch (Exception e) {
            logger.error("Ẩn bác sĩ thất bại: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Ẩn bác sĩ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/doctors/" + id + "/hide"
            ));
        }
    }
}
