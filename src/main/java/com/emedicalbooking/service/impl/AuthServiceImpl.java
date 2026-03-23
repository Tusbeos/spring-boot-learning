package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.LoginRequest;
import com.emedicalbooking.dto.request.RegisterRequest;
import com.emedicalbooking.dto.response.AuthResponse;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.exception.DuplicateEmailException;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.AllCodeRepository;
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
    private final AllCodeRepository allCodeRepository;
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
                .roleData(allCodeRepository.findByKeyMap("R3")
                        .orElseThrow(() -> new ResourceNotFoundException("Role R3 không tồn tại")))
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
        // Lấy thông tin user (đã gộp fetch role để tránh lazy exception)
        User user = userRepository.findByEmailWithRole(request.getEmail())
                .orElseThrow(() -> new org.springframework.security.authentication.BadCredentialsException("Email hoặc mật khẩu không đúng"));

        // Xác thực mật khẩu
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        // Build UserDetails tay (tránh query DB lần 2)
        java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = java.util.Collections.emptyList();
        if (user.getRoleData() != null) {
            authorities = java.util.Collections.singletonList(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRoleData().getKeyMap())
            );
        }
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );

        String token = jwtTokenProvider.generateToken(userDetails);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        // Lưu refresh token vào DB (thời hạn 7 ngày = 168 giờ)
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(java.time.LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthResponse.builder()
                .id(user.getId())
                .token(token)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleId(user.getRoleData() != null ? user.getRoleData().getKeyMap() : null)
                .build();
    }

    @Override
    public AuthResponse refresh(com.emedicalbooking.dto.request.RefreshTokenRequest request) {
        // Tìm user theo refresh token
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token không hợp lệ hoặc đã đăng xuất"));

        // Kiểm tra hết hạn
        if (user.getRefreshTokenExpiry() == null || user.getRefreshTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh Token đã hết hạn. Vui lòng đăng nhập lại.");
        }

        // Tạo access token mới
        java.util.List<org.springframework.security.core.authority.SimpleGrantedAuthority> authorities = java.util.Collections.emptyList();
        if (user.getRoleData() != null) {
            authorities = java.util.Collections.singletonList(
                    new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRoleData().getKeyMap())
            );
        }
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), authorities
        );

        String newToken = jwtTokenProvider.generateToken(userDetails);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken();

        // Rotate refresh token
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(java.time.LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return AuthResponse.builder()
                .id(user.getId())
                .token(newToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roleId(user.getRoleData() != null ? user.getRoleData().getKeyMap() : null)
                .build();
    }

    @Override
    public void logout(com.emedicalbooking.dto.request.RefreshTokenRequest request) {
        User user = userRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new IllegalArgumentException("Refresh Token không hợp lệ hoặc đã đăng xuất"));
        
        // Remove refresh token by setting it to null
        user.setRefreshToken(null);
        user.setRefreshTokenExpiry(null);
        userRepository.save(user);
    }
}
