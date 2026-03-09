package com.emedicalbooking.dto.request;

import lombok.Data;

@Data
public class UpdateSpecialtyRequest {
    private String name;
    private String descriptionHTML;
    private String descriptionMarkdown;
    private String imageBase64;
}
