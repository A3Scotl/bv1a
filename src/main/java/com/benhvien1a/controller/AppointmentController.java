package com.benhvien1a.controller;

import com.benhvien1a.dto.AppointmentDTO;
import com.benhvien1a.dto.response.ApiResponse;
import com.benhvien1a.model.Appointment;
import com.benhvien1a.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<Appointment>> createAppointment(@Valid @RequestBody AppointmentDTO appointment) {
        logger.info("Received request to create appointment: {}", appointment.getFullName());
        try {
            Appointment createdAppointment = appointmentService.createAppointment(appointment);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Appointment created successfully",
                    createdAppointment,
                    null,
                    null,
                    "/api/v1/appointments"
            ));
        } catch (Exception e) {
            logger.error("Failed to create appointment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to create appointment: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/appointments"
            ));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Appointment>> updateAppointment(@PathVariable Long id, @Valid @RequestBody AppointmentDTO appointment) {
        logger.info("Received request to update appointment with ID: {}", id);
        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(id, appointment);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Appointment updated successfully",
                    updatedAppointment,
                    null,
                    null,
                    "/api/v1/appointments/" + id
            ));
        } catch (Exception e) {
            logger.error("Failed to update appointment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to update appointment: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/appointments/" + id
            ));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> deleteAppointment(@PathVariable Long id) {
        logger.info("Received request to delete appointment with ID: {}", id);
        try {
            appointmentService.deleteAppointment(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Appointment deleted successfully",
                    null,
                    null,
                    null,
                    "/api/v1/appointments/" + id
            ));
        } catch (Exception e) {
            logger.error("Failed to delete appointment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to delete appointment: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/appointments/" + id
            ));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR' )")
    public ResponseEntity<ApiResponse<Appointment>> getAppointmentById(@PathVariable Long id) {
        logger.info("Received request to get appointment with ID: {}", id);
        try {
            Appointment appointment = appointmentService.getAppointmentById(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Appointment retrieved successfully",
                    appointment,
                    null,
                    null,
                    "/api/v1/appointments/" + id
            ));
        } catch (Exception e) {
            logger.error("Failed to retrieve appointment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to retrieve appointment: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/appointments/" + id
            ));
        }
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<List<Appointment>>> getAllAppointments() {
        logger.info("Received request to get all appointments");
        try {
            List<Appointment> appointments = appointmentService.getAllAppointments();
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "All appointments retrieved successfully",
                    appointments,
                    null,
                    null,
                    "/api/v1/appointments"
            ));
        } catch (Exception e) {
            logger.error("Failed to retrieve all appointments: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to retrieve all appointments: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/appointments"
            ));
        }
    }

    @PatchMapping("/{id}/hide")
    @PreAuthorize("hasAnyRole('ADMIN', 'EDITOR')")
    public ResponseEntity<ApiResponse<Void>> hideAppointment(@PathVariable Long id) {
        logger.info("Received request to hide appointment with ID: {}", id);
        try {
            appointmentService.hideAppointment(id);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Appointment hidden successfully",
                    null,
                    null,
                    null,
                    "/api/v1/appointments/" + id + "/hide"
            ));
        } catch (Exception e) {
            logger.error("Failed to hide appointment: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse<>(
                    false,
                    "Failed to hide appointment: " + e.getMessage(),
                    null,
                    e.getMessage(),
                    null,
                    "/api/v1/appointments/" + id + "/hide"
            ));
        }
    }
}
