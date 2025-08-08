/*
 * @ (#) AuthServiceImpl.java 1.0 5/4/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.response.AuthResponse;
import com.benhvien1a.request.LoginRequest;
import com.benhvien1a.exception.AuthException;
import com.benhvien1a.model.Role;
import com.benhvien1a.model.User;
import com.benhvien1a.repository.UserRepository;
import com.benhvien1a.security.JwtUtil;
import com.benhvien1a.service.AuthService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    // Biểu thức chính quy kiểm tra mật khẩu mạnh
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    @Autowired
    private UserRepository userRepository;



    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    /**
     * Khởi tạo tài khoản admin mặc định khi ứng dụng khởi động.
     */
    @PostConstruct
    public void initAdminAccount() {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            logger.info("Tạo tài khoản admin mặc định");
            User admin = User.builder()
                    .email("admin@gmail.com")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            logger.info("Tạo tài khoản admin thành công");
        } else {
            logger.info("Tài khoản admin đã tồn tại");
        }
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("Thử đăng nhập với email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail().toLowerCase());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Thông tin đăng nhập không hợp lệ cho: {}", request.getEmail());
            throw new AuthException("Thông tin đăng nhập không hợp lệ");
        }


        // Xác thực với AuthenticationManager
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            logger.warn("Xác thực thất bại nhưng tiếp tục: {}", e.getMessage());
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        logger.info("Đăng nhập thành công cho: {}, vai trò: {}", user.getEmail(), user.getRole());

        return new AuthResponse(token);
    }

    /**
     * Kiểm tra mật khẩu có đủ mạnh không.
     */
    private boolean isPasswordStrong(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}