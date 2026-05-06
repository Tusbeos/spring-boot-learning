package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.CreateApprovalRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.dto.response.ApprovalRequestResponse;
import com.emedicalbooking.service.ApprovalRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/approval-requests")
@RequiredArgsConstructor
public class ApprovalRequestController {

    private final ApprovalRequestService approvalRequestService;

    @PostMapping
    public ResponseEntity<ApiResponse<ApprovalRequestResponse>> createApprovalRequest(
            @Valid @RequestBody CreateApprovalRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Tao yeu cau phe duyet thanh cong", approvalRequestService.createApprovalRequest(request)));
    }

    @GetMapping("/clinic/{clinicId}/pending")
    public ResponseEntity<ApiResponse<List<ApprovalRequestResponse>>> getPendingRequestsByClinic(@PathVariable Long clinicId) {
        return ResponseEntity.ok(ApiResponse.success("OK", approvalRequestService.getPendingRequestsByClinic(clinicId)));
    }

    @PostMapping("/{requestId}/approve")
    public ResponseEntity<ApiResponse<ApprovalRequestResponse>> approveRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) Long reviewerId,
            @RequestParam(required = false) String reviewNote
    ) {
        return ResponseEntity.ok(ApiResponse.success("Phe duyet yeu cau thanh cong", approvalRequestService.approveRequest(requestId, reviewerId, reviewNote)));
    }

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<ApiResponse<ApprovalRequestResponse>> rejectRequest(
            @PathVariable Long requestId,
            @RequestParam(required = false) Long reviewerId,
            @RequestParam String reviewNote
    ) {
        return ResponseEntity.ok(ApiResponse.success("Tu choi yeu cau thanh cong", approvalRequestService.rejectRequest(requestId, reviewerId, reviewNote)));
    }
}
