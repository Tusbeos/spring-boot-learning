package com.emedicalbooking.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoctorDetailResponse {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String image; // base64
    private AllCodeResponse positionData;
    private AllCodeResponse roleData;

    @JsonProperty("Markdown")
    private MarkdownData markdown;

    @JsonProperty("DoctorInfo")
    private DoctorInfoData doctorInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MarkdownData {
        private String description;
        private String contentHTML;
        private String contentMarkdown;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DoctorInfoData {
        private String priceId;
        private String paymentId;
        private String provinceId;
        private String nameClinic;
        private String addressClinic;
        private String note;
        private Integer clinicId;
        private java.util.List<Integer> specialtyIds;
        private AllCodeResponse priceTypeData;
        private AllCodeResponse provinceTypeData;
        private AllCodeResponse paymentTypeData;
    }
}
