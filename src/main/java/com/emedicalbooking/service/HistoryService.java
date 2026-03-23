package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreateHistoryRequest;
import com.emedicalbooking.dto.response.HistoryResponse;

import java.util.List;
import java.util.Optional;

public interface HistoryService {

    /** Bác sĩ tạo hồ sơ bệnh sau khi kết thúc khám (booking status = S3) */
    HistoryResponse createHistory(CreateHistoryRequest request);

    /** Lấy toàn bộ lịch sử khám của 1 bệnh nhân */
    List<HistoryResponse> getHistoryByPatient(Long patientId);

    /** Lấy toàn bộ lịch sử khám do 1 bác sĩ thực hiện */
    List<HistoryResponse> getHistoryByDoctor(Long doctorId);

    /** Lấy hồ sơ khám của 1 booking cụ thể (dùng để pre-fill form kê đơn) */
    Optional<HistoryResponse> getHistoryByBooking(Long bookingId);
}
