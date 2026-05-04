package com.emedicalbooking.controller;

import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clinic-manager")
@RequiredArgsConstructor
public class ClinicManagerController {

    private final DoctorService doctorService;

    /**
     * SD1 (Pending) -> SD2 (Active)
     * Duyệt bác sĩ mới đăng ký sang trạng thái hoạt động.
     */
    @PostMapping("/doctors/{id}/approve")
    public ResponseEntity<ApiResponse<Void>> approveDoctor(@PathVariable Long id) {
        doctorService.changeDoctorStatus(id, "SD1", "SD2");
        return ResponseEntity.ok(ApiResponse.success("Duyệt bác sĩ thành công (SD1 -> SD2)", null));
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
}
