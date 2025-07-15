package com.benhvien1a.service.impl;


import com.benhvien1a.dto.AppointmentDTO;
import com.benhvien1a.model.Appointment;
import com.benhvien1a.model.AppointmentStatus;
import com.benhvien1a.model.Doctor;
import com.benhvien1a.repository.AppointmentRepository;
import com.benhvien1a.repository.DoctorRepository;
import com.benhvien1a.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;

    @Override
    public List<Appointment> getAllAppointments() {
        logger.info("Lấy tất cả cuộc hẹn");
        return appointmentRepository.findAll();
    }

    @Override
    public Appointment getAppointmentById(Long id) {
        logger.info("Lấy cuộc hẹn với ID: {}", id);
        return appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy cuộc hẹn với ID: {}", id);
                    return new RuntimeException("Không tìm thấy cuộc hẹn");
                });
    }

    @Override
    public Appointment createAppointment(AppointmentDTO request) {
        logger.info("Tạo cuộc hẹn với tên: {}", request.getFullName());

        String slug = generateSlug(request.getFullName());
        if (appointmentRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        Doctor doctor = new Doctor();
        if (request.getDoctorId() != null) {
            doctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> {
                        logger.error("Không tìm thấy bác sĩ với ID: {}", request.getDoctorId());
                        return new RuntimeException("Không tìm thấy bác sĩ");
                    });
        }

        Appointment appointment = Appointment.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .date(request.getDate())
                .timeSlot(request.getTimeSlot())
                .note(request.getNote())
                .slug(slug)
                .doctor(doctor)
                .status(AppointmentStatus.PENDING)
                .isActive(request.isActive())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);
        logger.info("Đã tạo cuộc hẹn với ID: {}", savedAppointment.getId());

        return savedAppointment;
    }

    @Override
    public Appointment updateAppointment(Long id, AppointmentDTO request) {
        logger.info("Cập nhật cuộc hẹn với ID: {}", id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy cuộc hẹn với ID: {}", id);
                    return new RuntimeException("Không tìm thấy cuộc hẹn");
                });

        String slug = generateSlug(request.getFullName());
        if (!slug.equals(appointment.getSlug()) && appointmentRepository.existsBySlug(slug)) {
            logger.warn("Slug đã tồn tại: {}", slug);
            throw new RuntimeException("Slug đã tồn tại");
        }

        appointment.setFullName(request.getFullName());
        appointment.setEmail(request.getEmail());
        appointment.setPhone(request.getPhone());
        appointment.setDate(request.getDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setNote(request.getNote());
        appointment.setSlug(slug);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setStatus(request.getStatus());
        appointment.setActive(request.isActive());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        logger.info("Đã cập nhật cuộc hẹn với ID: {}", updatedAppointment.getId());

        return updatedAppointment;
    }

    @Override
    public void deleteAppointment(Long id) {
        logger.info("Xóa cuộc hẹn với ID: {}", id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy cuộc hẹn với ID: {}", id);
                    return new RuntimeException("Không tìm thấy cuộc hẹn");
                });

        appointmentRepository.delete(appointment);
        logger.info("Đã xóa cuộc hẹn với ID: {}", id);
    }

    @Override
    public Appointment getAppointmentBySlug(String slug) {
        logger.info("Lấy cuộc hẹn với slug: {}", slug);

        return appointmentRepository.findBySlug(slug)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy cuộc hẹn với slug: {}", slug);
                    return new RuntimeException("Không tìm thấy cuộc hẹn");
                });
    }

    @Override
    public void hideAppointment(Long id) {
        logger.info("Ẩn cuộc hẹn với ID: {}", id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy cuộc hẹn với ID: {}", id);
                    return new RuntimeException("Không tìm thấy cuộc hẹn");
                });

        appointment.setActive(!appointment.isActive());
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        logger.info("Đã ẩn cuộc hẹn với ID: {}", id);
    }

    private String generateSlug(String name) {
        return name.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
    }
}
