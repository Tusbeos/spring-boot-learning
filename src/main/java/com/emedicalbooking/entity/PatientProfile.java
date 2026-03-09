package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Bảng lưu hồ sơ bệnh nhân được đặt lịch thay (đặt cho người khác).
 * Liên kết với User (người đặt hộ) qua user_id.
 */
@Entity
@Table(name = "patient_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PatientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /** Người dùng đặt hộ (account holder) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    /** Giới tính: keyMap từ AllCode (M / F / O) */
    @Column(name = "gender")
    private String gender;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(columnDefinition = "TEXT")
    private String address;

    /** Mối quan hệ với người đặt hộ: cha, mẹ, con, vợ, chồng... */
    @Column(name = "relationship")
    private String relationship;

    @Column(name = "medical_history", columnDefinition = "TEXT")
    private String medicalHistory;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
