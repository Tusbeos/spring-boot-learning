package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;



@Entity
@Table(name = "doctor_infos")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder

public class DoctorInfos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = true)
    private String addressClinic;

    @Column(nullable = true)
    private String nameClinic;

    @Column(nullable = true)
    private String note;

    @Column(nullable = true)
    private int count;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctorId", nullable = false)
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialtyId")
    private Specialty specialty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "priceId")
    private AllCode priceData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provinceId")
    private AllCode provinceData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paymentId")
    private AllCode paymentData;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
