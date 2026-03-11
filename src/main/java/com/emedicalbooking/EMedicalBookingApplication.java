package com.emedicalbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync   // Bật @Async để gửi email không block luồng chính
@org.springframework.scheduling.annotation.EnableScheduling // Bật @Scheduled cho cleanup task
public class EMedicalBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EMedicalBookingApplication.class, args);
    }
}
