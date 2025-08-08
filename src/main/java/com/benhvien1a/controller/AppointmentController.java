package com.benhvien1a.controller;

import com.benhvien1a.dto.AppointmentDTO;
import com.benhvien1a.response.ApiResponse;
import com.benhvien1a.model.Appointment;
import com.benhvien1a.model.AppointmentStatus;
import com.benhvien1a.service.AppointmentService;
import com.benhvien1a.util.ApiResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Appointment>> createAppointment(@Valid @RequestBody AppointmentDTO appointment) {
        logger.info("Received request to create appointment: {}", appointment.getFullName());
        try {
            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            return ApiResponseUtil.buildResponse(true, "Appointment created successfully", createdAppointment, "/api/v1/appointments");
        } catch (Exception e) {
            logger.error("Failed to create appointment: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to create appointment: " + e.getMessage(), e.getMessage(), "/api/v1/appointments");
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDTO appointment) {
        logger.info("Received request to update appointment with ID: {}", id);
        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(id, appointment);
            return ApiResponseUtil.buildResponse(true, "Appointment updated successfully", updatedAppointment, "/api/v1/appointments/" + id);
        } catch (Exception e) {
            logger.error("Failed to update appointment: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to update appointment: " + e.getMessage(), e.getMessage(), "/api/v1/appointments/" + id);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long id) {
        logger.info("Received request to delete appointment with ID: {}", id);
        try {
            appointmentService.deleteAppointment(id);
            return ApiResponseUtil.buildResponse(true, "Appointment deleted successfully", null, "/api/v1/appointments/" + id);
        } catch (Exception e) {
            logger.error("Failed to delete appointment: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to delete appointment: " + e.getMessage(), e.getMessage(), "/api/v1/appointments/" + id);
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentById(@PathVariable Long id) {
        logger.info("Received request to get appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ApiResponseUtil.buildResponse(true, "Appointment retrieved successfully", appointment, "/api/v1/appointments/" + id);
        } catch (Exception e) {
            logger.error("Failed to retrieve appointment: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.NOT_FOUND, "Failed to retrieve appointment: " + e.getMessage(), e.getMessage(), "/api/v1/appointments/" + id);
        }
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Page<Appointment>>> getAllAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String fullName) {
        logger.info("Received request to get all appointments with page: {}, size: {}, status: {}, fullName: {}", page, size, status, fullName);
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Appointment> appointments = status != null || fullName != null
                    ? appointmentService.getFilteredAppointments(status, fullName, pageable)
                    : appointmentService.getAllAppointments(pageable);
            return ApiResponseUtil.buildResponse(true, "All appointments retrieved successfully", appointments, "/api/v1/appointments");
        } catch (Exception e) {
            logger.error("Failed to retrieve all appointments: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve all appointments: " + e.getMessage(), e.getMessage(), "/api/v1/appointments");
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> hideAppointment(@PathVariable Long id) {
        logger.info("Received request to hide appointment with ID: {}", id);
        try {
            appointmentService.hideAppointment(id);
            return ApiResponseUtil.buildResponse(true, "Appointment hidden successfully", null, "/api/v1/appointments/" + id + "/hide");
        } catch (Exception e) {
            logger.error("Failed to hide appointment: {}", e.getMessage());
            return ApiResponseUtil.buildErrorResponse(HttpStatus.BAD_REQUEST, "Failed to hide appointment: " + e.getMessage(), e.getMessage(), "/api/v1/appointments/" + id + "/hide");
        }
    }
}