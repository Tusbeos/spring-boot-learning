package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateSpecialtyRequest {

    @NotBlank(message = "Tên không được để trống")
    private String name;

    @NotBlank(message = "descriptionHTML không được để trống")
    private String descriptionHTML;

    @NotBlank(message = "descriptionMarkdown không được để trống")
    private String descriptionMarkdown;

    @NotBlank(message = "Ảnh không được để trống")
    private String imageBase64;
}
