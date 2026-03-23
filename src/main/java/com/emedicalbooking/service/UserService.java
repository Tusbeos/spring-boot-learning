package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.ChangePasswordRequest;
import com.emedicalbooking.dto.request.CreateUserRequest;
import com.emedicalbooking.dto.request.UpdateUserRequest;
import com.emedicalbooking.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    void createUser(CreateUserRequest request);

    void updateUser(Long id, UpdateUserRequest request, String currentUserEmail);

    void deleteUser(Long id);

    void changePassword(Long userId, ChangePasswordRequest request, String currentUserEmail);
}
