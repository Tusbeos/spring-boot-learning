package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreatePatientProfileRequest;
import com.emedicalbooking.dto.response.PatientProfileResponse;

import java.util.List;

public interface PatientProfileService {

    PatientProfileResponse createProfile(int userId, CreatePatientProfileRequest request);

    List<PatientProfileResponse> getProfilesByUser(int userId);

    PatientProfileResponse getProfileById(int id);

    void deleteProfile(int id);
}
