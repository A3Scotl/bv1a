package com.benhvien1a.service;

import com.benhvien1a.response.AuthResponse;
import com.benhvien1a.request.LoginRequest;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}