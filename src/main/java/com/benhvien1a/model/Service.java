package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "services")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String slug;
    @ManyToOne
    private Category category;
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private boolean isActive;

    private LocalDateTime createdAt = LocalDateTime.now();
}
