/*
 * @ (#) AuthServiceImpl.java 1.0 5/4/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved
 */
package com.benhvien1a.service.impl;

import com.benhvien1a.dto.auth.*;
import com.benhvien1a.exception.AuthException;
import com.benhvien1a.model.Role;
import com.benhvien1a.model.User;
import com.benhvien1a.model.VerificationToken;
import com.benhvien1a.repository.UserRepository;
import com.benhvien1a.repository.VerificationTokenRepository;
import com.benhvien1a.security.JwtUtil;
import com.benhvien1a.service.AuthService;
import com.benhvien1a.service.EmailService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.regex.Pattern;

@Service
public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    // Biểu thức chính quy kiểm tra mật khẩu mạnh
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    // Giới hạn số lần gửi lại mã (3 lần trong 1 giờ)
    private static final int MAX_RESEND_COUNT = 3;
    private static final long RESEND_TIME_WINDOW = 3600000; // 1 giờ (ms)

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

    @Value("${verification.code.expiration:60}") // Mặc định 60 giây
    private long verificationCodeExpiration;

    /**
     * Khởi tạo tài khoản admin mặc định khi ứng dụng khởi động.
     */
    @PostConstruct
    public void initAdminAccount() {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            logger.info("Tạo tài khoản admin mặc định");
            User admin = User.builder()
                    .email("admin@gmail.com")
                    .fullName("Quản trị viên")
                    .password(passwordEncoder.encode("admin"))
                    .role(Role.ROLE_ADMIN)
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .build();
            userRepository.save(admin);
            logger.info("Tạo tài khoản admin thành công");
        } else {
            logger.info("Tài khoản admin đã tồn tại");
        }
    }

    /**
     * Tạo tài khoản mới (chỉ admin).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public AuthResponse createUser(RegisterRequest request) {
        logger.info("Tạo tài khoản mới với email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Email đã tồn tại: {}", request.getEmail());
            throw new AuthException("Email đã tồn tại");
        }

        // Kiểm tra mật khẩu mạnh
        if (!isPasswordStrong(request.getPassword())) {
            logger.warn("Mật khẩu yếu cho email: {}", request.getEmail());
            throw new AuthException("Mật khẩu phải dài ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
        }

        // Gán vai trò mặc định là EDITOR trừ khi được chỉ định là ADMIN
        Role role = request.getRole() != null && request.getRole().equals(Role.ROLE_ADMIN) ? Role.ROLE_ADMIN : Role.ROLE_EDITOR;

        User user = User.builder()
                .email(request.getEmail().toLowerCase())
                .fullName(request.getFullName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        userRepository.save(user);
        logger.info("Tạo tài khoản thành công: {}", user.getEmail());

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token);
    }

    /**
     * Xử lý đăng nhập người dùng.
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        logger.info("Thử đăng nhập với email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail().toLowerCase());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.error("Thông tin đăng nhập không hợp lệ cho: {}", request.getEmail());
            throw new AuthException("Thông tin đăng nhập không hợp lệ");
        }

        if (!user.isActive()) {
            logger.warn("Tài khoản không hoạt động: {}", request.getEmail());
            throw new AuthException("Tài khoản không hoạt động");
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
     * Yêu cầu đặt lại mật khẩu.
     */
    @Override
    public AuthResponse forgotPassword(ForgotPasswordRequest request) {
        logger.info("Yêu cầu đặt lại mật khẩu cho: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            logger.warn("Không tìm thấy email: {}", request.getEmail());
            throw new AuthException("Không tìm thấy email");
        }

        // Tạo mã xác thực 6 chữ số
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        LocalDateTime timestamp = LocalDateTime.now();
        verificationTokenRepository.save(VerificationToken.builder()
                .email(request.getEmail())
                .fullName(user.getFullName())
                .password(user.getPassword())
                .code(verificationCode)
                .timestamp(timestamp)
                .resendCount(0)
                .lastResendTime(timestamp)
                .build());

        emailService.sendVerificationEmail(request.getEmail(), verificationCode);
        logger.info("Mã xác thực đã gửi tới: {}. Mã hết hạn sau {} giây", request.getEmail(), verificationCodeExpiration);

        return new AuthResponse(null);
    }

    /**
     * Gửi lại mã xác thực cho đặt lại mật khẩu.
     */
    @Override
    public AuthResponse resendVerificationCode(ForgotPasswordRequest request) {
        logger.info("Gửi lại mã xác thực cho email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            logger.warn("Không tìm thấy email: {}", request.getEmail());
            throw new AuthException("Không tìm thấy email");
        }

        VerificationToken token = verificationTokenRepository.findByEmail(request.getEmail());
        if (token == null) {
            logger.warn("Không tìm thấy yêu cầu đặt lại mật khẩu cho email: {}", request.getEmail());
            throw new AuthException("Không tìm thấy yêu cầu đặt lại mật khẩu");
        }

        LocalDateTime currentTime = LocalDateTime.now();
        if (currentTime.isBefore(token.getLastResendTime().plusNanos(RESEND_TIME_WINDOW * 1000000))) {
            if (token.getResendCount() >= MAX_RESEND_COUNT) {
                logger.warn("Đã đạt giới hạn gửi lại mã cho email: {}", request.getEmail());
                throw new AuthException("Đã đạt giới hạn gửi lại mã. Vui lòng thử lại sau.");
            }
        }

        // Tạo mã mới
        String verificationCode = String.format("%06d", new Random().nextInt(999999));
        token.setCode(verificationCode);
        token.setTimestamp(LocalDateTime.now());
        token.setResendCount(token.getResendCount() + 1);
        token.setLastResendTime(LocalDateTime.now());
        verificationTokenRepository.save(token);

        emailService.sendVerificationEmail(request.getEmail(), verificationCode);
        logger.info("Mã xác thực đã gửi lại tới: {}. Số lần gửi lại: {}, hết hạn sau {} giây",
                request.getEmail(), token.getResendCount(), verificationCodeExpiration);

        return new AuthResponse(null);
    }

    /**
     * Đặt lại mật khẩu bằng mã xác thực.
     */
    @Override
    public AuthResponse resetPassword(ResetPasswordRequest request) {
        logger.info("Thử đặt lại mật khẩu");

        VerificationToken verificationToken = verificationTokenRepository.findByEmail(request.getEmail());
        if (verificationToken == null) {
            logger.warn("Không tìm thấy mã xác thực cho email: {}", request.getEmail());
            throw new AuthException("Không tìm thấy mã xác thực");
        }

        // Kiểm tra thời gian hết hạn
        if (LocalDateTime.now().isAfter(verificationToken.getTimestamp().plusNanos(verificationCodeExpiration * 1000000))) {
            logger.warn("Mã xác thực đã hết hạn cho email: {}", request.getEmail());
            throw new AuthException("Mã xác thực đã hết hạn");
        }

        if (!verificationToken.getCode().equals(request.getVerificationCode())) {
            logger.warn("Mã xác thực không hợp lệ cho email: {}", request.getEmail());
            throw new AuthException("Mã xác thực không hợp lệ");
        }

        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            logger.warn("Không tìm thấy người dùng với email: {}", request.getEmail());
            throw new AuthException("Không tìm thấy người dùng");
        }

        // Kiểm tra mật khẩu mạnh
        if (!isPasswordStrong(request.getPassword())) {
            logger.warn("Mật khẩu yếu cho email: {}", request.getEmail());
            throw new AuthException("Mật khẩu phải dài ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
        }

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        logger.info("Đặt lại mật khẩu thành công cho: {}", request.getEmail());

        return new AuthResponse(null);
    }

    /**
     * Thay đổi mật khẩu người dùng (chỉ admin).
     */
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public AuthResponse changePassword(ChangePasswordRequest request) {
        logger.info("Thay đổi mật khẩu cho người dùng: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail().toLowerCase());
        if (user == null) {
            logger.warn("Không tìm thấy người dùng: {}", request.getEmail());
            throw new AuthException("Không tìm thấy người dùng");
        }

        // Kiểm tra mật khẩu mạnh
        if (!isPasswordStrong(request.getNewPassword())) {
            logger.warn("Mật khẩu yếu cho email: {}", request.getEmail());
            throw new AuthException("Mật khẩu phải dài ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("Thay đổi mật khẩu thành công cho: {}", user.getEmail());

        return new AuthResponse(null);
    }

    /**
     * Kiểm tra mật khẩu có đủ mạnh không.
     */
    private boolean isPasswordStrong(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}