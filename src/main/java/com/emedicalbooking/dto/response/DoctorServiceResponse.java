package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorServiceResponse {
    private String nameVi;
    private String nameEn;
    private String price;
    private String descriptionVi;
    private String descriptionEn;
}
