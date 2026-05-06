package com.emedicalbooking.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String gender;
    private String roleId;
    private String positionId;
    private Long clinicId;
    private String avatar; // base64
}
