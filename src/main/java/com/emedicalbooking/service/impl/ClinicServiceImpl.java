package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.CreateClinicRequest;
import com.emedicalbooking.dto.request.UpdateClinicRequest;
import com.emedicalbooking.dto.response.ClinicResponse;
import com.emedicalbooking.entity.Clinic;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.ClinicRepository;
import com.emedicalbooking.repository.DoctorClinicSpecialtyRepository;
import com.emedicalbooking.service.ClinicService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClinicServiceImpl implements ClinicService {

    private final ClinicRepository clinicRepository;
    private final DoctorClinicSpecialtyRepository doctorClinicSpecialtyRepository;

    @Override
    @Transactional
    public void createClinic(CreateClinicRequest request) {
        Clinic clinic = Clinic.builder()
                .name(request.getName())
                .address(request.getAddress())
                .image(Base64.getDecoder().decode(request.getImageBase64()))
                .imageCover(Base64.getDecoder().decode(request.getImageCoverBase64()))
                .descriptionHTML(request.getDescriptionHTML())
                .descriptionMarkdown(request.getDescriptionMarkdown())
                .build();
        clinicRepository.save(clinic);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClinicResponse> getAllClinics(Integer limit) {
        List<Clinic> clinics = clinicRepository.findAll();
        var stream = clinics.stream();
        if (limit != null && limit > 0) {
            stream = stream.limit(limit);
        }
        return stream.map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClinicResponse getClinicDetail(int id) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", id));
        return toResponse(clinic);
    }

    @Override
    @Transactional
    public void updateClinic(int id, UpdateClinicRequest request) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", id));

        if (request.getName() != null) clinic.setName(request.getName());
        if (request.getAddress() != null) clinic.setAddress(request.getAddress());
        if (request.getDescriptionHTML() != null) clinic.setDescriptionHTML(request.getDescriptionHTML());
        if (request.getDescriptionMarkdown() != null) clinic.setDescriptionMarkdown(request.getDescriptionMarkdown());
        if (request.getImageBase64() != null) clinic.setImage(Base64.getDecoder().decode(request.getImageBase64()));
        if (request.getImageCoverBase64() != null) clinic.setImageCover(Base64.getDecoder().decode(request.getImageCoverBase64()));

        clinicRepository.save(clinic);
    }

    @Override
    @Transactional
    public void deleteClinic(int id) {
        Clinic clinic = clinicRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", id));

        // Cascade: xóa DoctorClinicSpecialty liên quan
        doctorClinicSpecialtyRepository.deleteAllByClinicId(id);
        clinicRepository.delete(clinic);
    }

    private ClinicResponse toResponse(Clinic c) {
        return ClinicResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .address(c.getAddress())
                .image(c.getImage() != null ? Base64.getEncoder().encodeToString(c.getImage()) : null)
                .imageCover(c.getImageCover() != null ? Base64.getEncoder().encodeToString(c.getImageCover()) : null)
                .descriptionHTML(c.getDescriptionHTML())
                .descriptionMarkdown(c.getDescriptionMarkdown())
                .build();
    }
}
