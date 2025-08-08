package com.benhvien1a.service.impl;

import com.benhvien1a.dto.AppointmentDTO;
import com.benhvien1a.model.Appointment;
import com.benhvien1a.model.AppointmentStatus;
import com.benhvien1a.repository.AppointmentRepository;
import com.benhvien1a.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(AppointmentServiceImpl.class);
    private final AppointmentRepository appointmentRepository;
    private final RecaptchaService recaptchaService;

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getAllAppointments(Pageable pageable) {
        logger.info("Fetching all appointments with pagination");
        return appointmentRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Appointment> getFilteredAppointments(AppointmentStatus status, String fullName, Pageable pageable) {
        logger.info("Fetching appointments with status: {} and fullName: {}", status, fullName);
        return appointmentRepository.findByStatusAndFullName(status, fullName, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment getAppointmentById(Long id) {
        logger.info("Fetching appointment with ID: {}", id);
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with ID: " + id));
    }

    @Override
    @Transactional
    public Appointment createAppointment(AppointmentDTO request) {
        logger.info("Creating appointment for: {}", request.getFullName());
        if (!recaptchaService.verify(request.getRecaptchaToken())) {
            logger.warn("Invalid reCAPTCHA for: {}", request.getFullName());
            throw new RuntimeException("reCAPTCHA verification failed");
        }

        Appointment appointment = Appointment.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .date(request.getDate())
                .timeSlot(request.getTimeSlot())
                .note(request.getNote())
                .status(AppointmentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public Appointment updateAppointment(Long id, AppointmentDTO request) {
        logger.info("Updating appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);

        appointment.setFullName(request.getFullName());
        appointment.setEmail(request.getEmail());
        appointment.setPhone(request.getPhone());
        appointment.setDate(request.getDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setNote(request.getNote());
        appointment.setStatus(request.getStatus());
        appointment.setUpdatedAt(LocalDateTime.now());

        return appointmentRepository.save(appointment);
    }

    @Override
    @Transactional
    public void deleteAppointment(Long id) {
        logger.info("Deleting appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);
        appointmentRepository.delete(appointment);
    }

    @Override
    @Transactional
    public void hideAppointment(Long id) {
        logger.info("Hiding appointment with ID: {}", id);
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
    }
}