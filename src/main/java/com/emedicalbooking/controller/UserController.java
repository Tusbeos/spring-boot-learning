package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.ChangePasswordRequest;
import com.emedicalbooking.dto.request.CreateUserRequest;
import com.emedicalbooking.dto.request.UpdateUserRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.UserResponse;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.repository.UserRepository;
import com.emedicalbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách user thành công", users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy user thành công", user));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createUser(@Valid @RequestBody CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo user thành công", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable Long id,
                                                         @Valid @RequestBody UpdateUserRequest request,
                                                         @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        userService.updateUser(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật user thành công", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa user thành công", null));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@PathVariable Long id,
                                                             @Valid @RequestBody ChangePasswordRequest request,
                                                             @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getSubject();
        userService.changePassword(id, request, email);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }
}
