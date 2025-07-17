package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "doctors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String avatarUrl;

    @ManyToOne
    private Department department;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String slug;

    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime updateAt;

    private boolean isActive;
    @Enumerated(EnumType.STRING)
    private Position position;

}
