package com.emedicalbooking.controller;

import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.ApprovalRequestResponse;
import com.emedicalbooking.dto.response.PackageResponse;
import com.emedicalbooking.service.ApprovalRequestService;
import com.emedicalbooking.service.DoctorService;
import com.emedicalbooking.service.PackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/clinic-manager")
@RequiredArgsConstructor
public class ClinicManagerController {

    private static final Set<String> MANAGER_DOCTOR_STATUSES = Set.of("SD2", "SD3", "SD4", "SD5");

    private final DoctorService doctorService;
    private final PackageService packageService;
    private final ApprovalRequestService approvalRequestService;

    /**
     * SD1 (Pending) -> SD2 (Active)
     * Duyệt bác sĩ mới đăng ký sang trạng thái hoạt động.
     */
    @PostMapping("/doctors/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDoctor(@PathVariable Long id) {
        doctorService.changeDoctorStatus(id, "SD1", "SD2");
        return ResponseEntity.ok(ApiResponse.success("Duyệt bác sĩ thành công (SD1 -> SD2)", null));
    }

    @PostMapping("/doctors/{id}/review/approve")
    public ResponseEntity<ApiResponse<ApprovalRequestResponse>> approveDoctorReview(
            @PathVariable Long id,
            @RequestParam(required = false) String reviewNote,
            Authentication authentication
    ) {
        String reviewerEmail = authentication != null ? authentication.getName() : null;
        ApprovalRequestResponse response = approvalRequestService.reviewDoctorRequest(id, reviewerEmail, true, reviewNote);
        return ResponseEntity.ok(ApiResponse.success("Phe duyet bac si thanh cong", response));
    }

    @PostMapping("/doctors/{id}/review/reject")
    public ResponseEntity<ApiResponse<ApprovalRequestResponse>> rejectDoctorReview(
            @PathVariable Long id,
            @RequestParam String reviewNote,
            Authentication authentication
    ) {
        String reviewerEmail = authentication != null ? authentication.getName() : null;
        ApprovalRequestResponse response = approvalRequestService.reviewDoctorRequest(id, reviewerEmail, false, reviewNote);
        return ResponseEntity.ok(ApiResponse.success("Tu choi bac si thanh cong", response));
    }

    /**
     * SD2 (Active) -> SD3 (Inactive)
     * Chuyển bác sĩ sang trạng thái ngưng hoạt động.
     */
    @PostMapping("/doctors/{id}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateDoctor(@PathVariable Long id) {
        doctorService.changeDoctorStatus(id, "SD2", "SD3");
        return ResponseEntity.ok(ApiResponse.success("Ngưng hoạt động bác sĩ thành công (SD2 -> SD3)", null));
    }

    /**
     * SD3 (Inactive) -> SD4 (On Leave)
     * Chuyển bác sĩ sang trạng thái nghỉ phép.
     */
    @PostMapping("/doctors/{id}/on-leave")
    public ResponseEntity<ApiResponse<Void>> setDoctorOnLeave(@PathVariable Long id) {
        doctorService.changeDoctorStatus(id, "SD3", "SD4");
        return ResponseEntity.ok(ApiResponse.success("Chuyển bác sĩ sang nghỉ phép thành công (SD3 -> SD4)", null));
    }

    @PatchMapping("/doctors/{id}/status")
    public ResponseEntity<ApiResponse<Void>> setDoctorStatus(
            @PathVariable Long id,
            @RequestParam String statusId
    ) {
        if (!MANAGER_DOCTOR_STATUSES.contains(statusId)) {
            return ResponseEntity.badRequest().body(ApiResponse.<Void>error("Trang thai bac si khong hop le"));
        }

        doctorService.setDoctorStatus(id, statusId);
        return ResponseEntity.ok(ApiResponse.success("Cap nhat trang thai bac si thanh cong", null));
    }

    @GetMapping("/clinics/{clinicId}/packages")
    public ResponseEntity<ApiResponse<List<PackageResponse>>> getPackagesByClinicId(@PathVariable Long clinicId) {
        return ResponseEntity.ok(ApiResponse.success("OK", packageService.getPackagesByClinicId(clinicId)));
    }

    @PostMapping("/packages/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approvePackage(@PathVariable Long id) {
        packageService.changePackageStatus(id, "SD1", "SD2");
        return ResponseEntity.ok(ApiResponse.success("Duyet goi kham thanh cong (SD1 -> SD2)", null));
    }
}
