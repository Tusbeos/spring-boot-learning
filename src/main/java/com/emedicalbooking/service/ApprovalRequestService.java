package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreateApprovalRequest;
import com.emedicalbooking.dto.response.ApprovalRequestResponse;

import java.util.List;

public interface ApprovalRequestService {

    ApprovalRequestResponse createApprovalRequest(CreateApprovalRequest request);

    List<ApprovalRequestResponse> getPendingRequestsByClinic(Long clinicId);

    ApprovalRequestResponse approveRequest(Long requestId, Long reviewerId, String reviewNote);

    ApprovalRequestResponse rejectRequest(Long requestId, Long reviewerId, String reviewNote);

    ApprovalRequestResponse reviewDoctorRequest(Long doctorId, String reviewerEmail, boolean approved, String reviewNote);
}
