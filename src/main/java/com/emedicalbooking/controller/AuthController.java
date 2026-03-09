package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.LoginRequest;
import com.emedicalbooking.dto.request.RegisterRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.AuthResponse;
import com.emedicalbooking.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller xử lý đăng ký và đăng nhập.
 *
 * BASE URL: /api/auth
 * Các endpoint này là PUBLIC (không cần JWT) - đã khai báo trong SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     * Đăng ký tài khoản mới.
     *
     * Request body:
     * {
     *   "fullName": "Nguyễn Văn A",
     *   "email": "a@gmail.com",
     *   "password": "123456"
     * }
     *
     * @Valid → tự động validate các annotation trong RegisterRequest (@NotBlank, @Email...)
     *          nếu lỗi → GlobalExceptionHandler bắt và trả về 400 với danh sách lỗi
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)  // HTTP 201 Created
                .body(ApiResponse.success("Đăng ký thành công", response));
    }

    /**
     * POST /api/auth/login
     * Đăng nhập, nhận JWT token.
     *
     * Request body:
     * { "email": "a@gmail.com", "password": "123456" }
     *
     * Response:
     * { "success": true, "message": "Đăng nhập thành công",
     *   "data": { "token": "eyJ...", "email": "a@gmail.com", ... } }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }
}
