package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreatePackageRequest {

    @NotBlank(message = "Tên gói khám không được để trống")
    private String name;

    @NotBlank(message = "Loại gói khám không được để trống")
    private String typeCode;

    @NotNull(message = "Phòng khám không được để trống")
    private Long clinicId;

    @Min(value = 0, message = "Giá không được âm")
    private int price;

    private String note;

    private String statusId;

    private String imageBase64;

    private String descriptionHTML;

    private String descriptionMarkdown;

    private List<PackageServiceItemRequest> packageServices;
}
