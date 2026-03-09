package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.CreatePatientProfileRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.PatientProfileResponse;
import com.emedicalbooking.service.PatientProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patient-profiles")
@RequiredArgsConstructor
public class PatientProfileController {

    private final PatientProfileService patientProfileService;

    /** Tạo hồ sơ bệnh nhân mới cho một user */
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PatientProfileResponse>> createProfile(
            @PathVariable int userId,
            @Valid @RequestBody CreatePatientProfileRequest request) {
        PatientProfileResponse response = patientProfileService.createProfile(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo hồ sơ bệnh nhân thành công", response));
    }

    /** Lấy danh sách tất cả hồ sơ bệnh nhân của một user */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PatientProfileResponse>>> getProfilesByUser(
            @PathVariable int userId) {
        List<PatientProfileResponse> profiles = patientProfileService.getProfilesByUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách hồ sơ thành công", profiles));
    }

    /** Lấy chi tiết một hồ sơ bệnh nhân theo id */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PatientProfileResponse>> getProfileById(@PathVariable int id) {
        PatientProfileResponse profile = patientProfileService.getProfileById(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy hồ sơ thành công", profile));
    }

    /** Xoá hồ sơ bệnh nhân */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable int id) {
        patientProfileService.deleteProfile(id);
        return ResponseEntity.ok(ApiResponse.success("Xoá hồ sơ thành công", null));
    }
}
