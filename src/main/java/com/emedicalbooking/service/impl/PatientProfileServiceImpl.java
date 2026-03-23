package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.CreatePatientProfileRequest;
import com.emedicalbooking.dto.response.PatientProfileResponse;
import com.emedicalbooking.entity.PatientProfile;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.PatientProfileRepository;
import com.emedicalbooking.repository.UserRepository;
import com.emedicalbooking.service.PatientProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientProfileServiceImpl implements PatientProfileService {

    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PatientProfileResponse createProfile(Long userId, CreatePatientProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng id=" + userId));

        PatientProfile profile = PatientProfile.builder()
                .user(user)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .relationship(request.getRelationship())
                .medicalHistory(request.getMedicalHistory())
                .build();

        PatientProfile saved = patientProfileRepository.save(profile);
        return toResponse(saved);
    }

    @Override
    public List<PatientProfileResponse> getProfilesByUser(Long userId) {
        return patientProfileRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PatientProfileResponse getProfileById(Long id) {
        PatientProfile profile = patientProfileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ id=" + id));
        return toResponse(profile);
    }

    @Override
    @Transactional
    public void deleteProfile(Long id) {
        if (!patientProfileRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy hồ sơ id=" + id);
        }
        patientProfileRepository.deleteById(id);
    }

    private PatientProfileResponse toResponse(PatientProfile p) {
        return PatientProfileResponse.builder()
                .id(p.getId())
                .userId(p.getUser().getId())
                .firstName(p.getFirstName())
                .lastName(p.getLastName())
                .phoneNumber(p.getPhoneNumber())
                .gender(p.getGender())
                .dateOfBirth(p.getDateOfBirth())
                .address(p.getAddress())
                .relationship(p.getRelationship())
                .medicalHistory(p.getMedicalHistory())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
