package com.benhvien1a.repository;

import com.benhvien1a.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByEmail(String email);

    void deleteByTimestampBefore(LocalDateTime localDateTime);
}