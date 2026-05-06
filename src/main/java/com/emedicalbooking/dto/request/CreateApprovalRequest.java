package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateApprovalRequest {

    @NotBlank(message = "Loai doi tuong phe duyet khong duoc de trong")
    private String targetType;

    @NotNull(message = "ID doi tuong phe duyet khong duoc de trong")
    private Long targetId;

    private Long clinicId;

    private Long requesterId;

    @NotBlank(message = "Tieu de yeu cau khong duoc de trong")
    private String title;

    private String description;

    private String payloadJson;
}
