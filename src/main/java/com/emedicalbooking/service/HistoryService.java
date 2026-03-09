package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.CreateHistoryRequest;
import com.emedicalbooking.dto.response.HistoryResponse;

import java.util.List;

public interface HistoryService {

    /** Bác sĩ tạo hồ sơ bệnh sau khi kết thúc khám (booking status = S3) */
    HistoryResponse createHistory(CreateHistoryRequest request);

    /** Lấy toàn bộ lịch sử khám của 1 bệnh nhân */
    List<HistoryResponse> getHistoryByPatient(int patientId);

    /** Lấy toàn bộ lịch sử khám do 1 bác sĩ thực hiện */
    List<HistoryResponse> getHistoryByDoctor(int doctorId);
}
