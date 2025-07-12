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
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String email;

    private LocalDate date;
    private String timeSlot;
    private String note;

    @ManyToOne
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();
}
