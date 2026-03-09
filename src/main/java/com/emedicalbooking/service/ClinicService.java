package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreateClinicRequest;
import com.emedicalbooking.dto.request.UpdateClinicRequest;
import com.emedicalbooking.dto.response.ClinicResponse;

import java.util.List;

public interface ClinicService {

    void createClinic(CreateClinicRequest request);

    List<ClinicResponse> getAllClinics(Integer limit);

    ClinicResponse getClinicDetail(int id);

    void updateClinic(int id, UpdateClinicRequest request);

    void deleteClinic(int id);
}
