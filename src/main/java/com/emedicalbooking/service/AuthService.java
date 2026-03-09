package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.LoginRequest;
import com.emedicalbooking.dto.request.RegisterRequest;
import com.emedicalbooking.dto.response.AuthResponse;

/**
 * Service xác thực: đăng ký và đăng nhập.
 */
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
