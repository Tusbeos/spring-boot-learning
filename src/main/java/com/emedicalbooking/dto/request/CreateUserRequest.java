package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateUserRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được để trống")
    private String password;

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
