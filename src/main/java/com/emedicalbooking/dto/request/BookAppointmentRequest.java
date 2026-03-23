package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookAppointmentRequest {

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotNull(message = "doctorId không được để trống")
    private Long doctorId;

    @NotBlank(message = "date không được để trống")
    private String date;

    @NotBlank(message = "timeType không được để trống")
    private String timeType;

    @NotBlank(message = "timeString không được để trống")
    private String timeString;

    @NotBlank(message = "doctorName không được để trống")
    private String doctorName;

    @NotBlank(message = "language không được để trống")
    private String language;

    private String fullName;
    private String firstName;
    private String lastName;
    private String gender;
    private String phoneNumber;
    private String address;
    private String birthday;
    private String reason;

    // --- Đặt hộ cho người khác ---
    /** true = đặt cho người khác, false hoặc null = đặt cho chính mình */
    private Boolean isForOther;

    /** Họ của bệnh nhân được đặt hộ */
    private String profileFirstName;

    /** Tên của bệnh nhân được đặt hộ */
    private String profileLastName;

    /** Số điện thoại của bệnh nhân được đặt hộ */
    private String profilePhoneNumber;

    /** Giới tính của bệnh nhân được đặt hộ */
    private String profileGender;

    /** Ngày sinh của bệnh nhân được đặt hộ */
    private String profileDateOfBirth;

    /** Địa chỉ của bệnh nhân được đặt hộ */
    private String profileAddress;

    /** Mối quan hệ: cha, mẹ, con, vợ, chồng... */
    private String relationship;

    /** Tiền sử bệnh của bệnh nhân được đặt hộ */
    private String medicalHistory;
}
