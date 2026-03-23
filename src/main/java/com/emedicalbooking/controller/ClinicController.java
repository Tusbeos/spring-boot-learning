package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.CreateClinicRequest;
import com.emedicalbooking.dto.request.UpdateClinicRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.ClinicResponse;
import com.emedicalbooking.service.ClinicService;
import com.emedicalbooking.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clinics")
@RequiredArgsConstructor
public class ClinicController {

    private final ClinicService clinicService;
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createClinic(
            @Valid @RequestBody CreateClinicRequest request) {
        clinicService.createClinic(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo phòng khám thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClinicResponse>>> getAllClinics(
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(ApiResponse.success("OK", clinicService.getAllClinics(limit)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ClinicResponse>> getClinicDetail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("OK", clinicService.getClinicDetail(id)));
    }

    @GetMapping("/{clinicId}/doctors")
    public ResponseEntity<ApiResponse<List<Long>>> getDoctorsByClinicId(@PathVariable Long clinicId) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getDoctorIdsByClinicId(clinicId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateClinic(
            @PathVariable Long id,
            @Valid @RequestBody UpdateClinicRequest request) {
        clinicService.updateClinic(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật phòng khám thành công", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClinic(@PathVariable Long id) {
        clinicService.deleteClinic(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa phòng khám thành công", null));
    }
}
