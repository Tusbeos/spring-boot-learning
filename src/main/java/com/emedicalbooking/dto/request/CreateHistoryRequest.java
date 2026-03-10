package com.emedicalbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateHistoryRequest {

    @NotNull(message = "bookingId không được để trống")
    private Integer bookingId;

    private String diagnosis;         // Chẩn đoán
    private String prescription;      // Đơn thuốc / phác đồ
    private String notes;             // Ghi chú thêm
    private LocalDate examinationDate; // Ngày khám (mặc định: hôm nay)
}
