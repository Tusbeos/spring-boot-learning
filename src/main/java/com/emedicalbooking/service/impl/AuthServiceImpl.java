package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.LoginRequest;
import com.emedicalbooking.dto.request.RegisterRequest;
import com.emedicalbooking.dto.response.AuthResponse;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.exception.DuplicateEmailException;
import com.emedicalbooking.repository.UserRepository;
import com.emedicalbooking.security.JwtTokenProvider;
import com.emedicalbooking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    /**
     * Đăng ký tài khoản mới.
     *
     * Luồng:
     * 1. Kiểm tra email đã tồn tại chưa → nếu có, throw DuplicateEmailException
     * 2. Mã hóa password bằng BCrypt (KHÔNG lưu raw password!)
     * 3. Tạo User entity với role mặc định là PATIENT
     * 4. Lưu vào database
     * 5. Tạo JWT token và trả về
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        // Bước 1: Kiểm tra email trùng
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        // Bước 2 + 3: Tạo user mới, hash password
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .build();

        // Bước 4: Lưu vào DB
        userRepository.save(user);

        // Bước 5: Tạo JWT và trả về
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

    /**
     * Đăng nhập.
     *
     * Luồng:
     * 1. AuthenticationManager kiểm tra email + password
     *    → nếu sai, tự throw BadCredentialsException (GlobalExceptionHandler bắt)
     * 2. Load UserDetails, tạo JWT token
     * 3. Trả về token cùng thông tin user
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        // Bước 1: Spring Security tự xác thực (so sánh password với BCrypt hash trong DB)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Bước 2: Lấy thông tin user và tạo token
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtTokenProvider.generateToken(userDetails);

        // Load user with role to avoid LazyInitializationException
        User userWithRole = userRepository.findByEmailWithRole(user.getEmail()).orElse(user);

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .tokenType("Bearer")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleId(userWithRole.getRoleData() != null ? userWithRole.getRoleData().getKeyMap() : null)
                .build();
    }
}
