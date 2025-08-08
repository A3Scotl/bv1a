package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "doctors", indexes = {
        @Index(name = "idx_doctor_slug", columnList = "slug", unique = true),
        @Index(name = "idx_doctor_is_active", columnList = "isActive"),
        @Index(name = "idx_doctor_department_id", columnList = "department_id"),
        @Index(name = "idx_doctor_fullname", columnList = "fullName")
})
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private String slug;
    @ManyToOne
    private Department department;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;

    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private Position position;
    private Boolean isActive;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}
