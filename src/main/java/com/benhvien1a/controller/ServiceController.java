package com.benhvien1a.controller;

import com.benhvien1a.dto.ServiceDTO;
import com.benhvien1a.dto.response.ApiResponse;
import com.benhvien1a.model.Service;
import com.benhvien1a.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
public class ServiceController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final ServiceService serviceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Service>> createService(@Valid @ModelAttribute ServiceDTO request) {
        logger.info("Nhận yêu cầu tạo dịch vụ: {}", request.getName());
        try {
            Service service = serviceService.createService(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Tạo dịch vụ thành công",
                    service,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services"
            ));
        } catch (Exception e) {
            logger.error("Tạo dịch vụ thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Tạo dịch vụ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services"
            ));
        }
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Service>> updateService(@PathVariable Long id, @Valid @ModelAttribute ServiceDTO request) {
        logger.info("Nhận yêu cầu cập nhật dịch vụ với ID: {}", id);
        try {
            Service service = serviceService.updateService(id, request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Cập nhật dịch vụ thành công",
                    service,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id
            ));
        } catch (Exception e) {
            logger.error("Cập nhật dịch vụ thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Cập nhật dịch vụ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> deleteService(@PathVariable Long id) {
        logger.info("Nhận yêu cầu xóa dịch vụ với ID: {}", id);
        try {
            serviceService.deleteService(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Xóa dịch vụ thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id
            ));
        } catch (Exception e) {
            logger.error("Xóa dịch vụ thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Xóa dịch vụ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id
            ));
        }
    }

    @GetMapping("/by-slug/{slug}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Service>> getServiceBySlug(@PathVariable String slug) {
        logger.info("Nhận yêu cầu lấy dịch vụ với slug: {}", slug);
        try {
            Service service = serviceService.getServiceBySlug(slug);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy dịch vụ thành công",
                    service,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + slug
            ));
        } catch (Exception e) {
            logger.error("Lấy dịch vụ thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Không tìm thấy dịch vụ: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + slug
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Service>> getServiceById(@PathVariable Long id) {
        logger.info("Nhận yêu cầu lấy dịch vụ với ID: {}", id);
        try {
            Service service = serviceService.getServiceById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy dịch vụ thành công",
                    service,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id
            ));
        } catch (Exception e) {
            logger.error("Lấy dịch vụ thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(
                    false,
                    "Không tìm thấy dịch vụ: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id
            ));
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<Service>>> getAllService() {
        logger.info("Nhận yêu cầu lấy tất cả dịch vụ");
        try {
            List<Service> services = serviceService.getAllActiveServices();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Lấy tất cả dịch vụ thành công",
                    services,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services"
            ));
        } catch (Exception e) {
            logger.error("Lấy tất cả dịch vụ thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(
                    false,
                    "Lấy tất cả dịch vụ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services"
            ));
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> hideService(@PathVariable Long id) {
        logger.info("Nhận yêu cầu ẩn dịch vụ với ID: {}", id);
        try {
            serviceService.hideService(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Ẩn dịch vụ thành công",
                    null,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id + "/hide"
            ));
        } catch (Exception e) {
            logger.error("Ẩn dịch vụ thất bại: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    "Ẩn dịch vụ thất bại: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/services/" + id + "/hide"
            ));
        }
    }

}
