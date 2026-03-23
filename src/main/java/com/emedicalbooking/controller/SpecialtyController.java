package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.CreateSpecialtyRequest;
import com.emedicalbooking.dto.request.UpdateSpecialtyRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.DoctorDetailResponse;
import com.emedicalbooking.dto.response.SpecialtyResponse;
import com.emedicalbooking.service.DoctorService;
import com.emedicalbooking.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;
    private final DoctorService doctorService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSpecialty(
            @Valid @RequestBody CreateSpecialtyRequest request) {
        specialtyService.createSpecialty(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo chuyên khoa thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> getAllSpecialties(
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(ApiResponse.success("OK", specialtyService.getAllSpecialties(limit)));
    }

    @GetMapping("/by-ids")
    public ResponseEntity<ApiResponse<List<SpecialtyResponse>>> getSpecialtiesByIds(
            @RequestParam List<Integer> ids) {
        return ResponseEntity.ok(ApiResponse.success("OK", specialtyService.getSpecialtiesByIds(ids)));
    }

    @GetMapping("/{specialtyId}/doctors")
    public ResponseEntity<ApiResponse<List<DoctorDetailResponse>>> getDoctorsBySpecialtyId(
            @PathVariable Long specialtyId) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getDoctorsBySpecialtyId(specialtyId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateSpecialty(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSpecialtyRequest request) {
        specialtyService.updateSpecialty(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật chuyên khoa thành công", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpecialty(@PathVariable Long id) {
        specialtyService.deleteSpecialty(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa chuyên khoa thành công", null));
    }
}
