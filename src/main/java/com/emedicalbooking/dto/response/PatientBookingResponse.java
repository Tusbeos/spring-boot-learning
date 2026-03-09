package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientBookingResponse {
    private int id;
    private String statusId;
    private int doctorId;
    private int patientId;
    private String date;
    private String timeType;
    private String token;
    private String birthday;
    private String reason;
    private PatientData patientData;
    private AllCodeResponse bookingTimeTypeData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PatientData {
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
    }
}
