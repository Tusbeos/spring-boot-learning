package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreateSpecialtyRequest;
import com.emedicalbooking.dto.request.UpdateSpecialtyRequest;
import com.emedicalbooking.dto.response.SpecialtyResponse;

import java.util.List;

public interface SpecialtyService {

    void createSpecialty(CreateSpecialtyRequest request);

    List<SpecialtyResponse> getAllSpecialties(Integer limit);

    List<SpecialtyResponse> getSpecialtiesByIds(List<Integer> ids);

    void updateSpecialty(Long id, UpdateSpecialtyRequest request);

    void deleteSpecialty(Long id);
}
