package com.emedicalbooking.repository;

import com.emedicalbooking.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {

    // Lấy lịch sử khám của 1 bệnh nhân (qua booking.patient.id), sắp xếp theo thời gian tạo mới nhất
    @Query("SELECT h FROM History h " +
           "JOIN FETCH h.booking b " +
           "JOIN FETCH b.patient " +
           "JOIN FETCH b.doctor " +
           "LEFT JOIN FETCH b.timeTypeData " +
           "LEFT JOIN FETCH b.patientProfile " +
           "WHERE b.patient.id = :patientId " +
           "ORDER BY h.createdAt DESC")
    List<History> findByPatientId(@Param("patientId") Long patientId);

    // Lấy tất cả lịch sử khám của 1 bác sĩ, sắp xếp theo thời gian tạo mới nhất
    @Query("SELECT h FROM History h " +
           "JOIN FETCH h.booking b " +
           "JOIN FETCH b.patient " +
           "JOIN FETCH b.doctor " +
           "LEFT JOIN FETCH b.timeTypeData " +
           "WHERE b.doctor.id = :doctorId " +
           "ORDER BY h.createdAt DESC")
    List<History> findByDoctorId(@Param("doctorId") Long doctorId);

    // Kiểm tra 1 booking đã có history chưa
    Optional<History> findByBookingId(Long bookingId);
}
