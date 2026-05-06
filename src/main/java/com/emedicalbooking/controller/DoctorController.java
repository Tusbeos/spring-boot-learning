package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.BulkCreateDoctorServicesRequest;
import com.emedicalbooking.dto.request.BulkCreateScheduleRequest;
import com.emedicalbooking.dto.request.SaveDoctorInfoRequest;
import com.emedicalbooking.dto.response.*;
import com.emedicalbooking.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;

    @GetMapping("/top")
    public ResponseEntity<ApiResponse<List<DoctorListResponse>>> getTopDoctors(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getTopDoctors(limit)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<DoctorListResponse>>> getAllDoctors() {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getAllDoctors()));
    }

    @GetMapping("/paginated")
    public ResponseEntity<ApiResponse<DoctorPageResponse>> getDoctorsPaginated(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String clinic) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getDoctorsPaginated(page, limit, search, specialty, clinic)));
    }

    @PostMapping("/{doctorId}/info")
    public ResponseEntity<ApiResponse<Void>> saveDoctorInfo(
            @PathVariable Long doctorId,
            @Valid @RequestBody SaveDoctorInfoRequest request) {
        doctorService.saveDoctorInfo(doctorId, request);
        return ResponseEntity.ok(ApiResponse.success("Lưu thông tin bác sĩ thành công", null));
    }

    @GetMapping("/{doctorId}")
    public ResponseEntity<ApiResponse<DoctorDetailResponse>> getDoctorDetail(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getDoctorDetail(doctorId)));
    }

    @PostMapping("/{doctorId}/schedules")
    public ResponseEntity<ApiResponse<Void>> bulkCreateSchedule(
            @PathVariable Long doctorId,
            @Valid @RequestBody BulkCreateScheduleRequest request) {
        doctorService.bulkCreateSchedule(doctorId, request);
        return ResponseEntity.ok(ApiResponse.success("Tạo lịch khám thành công", null));
    }

    @GetMapping("/{doctorId}/schedules")
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getScheduleByDate(
            @PathVariable Long doctorId,
            @RequestParam String date) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getScheduleByDate(doctorId, date)));
    }

    @PostMapping("/{doctorId}/services")
    public ResponseEntity<ApiResponse<Void>> bulkCreateDoctorServices(
            @PathVariable Long doctorId,
            @Valid @RequestBody BulkCreateDoctorServicesRequest request) {
        doctorService.bulkCreateDoctorServices(doctorId, request);
        return ResponseEntity.ok(ApiResponse.success("Tạo dịch vụ bác sĩ thành công", null));
    }

    @GetMapping("/{doctorId}/services")
    public ResponseEntity<ApiResponse<List<DoctorServiceResponse>>> getDoctorServices(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getDoctorServices(doctorId)));
    }

    @GetMapping("/{doctorId}/extra-info")
    public ResponseEntity<ApiResponse<DoctorExtraInfoResponse>> getExtraInfo(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getExtraInfo(doctorId)));
    }

    @GetMapping("/{doctorId}/specialties")
    public ResponseEntity<ApiResponse<List<Long>>> getSpecialtiesByDoctorId(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getSpecialtiesByDoctorId(doctorId)));
    }

    @GetMapping("/{doctorId}/patients")
    public ResponseEntity<ApiResponse<List<PatientBookingResponse>>> getPatientsByDoctor(
            @PathVariable Long doctorId,
            @RequestParam String date) {
        return ResponseEntity.ok(ApiResponse.success("OK", doctorService.getPatientsByDoctorAndDate(doctorId, date)));
    }
}
