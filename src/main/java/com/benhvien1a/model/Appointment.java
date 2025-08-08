/*
 * @ (#) Appointment.java 1.0 7/12/2025
 *
 * Copyright (c) 2025 IUH.All rights reserved
 */

package com.benhvien1a.model;

/*
 * @description
 * @author : Nguyen Truong An
 * @date : 7/12/2025
 * @version 1.0
 */

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointments", indexes = {
        @Index(name = "idx_appointment_status", columnList = "status"),
        @Index(name = "idx_appointment_created_at", columnList = "createdAt")
})
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;
    private LocalDate date;
    private String timeSlot;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
