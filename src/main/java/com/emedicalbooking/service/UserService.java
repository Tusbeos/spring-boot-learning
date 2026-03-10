package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.ChangePasswordRequest;
import com.emedicalbooking.dto.request.CreateUserRequest;
import com.emedicalbooking.dto.request.UpdateUserRequest;
import com.emedicalbooking.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();

    UserResponse getUserById(int id);

    void createUser(CreateUserRequest request);

    void updateUser(int id, UpdateUserRequest request);

    void deleteUser(int id);

    void changePassword(int userId, ChangePasswordRequest request);
}
