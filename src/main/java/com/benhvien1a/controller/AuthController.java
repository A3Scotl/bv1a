/*
 * @ (#) AuthController.java 1.0 2025-05-05
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.controller;

import com.benhvien1a.dto.auth.*;
import com.benhvien1a.dto.response.ApiResponse;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("Nhận yêu cầu tạo tài khoản cho: {}", request.getEmail());
        try {
            AuthResponse response = authService.createUser(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Tạo tài khoản thành công",
                    response,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/register"
            ));
        } catch (AuthException e) {
            logger.error("Tạo tài khoản thất bại cho {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/register"
            ));
        }
    }

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

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<AuthResponse>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Nhận yêu cầu đặt lại mật khẩu cho: {}", request.getEmail());
        try {
            AuthResponse response = authService.forgotPassword(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Mã xác thực đã được gửi tới email",
                    response,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/forgot-password"
            ));
        } catch (AuthException e) {
            logger.error("Xử lý đặt lại mật khẩu thất bại cho {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/forgot-password"
            ));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<AuthResponse>> resendVerificationCode(@Valid @RequestBody ForgotPasswordRequest request) {
        logger.info("Nhận yêu cầu gửi lại mã xác thực cho: {}", request.getEmail());
        try {
            AuthResponse response = authService.resendVerificationCode(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Mã xác thực đã được gửi lại tới email",
                    response,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/resend-verification"
            ));
        } catch (AuthException e) {
            logger.error("Gửi lại mã xác thực thất bại cho {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/resend-verification"
            ));
        }
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ApiResponse<AuthResponse>> handleGetResetPassword(@RequestParam("token") String token) {
        logger.info("Nhận yêu cầu GET đặt lại mật khẩu với token: {}", token);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Vui lòng sử dụng phương thức POST để đặt lại mật khẩu với token này.",
                null,
                null,
                ZonedDateTime.now(ZoneId.of("UTC")),
                "/api/v1/auth/reset-password"
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<AuthResponse>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        logger.info("Nhận yêu cầu đặt lại mật khẩu cho email: {}", request.getEmail());
        try {
            AuthResponse response = authService.resetPassword(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Đặt lại mật khẩu thành công",
                    response,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/reset-password"
            ));
        } catch (AuthException e) {
            logger.error("Đặt lại mật khẩu thất bại cho {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/reset-password"
            ));
        }
    }

    @PostMapping("/change-password")
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    public ResponseEntity<ApiResponse<AuthResponse>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        logger.info("Nhận yêu cầu thay đổi mật khẩu cho: {}", request.getEmail());
        try {
            AuthResponse response = authService.changePassword(request);
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Thay đổi mật khẩu thành công",
                    response,
                    null,
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/change-password"
            ));
        } catch (AuthException e) {
            logger.error("Thay đổi mật khẩu thất bại cho {}: {}", request.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(
                    false,
                    e.getMessage(),
                    null,
                    e.getMessage(),
                    ZonedDateTime.now(ZoneId.of("UTC")),
                    "/api/v1/auth/change-password"
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