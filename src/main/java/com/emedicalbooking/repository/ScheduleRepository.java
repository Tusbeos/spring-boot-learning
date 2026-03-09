package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.timeTypeData WHERE s.doctor.id = :doctorId AND s.date = :date")
    List<Schedule> findByDoctorIdAndDate(@Param("doctorId") int doctorId, @Param("date") String date);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.date = :date AND s.timeTypeData.keyMap = :timeType")
    Optional<Schedule> findByDoctorIdAndDateAndTimeType(
            @Param("doctorId") int doctorId,
            @Param("date") String date,
            @Param("timeType") String timeType);

    void deleteByDoctorIdAndDate(int doctorId, String date);
}
