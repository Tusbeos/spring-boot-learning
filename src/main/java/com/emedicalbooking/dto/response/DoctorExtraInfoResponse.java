package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorExtraInfoResponse {
    private String priceId;
    private String paymentId;
    private String provinceId;
    private String nameClinic;
    private String addressClinic;
    private String note;
    private Long clinicId;
    private int count;
    private AllCodeResponse priceTypeData;
    private AllCodeResponse provinceTypeData;
    private AllCodeResponse paymentTypeData;
}
