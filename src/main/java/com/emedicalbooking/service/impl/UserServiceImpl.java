package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.ChangePasswordRequest;
import com.emedicalbooking.dto.request.CreateUserRequest;
import com.emedicalbooking.dto.request.UpdateUserRequest;
import com.emedicalbooking.dto.response.UserResponse;
import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.entity.Clinic;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.exception.DuplicateEmailException;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.AllCodeRepository;
import com.emedicalbooking.repository.ClinicRepository;
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
    private final ClinicRepository clinicRepository;
    private final com.emedicalbooking.repository.DoctorInfosRepository doctorInfosRepository;
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
    public UserResponse getUserById(Long id) {
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
        applyClinicManagerAssignment(user, request.getRoleId(), request.getClinicId());
        if (request.getAvatar() != null) {
            user.setImage(decodeBase64Image(request.getAvatar()));
        }

        userRepository.save(user);

        // Nếu là bác sĩ (R2), tạo mặc định DoctorInfos với status SD1
        if ("R2".equals(request.getRoleId())) {
            AllCode sd1 = allCodeRepository.findByKeyMap("SD1").orElse(null);
            com.emedicalbooking.entity.DoctorInfos doctorInfo = com.emedicalbooking.entity.DoctorInfos.builder()
                    .doctor(user)
                    .statusData(sd1)
                    .build();
            doctorInfosRepository.save(doctorInfo);
        }
    }

    @Override
    @Transactional
    public void updateUser(Long id, UpdateUserRequest request, String currentUserEmail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        // Kiểm tra quyền: Admin (R1) có thể sửa bất kỳ user, còn lại chỉ được sửa chính mình
        User currentUser = userRepository.findByEmailWithRole(currentUserEmail).orElse(null);
        boolean isAdmin = currentUser != null
                && currentUser.getRoleData() != null
                && "R1".equals(currentUser.getRoleData().getKeyMap());
                
        if (!isAdmin && (currentUser == null || currentUser.getId() != id)) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn không có quyền cập nhật thông tin user khác");
        }

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());

        if (request.getGender() != null) {
            user.setGenderData(findAllCode(request.getGender()));
        }

        // Chỉ Admin mới được thay đổi role và position
        if (isAdmin) {
            if (request.getRoleId() != null) {
                user.setRoleData(findAllCode(request.getRoleId()));
            }
            if (request.getPositionId() != null) {
                user.setPositionData(findAllCode(request.getPositionId()));
            }
            applyClinicManagerAssignment(user, request.getRoleId(), request.getClinicId());
        }

        if (request.getAvatar() != null) {
            user.setImage(decodeBase64Image(request.getAvatar()));
        }

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
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
        if (keyMap == null) return null;
        return allCodeRepository.findByKeyMap(keyMap)
                .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", keyMap));
    }

    private void applyClinicManagerAssignment(User user, String roleId, Long clinicId) {
        if (!"R4".equals(roleId)) {
            user.setClinic(null);
            return;
        }
        if (clinicId == null) {
            throw new IllegalArgumentException("Clinic Manager phải được phân quyền quản lý một cơ sở y tế");
        }
        Clinic clinic = clinicRepository.findById(clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", clinicId));
        user.setClinic(clinic);
        user.setImage(clinic.getImage());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request, String currentUserEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        // Kiểm tra quyền: Chỉ cho phép user đổi mật khẩu của chính mình
        User currentUser = userRepository.findByEmailWithRole(currentUserEmail).orElse(null);
        if (currentUser == null || currentUser.getId() != userId) {
            throw new org.springframework.security.access.AccessDeniedException("Bạn chỉ có thể đổi mật khẩu cho chính mình");
        }

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
