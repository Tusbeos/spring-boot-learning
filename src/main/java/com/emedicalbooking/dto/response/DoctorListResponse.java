package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorListResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String image; // base64
    private AllCodeResponse positionData;
    private AllCodeResponse genderData;
    private int count; // số lượt đặt — dùng cho TopDoctor
}
