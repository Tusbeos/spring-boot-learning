package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.statusData " +
           "LEFT JOIN FETCH b.doctor " +
           "LEFT JOIN FETCH b.patient " +
           "LEFT JOIN FETCH b.timeTypeData " +
           "LEFT JOIN FETCH b.patientProfile " +
           "WHERE b.doctor.id = :doctorId AND b.date = :date " +
           "AND b.statusData.keyMap IN ('S1', 'S2', 'S3')")
    List<Booking> findByDoctorAndDate(@Param("doctorId") Long doctorId, @Param("date") String date);

    @Query("SELECT b FROM Booking b WHERE b.patient.id = :patientId AND b.doctor.id = :doctorId " +
           "AND b.date = :date AND b.timeTypeData.keyMap = :timeType")
    Optional<Booking> findByPatientAndDoctorAndDateAndTimeType(
            @Param("patientId") Long patientId,
            @Param("doctorId") Long doctorId,
            @Param("date") String date,
            @Param("timeType") String timeType);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.statusData WHERE b.token = :token AND b.doctor.id = :doctorId AND b.statusData.keyMap = 'S1'")
    Optional<Booking> findByTokenAndDoctorId(@Param("token") String token, @Param("doctorId") Long doctorId);

    /** Lấy tất cả lịch hẽn S1 đã quá thời gian xác nhận — dùng cho scheduled cleanup */
    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.timeTypeData " +
           "LEFT JOIN FETCH b.doctor " +
           "WHERE b.statusData.keyMap = 'S1' AND b.tokenExpiry < :now")
    List<Booking> findExpiredUnconfirmedBookings(@Param("now") java.time.LocalDateTime now);
}
