package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClinicRequest {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;

    @NotBlank(message = "Ảnh logo không được để trống")
    private String imageBase64;

    @NotBlank(message = "Ảnh bìa không được để trống")
    private String imageCoverBase64;

    @NotBlank(message = "descriptionHTML không được để trống")
    private String descriptionHTML;

    @NotBlank(message = "descriptionMarkdown không được để trống")
    private String descriptionMarkdown;
}
