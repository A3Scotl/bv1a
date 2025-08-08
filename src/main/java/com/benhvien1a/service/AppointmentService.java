package com.benhvien1a.service;

import com.benhvien1a.dto.AppointmentDTO;
import com.benhvien1a.model.Appointment;
import com.benhvien1a.model.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AppointmentService {
    Page<Appointment> getAllAppointments(Pageable pageable);
    Page<Appointment> getFilteredAppointments(AppointmentStatus status, String fullName, Pageable pageable);
    Appointment getAppointmentById(Long id);
    Appointment createAppointment(AppointmentDTO request);
    Appointment updateAppointment(Long id, AppointmentDTO request);
    void deleteAppointment(Long id);
    void hideAppointment(Long id);
}