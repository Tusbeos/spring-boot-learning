package com.emedicalbooking.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class HistoryResponse {

    private Long id;

    // Thông tin buổi khám (từ Booking)
    private Long bookingId;
    private String bookingDate;        // booking.date (timestamp string từ FE)
    private String timeType;           // keyMap của khung giờ
    private String timeTypeValueVi;    // tên khung giờ tiếng Việt
    private String timeTypeValueEn;

    // Thông tin bác sĩ
    private Long doctorId;
    private String doctorFirstName;
    private String doctorLastName;

    // Thông tin bệnh nhân
    private Long patientId;
    private String patientFirstName;
    private String patientLastName;
    private String patientEmail;

    // Lý do bệnh nhân đặt lịch
    private String reason;

    // Kết quả khám (do bác sĩ ghi)
    private String diagnosis;
    private String prescription;
    private String notes;
    private LocalDate examinationDate;

    // Thông tin người được đặt hộ (nếu đặt cho người thân, ngược lại null)
    private Long profileId;
    private String profileFirstName;
    private String profileLastName;
    private String profileRelationship;

    private LocalDateTime createdAt;
}
