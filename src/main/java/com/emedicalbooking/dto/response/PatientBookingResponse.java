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
    private Long id;
    private String statusId;
    private Long doctorId;
    private Long patientId;
    private String date;
    private String timeType;
    private String token;
    private String birthday;
    private String reason;
    private PatientData patientData;
    private AllCodeResponse bookingTimeTypeData;

    /** Dữ liệu bệnh nhân được đặt hộ (null nếu tự đặt cho mình) */
    private ProfileData profileData;

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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileData {
        private Long id;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String gender;
        private String dateOfBirth;
        private String address;
        private String relationship;
        private String medicalHistory;
    }
}
