package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.CreateSpecialtyRequest;
import com.emedicalbooking.dto.request.UpdateSpecialtyRequest;
import com.emedicalbooking.dto.response.SpecialtyResponse;
import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.entity.Specialty;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.AllCodeRepository;
import com.emedicalbooking.repository.DoctorClinicSpecialtyRepository;
import com.emedicalbooking.repository.SpecialtyRepository;
import com.emedicalbooking.service.SpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final DoctorClinicSpecialtyRepository doctorClinicSpecialtyRepository;
    private final AllCodeRepository allCodeRepository;

    @Override
    @Transactional
    public void createSpecialty(CreateSpecialtyRequest request) {
        AllCode sd1 = allCodeRepository.findByKeyMap("SD1").orElse(null);
        Specialty specialty = Specialty.builder()
                .name(request.getName())
                .descriptionHTML(request.getDescriptionHTML())
                .descriptionMarkdown(request.getDescriptionMarkdown())
                .image(decodeBase64Image(request.getImageBase64()))
                .statusData(sd1)
                .build();
        specialtyRepository.save(specialty);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getAllSpecialties(Integer limit) {
        List<Specialty> specialties = specialtyRepository.findAll();
        var stream = specialties.stream();
        if (limit != null && limit > 0) {
            stream = stream.limit(limit);
        }
        return stream.map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SpecialtyResponse> getSpecialtiesByIds(List<Integer> ids) {
        return specialtyRepository.findByIdIn(ids).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateSpecialty(Long id, UpdateSpecialtyRequest request) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", id));

        if (request.getName() != null) specialty.setName(request.getName());
        if (request.getDescriptionHTML() != null) specialty.setDescriptionHTML(request.getDescriptionHTML());
        if (request.getDescriptionMarkdown() != null) specialty.setDescriptionMarkdown(request.getDescriptionMarkdown());
        if (request.getImageBase64() != null) specialty.setImage(decodeBase64Image(request.getImageBase64()));

        specialtyRepository.save(specialty);
    }

    /** Xử lý cả 2 dạng: chuỗi base64 thuần và data URI (data:image/...;base64,...) */
    private byte[] decodeBase64Image(String imageBase64) {
        String pure = imageBase64.contains(",")
                ? imageBase64.substring(imageBase64.indexOf(',') + 1)
                : imageBase64;
        return Base64.getDecoder().decode(pure.trim());
    }

    @Override
    @Transactional
    public void deleteSpecialty(Long id) {
        Specialty specialty = specialtyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", id));

        // Cascade: xóa DoctorClinicSpecialty liên quan
        doctorClinicSpecialtyRepository.deleteAllBySpecialtyId(id);
        specialtyRepository.delete(specialty);
    }

    private SpecialtyResponse toResponse(Specialty s) {
        return SpecialtyResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .descriptionHTML(s.getDescriptionHTML())
                .descriptionMarkdown(s.getDescriptionMarkdown())
                .image(s.getImage() != null ? Base64.getEncoder().encodeToString(s.getImage()) : null)
                .build();
    }
}
