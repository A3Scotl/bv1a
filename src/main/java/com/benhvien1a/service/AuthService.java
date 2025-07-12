package com.benhvien1a.service;

import com.benhvien1a.dto.auth.*;

public interface AuthService {
    AuthResponse createUser(RegisterRequest request); // Admin-only
    AuthResponse login(LoginRequest request);
    AuthResponse forgotPassword(ForgotPasswordRequest request);
    AuthResponse resendVerificationCode(ForgotPasswordRequest request);
    AuthResponse resetPassword(ResetPasswordRequest request);
    AuthResponse changePassword(ChangePasswordRequest request);
}