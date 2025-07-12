/*
 * @ (#) EmailService.java 1.0 7/11/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service;

public interface EmailService {
    void sendResetPasswordEmail(String to, String resetLink);
    void sendVerificationEmail(String to, String code);
}