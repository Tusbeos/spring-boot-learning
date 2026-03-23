package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.LoginRequest;
import com.emedicalbooking.dto.request.RegisterRequest;
import com.emedicalbooking.dto.response.AuthResponse;


public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refresh(com.emedicalbooking.dto.request.RefreshTokenRequest request);
    void logout(com.emedicalbooking.dto.request.RefreshTokenRequest request);
}
