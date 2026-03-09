package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VerifyBookingRequest {

    @NotBlank(message = "Token không được để trống")
    private String token;

    @NotNull(message = "doctorId không được để trống")
    private Integer doctorId;
}
