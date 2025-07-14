package com.benhvien1a.service;


import com.benhvien1a.dto.AppointmentDTO;
import com.benhvien1a.model.Appointment;

import java.util.List;

public interface AppointmentService {
    List<Appointment> getAllAppointments();
    Appointment getAppointmentById(Long id);
    Appointment createAppointment(AppointmentDTO request);
    Appointment updateAppointment(Long id, AppointmentDTO request);
    void deleteAppointment(Long id);
    Appointment getAppointmentBySlug(String slug);
    void hideAppointment(Long id);
}
