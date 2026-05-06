package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.CreateApprovalRequest;
import com.emedicalbooking.dto.response.AllCodeResponse;
import com.emedicalbooking.dto.response.ApprovalRequestResponse;
import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.entity.ApprovalRequest;
import com.emedicalbooking.entity.Clinic;
import com.emedicalbooking.entity.DoctorInfos;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.AllCodeRepository;
import com.emedicalbooking.repository.ApprovalRequestRepository;
import com.emedicalbooking.repository.ClinicRepository;
import com.emedicalbooking.repository.DoctorInfosRepository;
import com.emedicalbooking.repository.UserRepository;
import com.emedicalbooking.service.ApprovalRequestService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApprovalRequestServiceImpl implements ApprovalRequestService {

    private static final String STATUS_PENDING = "AR1";
    private static final String STATUS_APPROVED = "AR2";
    private static final String STATUS_REJECTED = "AR3";
    private static final String DOCTOR_STATUS_PENDING = "SD1";
    private static final String DOCTOR_STATUS_ACTIVE = "SD2";
    private static final String DOCTOR_STATUS_LOCKED = "SD5";

    private final ApprovalRequestRepository approvalRequestRepository;
    private final AllCodeRepository allCodeRepository;
    private final ClinicRepository clinicRepository;
    private final UserRepository userRepository;
    private final DoctorInfosRepository doctorInfosRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ApprovalRequestResponse createApprovalRequest(CreateApprovalRequest request) {
        AllCode pendingStatus = findAllCode(STATUS_PENDING);
        Clinic clinic = request.getClinicId() == null ? null : clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", request.getClinicId()));
        User requester = request.getRequesterId() == null ? null : userRepository.findById(request.getRequesterId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getRequesterId()));

        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .requestCode(generateRequestCode())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .clinic(clinic)
                .requester(requester)
                .statusData(pendingStatus)
                .title(request.getTitle())
                .description(request.getDescription())
                .payloadJson(request.getPayloadJson())
                .submittedAt(LocalDateTime.now())
                .build();

        return toResponse(approvalRequestRepository.save(approvalRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ApprovalRequestResponse> getPendingRequestsByClinic(Long clinicId) {
        return approvalRequestRepository.findByClinic_IdAndStatusData_KeyMap(clinicId, STATUS_PENDING)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ApprovalRequestResponse approveRequest(Long requestId, Long reviewerId, String reviewNote) {
        return reviewRequest(requestId, reviewerId, reviewNote, STATUS_APPROVED);
    }

    @Override
    @Transactional
    public ApprovalRequestResponse rejectRequest(Long requestId, Long reviewerId, String reviewNote) {
        return reviewRequest(requestId, reviewerId, reviewNote, STATUS_REJECTED);
    }

    @Override
    @Transactional
    public ApprovalRequestResponse reviewDoctorRequest(Long doctorId, String reviewerEmail, boolean approved, String reviewNote) {
        if (!approved && (reviewNote == null || reviewNote.trim().isEmpty())) {
            throw new IllegalArgumentException("Ly do tu choi khong duoc de trong");
        }

        DoctorInfos doctorInfos = doctorInfosRepository.findFirstByDoctorId(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorInfos", "doctorId", doctorId));

        String currentStatus = doctorInfos.getStatusData() != null ? doctorInfos.getStatusData().getKeyMap() : DOCTOR_STATUS_PENDING;
        if (!DOCTOR_STATUS_PENDING.equals(currentStatus)) {
            throw new IllegalArgumentException("Bac si khong con o trang thai cho duyet");
        }

        User reviewer = reviewerEmail == null || reviewerEmail.isBlank()
                ? null
                : userRepository.findByEmailWithRole(reviewerEmail).orElse(null);
        User doctor = doctorInfos.getDoctor();
        Clinic clinic = doctorInfos.getClinic() != null ? doctorInfos.getClinic() : (doctor != null ? doctor.getClinic() : null);
        LocalDateTime now = LocalDateTime.now();
        String approvalStatus = approved ? STATUS_APPROVED : STATUS_REJECTED;
        String doctorStatus = approved ? DOCTOR_STATUS_ACTIVE : DOCTOR_STATUS_LOCKED;

        doctorInfos.setStatusData(findAllCode(doctorStatus));
        doctorInfosRepository.save(doctorInfos);

        ApprovalRequest approvalRequest = ApprovalRequest.builder()
                .requestCode(generateRequestCode())
                .targetType("DOCTOR")
                .targetId(doctorId)
                .clinic(clinic)
                .requester(doctor)
                .reviewer(reviewer)
                .statusData(findAllCode(approvalStatus))
                .title((approved ? "Phe duyet bac si " : "Tu choi bac si ") + toUserName(doctor))
                .description("Yeu cau phe duyet bac si tai co so y te")
                .payloadJson(toDoctorPayloadJson(doctorInfos, currentStatus))
                .reviewNote(reviewNote)
                .submittedAt(doctorInfos.getCreatedAt() != null ? doctorInfos.getCreatedAt() : now)
                .reviewedAt(now)
                .build();

        return toResponse(approvalRequestRepository.save(approvalRequest));
    }

    private ApprovalRequestResponse reviewRequest(Long requestId, Long reviewerId, String reviewNote, String statusKey) {
        ApprovalRequest approvalRequest = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalRequest", "id", requestId));
        User reviewer = reviewerId == null ? null : userRepository.findById(reviewerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", reviewerId));

        approvalRequest.setReviewer(reviewer);
        approvalRequest.setStatusData(findAllCode(statusKey));
        approvalRequest.setReviewNote(reviewNote);
        approvalRequest.setReviewedAt(LocalDateTime.now());

        return toResponse(approvalRequestRepository.save(approvalRequest));
    }

    private String generateRequestCode() {
        String prefix = "REQ-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        for (int i = 0; i < 10; i++) {
            String requestCode = prefix + String.format("%06d", System.currentTimeMillis() % 1_000_000);
            if (!approvalRequestRepository.existsByRequestCode(requestCode)) {
                return requestCode;
            }
        }
        return prefix + System.nanoTime();
    }

    private AllCode findAllCode(String keyMap) {
        return allCodeRepository.findByKeyMap(keyMap)
                .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", keyMap));
    }

    private String toDoctorPayloadJson(DoctorInfos doctorInfos, String previousStatus) {
        Map<String, Object> payload = new LinkedHashMap<>();
        User doctor = doctorInfos.getDoctor();
        payload.put("doctorId", doctor != null ? doctor.getId() : null);
        payload.put("doctorName", toUserName(doctor));
        payload.put("email", doctor != null ? doctor.getEmail() : null);
        payload.put("clinicId", doctorInfos.getClinic() != null ? doctorInfos.getClinic().getId() : null);
        payload.put("clinicName", doctorInfos.getClinic() != null ? doctorInfos.getClinic().getName() : doctorInfos.getNameClinic());
        payload.put("specialtyId", doctorInfos.getSpecialty() != null ? doctorInfos.getSpecialty().getId() : null);
        payload.put("specialtyName", doctorInfos.getSpecialty() != null ? doctorInfos.getSpecialty().getName() : null);
        payload.put("previousStatusId", previousStatus);

        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private ApprovalRequestResponse toResponse(ApprovalRequest request) {
        return ApprovalRequestResponse.builder()
                .id(request.getId())
                .requestCode(request.getRequestCode())
                .targetType(request.getTargetType())
                .targetId(request.getTargetId())
                .clinicId(request.getClinic() != null ? request.getClinic().getId() : null)
                .clinicName(request.getClinic() != null ? request.getClinic().getName() : null)
                .requesterId(request.getRequester() != null ? request.getRequester().getId() : null)
                .requesterName(toUserName(request.getRequester()))
                .reviewerId(request.getReviewer() != null ? request.getReviewer().getId() : null)
                .reviewerName(toUserName(request.getReviewer()))
                .statusId(request.getStatusData() != null ? request.getStatusData().getKeyMap() : null)
                .statusData(toAllCodeResponse(request.getStatusData()))
                .title(request.getTitle())
                .description(request.getDescription())
                .payloadJson(request.getPayloadJson())
                .reviewNote(request.getReviewNote())
                .submittedAt(request.getSubmittedAt())
                .reviewedAt(request.getReviewedAt())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }

    private String toUserName(User user) {
        if (user == null) {
            return null;
        }
        String fullName = ((user.getLastName() == null ? "" : user.getLastName()) + " " +
                (user.getFirstName() == null ? "" : user.getFirstName())).trim();
        return fullName.isEmpty() ? user.getEmail() : fullName;
    }

    private AllCodeResponse toAllCodeResponse(AllCode allCode) {
        if (allCode == null) {
            return null;
        }

        return AllCodeResponse.builder()
                .keyMap(allCode.getKeyMap())
                .valueEn(allCode.getValueEn())
                .valueVi(allCode.getValueVi())
                .build();
    }
}
