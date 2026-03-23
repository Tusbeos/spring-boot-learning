package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s LEFT JOIN FETCH s.timeTypeData WHERE s.doctor.id = :doctorId AND s.date = :date")
    List<Schedule> findByDoctorIdAndDate(@Param("doctorId") Long doctorId, @Param("date") String date);

    @Query("SELECT s FROM Schedule s WHERE s.doctor.id = :doctorId AND s.date = :date AND s.timeTypeData.keyMap = :timeType")
    Optional<Schedule> findByDoctorIdAndDateAndTimeType(
            @Param("doctorId") Long doctorId,
            @Param("date") String date,
            @Param("timeType") String timeType);

    /**
     * Tăng currentNumber một cách nguyên tử (atomic) nếu còn slot trống.
     * Trả về số rows bị ảnh hưởng: 1 = thành công, 0 = hết chỗ (race condition bị chặn).
     */
    @Modifying
    @Query("UPDATE Schedule s SET s.currentNumber = s.currentNumber + 1 " +
           "WHERE s.id = :id AND s.currentNumber < s.maxNumber")
    int incrementCurrentNumber(@Param("id") Long scheduleId);

    /** Giảm currentNumber khi xoá lịch hẽn hết hạn. Chỉ giảm khi còn > 0 để tránh âm. */
    @Modifying
    @Query("UPDATE Schedule s SET s.currentNumber = s.currentNumber - 1 " +
           "WHERE s.id = :id AND s.currentNumber > 0")
    int decrementCurrentNumber(@Param("id") Long scheduleId);

    void deleteByDoctorIdAndDate(Long doctorId, String date);
}
