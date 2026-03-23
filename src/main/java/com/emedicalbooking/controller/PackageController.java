package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.CreatePackageRequest;
import com.emedicalbooking.dto.request.UpdatePackageRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.PackageResponse;
import com.emedicalbooking.service.PackageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class PackageController {

    private final PackageService packageService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createPackage(
            @Valid @RequestBody CreatePackageRequest request) {
        packageService.createPackage(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo gói khám thành công", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getAllPackages(
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(ApiResponse.success("OK", packageService.getAllPackages(limit)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PackageResponse>> getPackageById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("OK", packageService.getPackageById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updatePackage(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePackageRequest request) {
        packageService.updatePackage(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật gói khám thành công", null));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePackage(@PathVariable Long id) {
        packageService.deletePackage(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa gói khám thành công", null));
    }
}
