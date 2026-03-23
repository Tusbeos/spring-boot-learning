package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.CreateHistoryRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.HistoryResponse;
import com.emedicalbooking.service.HistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    /**
     * POST /api/histories
     * Bác sĩ tạo / cập nhật hồ sơ khám sau khi xác nhận bệnh nhân (S3).
     * Body: { bookingId, diagnosis, prescription, notes }
     */
    @PostMapping
    public ResponseEntity<ApiResponse<HistoryResponse>> createHistory(
            @Valid @RequestBody CreateHistoryRequest request) {
        HistoryResponse response = historyService.createHistory(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Lưu hồ sơ khám thành công", response));
    }

    /**
     * GET /api/histories/patient/{patientId}
     * Lấy toàn bộ lịch sử khám của bệnh nhân (patient hoặc doctor hoặc admin).
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<HistoryResponse>>> getByPatient(
            @PathVariable Long patientId) {
        List<HistoryResponse> list = historyService.getHistoryByPatient(patientId);
        return ResponseEntity.ok(ApiResponse.success(null, list));
    }

    /**
     * GET /api/histories/doctor/{doctorId}
     * Lấy danh sách hồ sơ khám do bác sĩ thực hiện.
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<ApiResponse<List<HistoryResponse>>> getByDoctor(
            @PathVariable Long doctorId) {
        List<HistoryResponse> list = historyService.getHistoryByDoctor(doctorId);
        return ResponseEntity.ok(ApiResponse.success(null, list));
    }

    /**
     * GET /api/histories/booking/{bookingId}
     * Lấy hồ sơ khám của 1 lịch hẹn cụ thể (dùng để pre-fill form kê đơn).
     */
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<ApiResponse<HistoryResponse>> getByBooking(
            @PathVariable Long bookingId) {
        HistoryResponse data = historyService.getHistoryByBooking(bookingId).orElse(null);
        return ResponseEntity.ok(ApiResponse.success(null, data));
    }
}
