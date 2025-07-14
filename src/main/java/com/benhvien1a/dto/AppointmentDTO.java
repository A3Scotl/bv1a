package com.benhvien1a.dto;

import com.benhvien1a.model.AppointmentStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AppointmentDTO {
    private String fullName;
    private String phone;
    private String email;
    private LocalDate date;
    private String timeSlot;
    private String note;
    private Long doctorId;
    private AppointmentStatus status;
    private boolean isActive;
}
