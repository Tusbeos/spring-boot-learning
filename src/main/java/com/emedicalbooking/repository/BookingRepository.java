package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b " +
           "LEFT JOIN FETCH b.statusData " +
           "LEFT JOIN FETCH b.doctor " +
           "LEFT JOIN FETCH b.patient " +
           "LEFT JOIN FETCH b.timeTypeData " +
           "WHERE b.doctor.id = :doctorId AND b.date = :date AND b.statusData.keyMap = 'S2'")
    List<Booking> findByDoctorAndDate(@Param("doctorId") int doctorId, @Param("date") String date);

    @Query("SELECT b FROM Booking b WHERE b.patient.id = :patientId AND b.doctor.id = :doctorId " +
           "AND b.date = :date AND b.timeTypeData.keyMap = :timeType")
    Optional<Booking> findByPatientAndDoctorAndDateAndTimeType(
            @Param("patientId") int patientId,
            @Param("doctorId") int doctorId,
            @Param("date") String date,
            @Param("timeType") String timeType);

    @Query("SELECT b FROM Booking b LEFT JOIN FETCH b.statusData WHERE b.token = :token AND b.doctor.id = :doctorId AND b.statusData.keyMap = 'S1'")
    Optional<Booking> findByTokenAndDoctorId(@Param("token") String token, @Param("doctorId") int doctorId);
}
