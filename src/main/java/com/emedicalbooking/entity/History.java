package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "histories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Gắn 1-1 với Booking: khi bác sĩ xác nhận đã khám xong (S3),
     * tạo 1 History record từ booking đó.
     * Từ booking có thể lấy: patient, doctor, date, timeType, reason.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookingId", nullable = false, unique = true)
    private Booking booking;

    /**
     * Chẩn đoán bệnh của bác sĩ sau khi khám.
     */
    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    /**
     * Đơn thuốc / phác đồ điều trị.
     */
    @Column(columnDefinition = "TEXT")
    private String prescription;

    /**
     * Ghi chú thêm của bác sĩ (ví dụ: tái khám sau 2 tuần...).
     */
    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

