package com.emedicalbooking.config;

import com.emedicalbooking.repository.DoctorInfosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeeklyResetScheduler {

    private final DoctorInfosRepository doctorInfosRepository;

    // Reset count về 0 mỗi thứ Hai lúc 00:00 (đầu tuần mới)
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void resetWeeklyBookingCount() {
        log.info("Bắt đầu reset count đặt lịch tuần mới...");
        doctorInfosRepository.findAll().forEach(info -> {
            info.setCount(0);
            doctorInfosRepository.save(info);
        });
        log.info("Đã reset count đặt lịch cho tất cả bác sĩ.");
    }
}
