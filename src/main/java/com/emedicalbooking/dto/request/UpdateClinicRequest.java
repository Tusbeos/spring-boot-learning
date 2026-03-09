package com.emedicalbooking.dto.request;

import lombok.Data;

@Data
public class UpdateClinicRequest {
    private String name;
    private String address;
    private String descriptionHTML;
    private String descriptionMarkdown;
    private String imageBase64;
    private String imageCoverBase64;
}
