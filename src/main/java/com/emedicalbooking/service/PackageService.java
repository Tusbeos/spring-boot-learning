package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreatePackageRequest;
import com.emedicalbooking.dto.request.UpdatePackageRequest;
import com.emedicalbooking.dto.response.PackageResponse;

import java.util.List;

public interface PackageService {

    void createPackage(CreatePackageRequest request);

    List<PackageResponse> getAllPackages(Integer limit);

    PackageResponse getPackageById(Long id);

    void updatePackage(Long id, UpdatePackageRequest request);

    void deletePackage(Long id);
}
