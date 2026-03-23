package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreatePatientProfileRequest;
import com.emedicalbooking.dto.response.PatientProfileResponse;

import java.util.List;

public interface PatientProfileService {

    PatientProfileResponse createProfile(Long userId, CreatePatientProfileRequest request);

    List<PatientProfileResponse> getProfilesByUser(Long userId);

    PatientProfileResponse getProfileById(Long id);

    void deleteProfile(Long id);
}
