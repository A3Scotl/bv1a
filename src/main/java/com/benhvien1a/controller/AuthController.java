/*
 * @ (#) AuthController.java 1.0 2025-05-05
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.controller;

import com.benhvien1a.response.AuthResponse;
import com.benhvien1a.request.LoginRequest;
import com.benhvien1a.response.ApiResponse;
import com.benhvien1a.exception.AuthException;
import com.benhvien1a.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        logger.info("Nhận yêu cầu đăng nhập cho: {}", request.getEmail());
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Đăng nhập thành công",
                    response,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/login"
            ));
        } catch (AuthException e) {
            logger.error("Đăng nhập thất bại cho {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/login"
            ));
        }
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<AuthResponse>> logout(HttpServletResponse response) {
        logger.info("Nhận yêu cầu đăng xuất");
        // Xóa cookie token
        Cookie cookie = new Cookie("token", null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        logger.info("Xóa cookie token khi đăng xuất");
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Đăng xuất thành công. Cookie đã được xóa.",
                null,
                null,
                ZonedDateTime.now(ZoneId.of("UTC")),
                "/api/v1/auth/logout"
        ));
    }
}