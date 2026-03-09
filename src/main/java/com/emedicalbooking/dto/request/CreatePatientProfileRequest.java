package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePatientProfileRequest {

    @NotBlank(message = "Họ không được để trống")
    private String firstName;

    @NotBlank(message = "Tên không được để trống")
    private String lastName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phoneNumber;

    private String gender;
    private String dateOfBirth;
    private String address;

    @NotBlank(message = "Mối quan hệ không được để trống")
    private String relationship;

    private String medicalHistory;
}
