package com.emedicalbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String gender;
    private String dateOfBirth;
    private String address;
    private String relationship;
    private String medicalHistory;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
