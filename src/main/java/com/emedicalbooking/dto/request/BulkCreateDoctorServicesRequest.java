package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class BulkCreateDoctorServicesRequest {

    @NotNull(message = "arrDoctorService không được để trống")
    private List<DoctorServiceItem> arrDoctorService;

    @Data
    public static class DoctorServiceItem {
        private String nameVi;
        private String nameEn;
        private String price;
        private String descriptionVi;
        private String descriptionEn;
    }
}
