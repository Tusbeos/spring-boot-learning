package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecialtyResponse {
    private Long id;
    private String name;
    private String descriptionHTML;
    private String descriptionMarkdown;
    private String image; // base64
}
