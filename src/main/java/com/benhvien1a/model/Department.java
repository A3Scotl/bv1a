package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departments", indexes = {
        @Index(name = "idx_department_slug", columnList = "slug", unique = true),
        @Index(name = "idx_department_is_active", columnList = "isActive"),
        @Index(name = "idx_department_name", columnList = "name")
})
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String slug;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String description;
    private String thumbnail;
    private Boolean isActive;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;

}
