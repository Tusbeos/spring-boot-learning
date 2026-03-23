package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.CreatePackageRequest;
import com.emedicalbooking.dto.request.PackageServiceItemRequest;
import com.emedicalbooking.dto.request.UpdatePackageRequest;
import com.emedicalbooking.dto.response.AllCodeResponse;
import com.emedicalbooking.dto.response.PackageResponse;
import com.emedicalbooking.dto.response.PackageServiceItemResponse;
import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.entity.Clinic;
import com.emedicalbooking.entity.Markdown;
import com.emedicalbooking.entity.MedicalPackage;
import com.emedicalbooking.entity.PackageServiceItem;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.AllCodeRepository;
import com.emedicalbooking.repository.ClinicRepository;
import com.emedicalbooking.repository.MarkdownRepository;
import com.emedicalbooking.repository.PackageRepository;
import com.emedicalbooking.repository.PackageServiceItemRepository;
import com.emedicalbooking.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackageServiceImpl implements PackageService {

    private final PackageRepository packageRepository;
    private final PackageServiceItemRepository packageServiceItemRepository;
    private final AllCodeRepository allCodeRepository;
    private final ClinicRepository clinicRepository;
    private final MarkdownRepository markdownRepository;

    @Override
    @Transactional
    public void createPackage(CreatePackageRequest request) {
        AllCode typeData = allCodeRepository.findByKeyMap(request.getTypeCode())
                .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", request.getTypeCode()));

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", request.getClinicId()));

        MedicalPackage pkg = MedicalPackage.builder()
                .name(request.getName())
                .typeData(typeData)
                .clinic(clinic)
                .price(request.getPrice())
                .note(request.getNote())
                .image(request.getImageBase64() != null ? decodeBase64Image(request.getImageBase64()) : null)
                .build();

        packageRepository.save(pkg);

        // Lưu descriptionHTML/Markdown vào bảng markdowns
        markdownRepository.save(Markdown.builder()
                .packageId(pkg.getId())
                .contentHTML(request.getDescriptionHTML())
                .contentMarkdown(request.getDescriptionMarkdown())
                .build());

        // Lưu danh sách dịch vụ trong gói khám
        if (request.getPackageServices() != null && !request.getPackageServices().isEmpty()) {
            savePackageServiceItems(pkg, request.getPackageServices());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<PackageResponse> getAllPackages(Integer limit) {
        List<MedicalPackage> packages = packageRepository.findAll();
        var stream = packages.stream();
        if (limit != null && limit > 0) {
            stream = stream.limit(limit);
        }
        return stream.map(this::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PackageResponse getPackageById(Long id) {
        MedicalPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));
        return toResponse(pkg);
    }

    @Override
    @Transactional
    public void updatePackage(Long id, UpdatePackageRequest request) {
        MedicalPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));

        if (request.getName() != null) pkg.setName(request.getName());
        if (request.getPrice() != null) pkg.setPrice(request.getPrice());
        if (request.getNote() != null) pkg.setNote(request.getNote().isEmpty() ? null : request.getNote());
        if (request.getImageBase64() != null) pkg.setImage(decodeBase64Image(request.getImageBase64()));

        if (request.getTypeCode() != null) {
            AllCode typeData = allCodeRepository.findByKeyMap(request.getTypeCode())
                    .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", request.getTypeCode()));
            pkg.setTypeData(typeData);
        }

        if (request.getClinicId() != null) {
            Clinic clinic = clinicRepository.findById(request.getClinicId())
                    .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", request.getClinicId()));
            pkg.setClinic(clinic);
        }

        packageRepository.save(pkg);

        // Cập nhật descriptionHTML/Markdown trong bảng markdowns
        if (request.getDescriptionHTML() != null || request.getDescriptionMarkdown() != null) {
            Markdown md = markdownRepository.findFirstByPackageId(id)
                    .orElse(Markdown.builder().packageId(id).build());
            if (request.getDescriptionHTML() != null) md.setContentHTML(request.getDescriptionHTML());
            if (request.getDescriptionMarkdown() != null) md.setContentMarkdown(request.getDescriptionMarkdown());
            markdownRepository.save(md);
        }

        // Cập nhật danh sách dịch vụ: xóa cũ rồi tạo mới
        if (request.getPackageServices() != null) {
            packageServiceItemRepository.deleteAllByPackageId(id);
            savePackageServiceItems(pkg, request.getPackageServices());
        }
    }

    @Override
    @Transactional
    public void deletePackage(Long id) {
        MedicalPackage pkg = packageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Package", "id", id));

        packageServiceItemRepository.deleteAllByPackageId(id);
        // Xóa dữ liệu markdown của gói khám
        markdownRepository.findFirstByPackageId(id).ifPresent(markdownRepository::delete);
        packageRepository.delete(pkg);
    }

    // ==================== Helper ====================

    /** Lưu danh sách dịch vụ cho một gói khám */
    private void savePackageServiceItems(MedicalPackage pkg, List<PackageServiceItemRequest> items) {
        for (PackageServiceItemRequest itemReq : items) {
            if (itemReq.getServiceName() == null || itemReq.getServiceName().trim().isEmpty()) continue;

            AllCode groupServiceData = null;
            if (itemReq.getGroupServiceCode() != null && !itemReq.getGroupServiceCode().isEmpty()) {
                groupServiceData = allCodeRepository.findByKeyMap(itemReq.getGroupServiceCode()).orElse(null);
            }

            PackageServiceItem item = PackageServiceItem.builder()
                    .medicalPackage(pkg)
                    .groupServiceData(groupServiceData)
                    .serviceName(itemReq.getServiceName().trim())
                    .description(itemReq.getDescription())
                    .build();
            packageServiceItemRepository.save(item);
        }
    }

    private PackageResponse toResponse(MedicalPackage pkg) {
        // Map AllCode typeData
        AllCodeResponse typeDataRes = null;
        if (pkg.getTypeData() != null) {
            typeDataRes = AllCodeResponse.builder()
                    .keyMap(pkg.getTypeData().getKeyMap())
                    .valueEn(pkg.getTypeData().getValueEn())
                    .valueVi(pkg.getTypeData().getValueVi())
                    .build();
        }

        // Map danh sách dịch vụ
        List<PackageServiceItemResponse> serviceItems = pkg.getPackageServices() == null ? List.of() :
                pkg.getPackageServices().stream().map(s -> {
                    AllCodeResponse groupRes = null;
                    if (s.getGroupServiceData() != null) {
                        groupRes = AllCodeResponse.builder()
                                .keyMap(s.getGroupServiceData().getKeyMap())
                                .valueEn(s.getGroupServiceData().getValueEn())
                                .valueVi(s.getGroupServiceData().getValueVi())
                                .build();
                    }
                    return PackageServiceItemResponse.builder()
                            .id(s.getId())
                            .groupServiceCode(s.getGroupServiceData() != null ? s.getGroupServiceData().getKeyMap() : null)
                            .groupServiceData(groupRes)
                            .serviceName(s.getServiceName())
                            .description(s.getDescription())
                            .build();
                }).collect(Collectors.toList());

        return PackageResponse.builder()
                .id(pkg.getId())
                .name(pkg.getName())
                .typeCode(pkg.getTypeData() != null ? pkg.getTypeData().getKeyMap() : null)
                .typeData(typeDataRes)
                .clinicId(pkg.getClinic() != null ? pkg.getClinic().getId() : 0)
                .clinicName(pkg.getClinic() != null ? pkg.getClinic().getName() : null)
                .price(pkg.getPrice())
                .note(pkg.getNote())
                .descriptionHTML(markdownRepository.findFirstByPackageId(pkg.getId())
                        .map(Markdown::getContentHTML).orElse(null))
                .descriptionMarkdown(markdownRepository.findFirstByPackageId(pkg.getId())
                        .map(Markdown::getContentMarkdown).orElse(null))
                .image(pkg.getImage() != null ? Base64.getEncoder().encodeToString(pkg.getImage()) : null)
                .packageServices(serviceItems)
                .build();
    }

    /** Xử lý cả 2 dạng: chuỗi base64 thuần và data URI (data:image/...;base64,...) */
    private byte[] decodeBase64Image(String imageBase64) {
        String pure = imageBase64.contains(",")
                ? imageBase64.substring(imageBase64.indexOf(',') + 1)
                : imageBase64;
        return Base64.getDecoder().decode(pure.trim());
    }
}
