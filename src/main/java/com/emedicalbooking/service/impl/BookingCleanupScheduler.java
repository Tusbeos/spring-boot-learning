package com.emedicalbooking.service.impl;

import com.emedicalbooking.entity.Booking;
import com.emedicalbooking.repository.BookingRepository;
import com.emedicalbooking.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduled task chạy mỗi 60 giây để dọn dẹp các lịch hẹn
 * chưa xác nhận email (status S1) đã hết hạn token.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;
    private final ScheduleRepository scheduleRepository;

    @Scheduled(fixedRate = 60_000) // Chạy mỗi 60 giây
    @Transactional
    public void cleanupExpiredBookings() {
        List<Booking> expired = bookingRepository.findExpiredUnconfirmedBookings(LocalDateTime.now());

        if (expired.isEmpty()) return;

        for (Booking booking : expired) {
            try {
                // Giảm currentNumber của schedule để slot được giải phóng
                scheduleRepository.findByDoctorIdAndDateAndTimeType(
                        booking.getDoctor().getId(),
                        booking.getDate(),
                        booking.getTimeTypeData().getKeyMap()
                ).ifPresent(schedule ->
                        scheduleRepository.decrementCurrentNumber(schedule.getId())
                );

                bookingRepository.delete(booking);
            } catch (Exception e) {
                log.error("Lỗi khi xóa booking hết hạn id={}: {}", booking.getId(), e.getMessage());
            }
        }

        log.info("Cleanup: đã xóa {} lịch hẹn hết hạn xác nhận email", expired.size());
    }
}
