package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestResponse {
    private Long id;
    private String requestCode;
    private String targetType;
    private Long targetId;
    private Long clinicId;
    private String clinicName;
    private Long requesterId;
    private String requesterName;
    private Long reviewerId;
    private String reviewerName;
    private String statusId;
    private AllCodeResponse statusData;
    private String title;
    private String description;
    private String payloadJson;
    private String reviewNote;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
