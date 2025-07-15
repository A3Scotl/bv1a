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
    private final RecaptchaService recaptchaService;

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

        // ✅ Kiểm tra reCAPTCHA
        boolean isCaptchaValid = recaptchaService.verify(request.getRecaptchaToken());
        if (!isCaptchaValid) {
            logger.warn("reCAPTCHA không hợp lệ cho: {}", request.getFullName());
            throw new RuntimeException("Xác minh reCAPTCHA thất bại. Vui lòng thử lại.");
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


        appointment.setFullName(request.getFullName());
        appointment.setEmail(request.getEmail());
        appointment.setPhone(request.getPhone());
        appointment.setDate(request.getDate());
        appointment.setTimeSlot(request.getTimeSlot());
        appointment.setNote(request.getNote());

        appointment.setUpdatedAt(LocalDateTime.now());
        appointment.setStatus(request.getStatus());


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
    public void hideAppointment(Long id) {
        logger.info("Ẩn cuộc hẹn với ID: {}", id);

        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Không tìm thấy cuộc hẹn với ID: {}", id);
                    return new RuntimeException("Không tìm thấy cuộc hẹn");
                });


        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);
        logger.info("Đã ẩn cuộc hẹn với ID: {}", id);
    }


}
