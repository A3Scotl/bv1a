package com.benhvien1a.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, columnDefinition = "VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String email;

    @Column(name = "password", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


}