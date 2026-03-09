package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClinicResponse {
    private int id;
    private String name;
    private String address;
    private String image; // base64
    private String imageCover; // base64
    private String descriptionHTML;
    private String descriptionMarkdown;
}
