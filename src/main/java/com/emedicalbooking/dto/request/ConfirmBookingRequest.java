package com.emedicalbooking.dto.request;

import lombok.Data;

@Data
public class ConfirmBookingRequest {

    private Long doctorId;
    private String statusId; // default "S3"
}
