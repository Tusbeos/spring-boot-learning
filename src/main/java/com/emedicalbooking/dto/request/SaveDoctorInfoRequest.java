package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SaveDoctorInfoRequest {

    @NotBlank(message = "contentHTML không được để trống")
    private String contentHTML;

    @NotBlank(message = "contentMarkdown không được để trống")
    private String contentMarkdown;

    @NotBlank(message = "action không được để trống")
    private String action; // CREATE hoặc EDIT

    @NotBlank(message = "selectedPrice không được để trống")
    private String selectedPrice;

    @NotBlank(message = "selectedPayment không được để trống")
    private String selectedPayment;

    @NotBlank(message = "selectedProvince không được để trống")
    private String selectedProvince;

    @NotBlank(message = "nameClinic không được để trống")
    private String nameClinic;

    @NotBlank(message = "addressClinic không được để trống")
    private String addressClinic;

    @NotBlank(message = "note không được để trống")
    private String note;

    @NotNull(message = "clinicId không được để trống")
    private Long clinicId;

    @NotNull(message = "specialtyIds không được để trống")
    private List<Long> specialtyIds;

    private String description;
}
