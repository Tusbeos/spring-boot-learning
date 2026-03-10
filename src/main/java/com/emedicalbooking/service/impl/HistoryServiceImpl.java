package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.CreateHistoryRequest;
import com.emedicalbooking.dto.response.HistoryResponse;
import com.emedicalbooking.entity.Booking;
import com.emedicalbooking.entity.History;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.BookingRepository;
import com.emedicalbooking.repository.HistoryRepository;
import com.emedicalbooking.service.HistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoryServiceImpl implements HistoryService {

    private final HistoryRepository historyRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public HistoryResponse createHistory(CreateHistoryRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", request.getBookingId()));

        // Nếu booking đã có history thì cập nhật thay vì tạo mới
        History history = historyRepository.findByBookingId(request.getBookingId())
                .orElse(History.builder().booking(booking).build());

        history.setDiagnosis(request.getDiagnosis());
        history.setPrescription(request.getPrescription());
        history.setNotes(request.getNotes());
        history.setExaminationDate(
            request.getExaminationDate() != null
                ? request.getExaminationDate()
                : java.time.LocalDate.now()
        );

        return toResponse(historyRepository.save(history));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryResponse> getHistoryByPatient(int patientId) {
        return historyRepository.findByPatientId(patientId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistoryResponse> getHistoryByDoctor(int doctorId) {
        return historyRepository.findByDoctorId(doctorId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<HistoryResponse> getHistoryByBooking(int bookingId) {
        return historyRepository.findByBookingId(bookingId)
                .map(this::toResponse);
    }

    // Chuyển History entity → HistoryResponse DTO
    private HistoryResponse toResponse(History h) {
        Booking b = h.getBooking();
        HistoryResponse.HistoryResponseBuilder builder = HistoryResponse.builder()
                .id(h.getId())
                .bookingId(b.getId())
                .bookingDate(b.getDate())
                .reason(b.getReason())
                .diagnosis(h.getDiagnosis())
                .prescription(h.getPrescription())
                .notes(h.getNotes())
                .examinationDate(h.getExaminationDate())
                .createdAt(h.getCreatedAt());

        if (b.getTimeTypeData() != null) {
            builder.timeType(b.getTimeTypeData().getKeyMap())
                   .timeTypeValueVi(b.getTimeTypeData().getValueVi())
                   .timeTypeValueEn(b.getTimeTypeData().getValueEn());
        }
        if (b.getDoctor() != null) {
            builder.doctorId(b.getDoctor().getId())
                   .doctorFirstName(b.getDoctor().getFirstName())
                   .doctorLastName(b.getDoctor().getLastName());
        }
        if (b.getPatient() != null) {
            builder.patientId(b.getPatient().getId())
                   .patientFirstName(b.getPatient().getFirstName())
                   .patientLastName(b.getPatient().getLastName())
                   .patientEmail(b.getPatient().getEmail());
        }
        // Nếu booking đặt cho người thân thì đính kèm thông tin profile
        if (b.getPatientProfile() != null) {
            var p = b.getPatientProfile();
            builder.profileId(p.getId())
                   .profileFirstName(p.getFirstName())
                   .profileLastName(p.getLastName())
                   .profileRelationship(p.getRelationship());
        }
        return builder.build();
    }
}
