package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateUserRequest {

    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;

    @NotBlank(message = "Gender không được để trống")
    private String gender;

    @NotBlank(message = "RoleId không được để trống")
    private String roleId;

    @NotBlank(message = "PositionId không được để trống")
    private String positionId;

    private String image; // base64
}
