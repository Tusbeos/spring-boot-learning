package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.ChangePasswordRequest;
import com.emedicalbooking.dto.request.CreateUserRequest;
import com.emedicalbooking.dto.request.UpdateUserRequest;
import com.emedicalbooking.dto.response.UserResponse;
import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.exception.DuplicateEmailException;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.AllCodeRepository;
import com.emedicalbooking.repository.UserRepository;
import com.emedicalbooking.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AllCodeRepository allCodeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAllWithRelations()
                .stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(int id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    @Transactional
    public void createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .build();

        if (request.getGender() != null) {
            user.setGenderData(findAllCode(request.getGender()));
        }
        if (request.getRoleId() != null) {
            user.setRoleData(findAllCode(request.getRoleId()));
        }
        if (request.getPositionId() != null) {
            user.setPositionData(findAllCode(request.getPositionId()));
        }
        if (request.getAvatar() != null) {
            user.setImage(decodeBase64Image(request.getAvatar()));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateUser(int id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        user.setGenderData(findAllCode(request.getGender()));
        user.setRoleData(findAllCode(request.getRoleId()));
        user.setPositionData(findAllCode(request.getPositionId()));

        if (request.getAvatar() != null) {
            user.setImage(decodeBase64Image(request.getAvatar()));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    private byte[] decodeBase64Image(String imageBase64) {
        String pure = imageBase64.contains(",")
                ? imageBase64.substring(imageBase64.indexOf(',') + 1)
                : imageBase64;
        return Base64.getDecoder().decode(pure.trim());
    }

    private AllCode findAllCode(String keyMap) {
        return allCodeRepository.findAll().stream()
                .filter(a -> a.getKeyMap().equals(keyMap))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", keyMap));
    }

    @Override
    @Transactional
    public void changePassword(int userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không chính xác");
        }

        // Kiểm tra mật khẩu mới và xác nhận có khớp không
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới và xác nhận mật khẩu không khớp");
        }

        // Kiểm tra mật khẩu mới không trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
