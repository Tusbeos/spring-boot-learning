package com.emedicalbooking.job;

import com.emedicalbooking.entity.Booking;
import com.emedicalbooking.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingCleanupJob {

    private final BookingRepository bookingRepository;

    /**
     * Tự động chạy mỗi giờ 1 lần để xác định các Booking trạng thái S1 (Chờ xác nhận)
     * đã vượt quá tokenExpiry (quá hạn xác nhận email) và xóa chúng để dọn dẹp DB.
     * Cú pháp Cron: giây phút giờ ngày tháng thứ (0 0 * * * * = tròn mỗi giờ)
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredBookings() {
        log.info("Starting scheduled job: cleanupExpiredBookings at {}", LocalDateTime.now());
        
        // Lấy tất cả bookings (S1) với tokenExpiry trước thời điểm hiện tại
        List<Booking> expiredBookings = bookingRepository.findExpiredUnconfirmedBookings(LocalDateTime.now());
        
        if (!expiredBookings.isEmpty()) {
            log.info("Found {} expired bookings to delete", expiredBookings.size());
            bookingRepository.deleteAll(expiredBookings);
            log.info("Successfully deleted {} expired bookings", expiredBookings.size());
        } else {
            log.info("No expired bookings found. Job finished.");
        }
    }
}
