package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "doctor_services")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DoctorService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nameVi;
    private String nameEn;
    private String price;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String descriptionVi;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descriptionEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctorId")
    private User doctor;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
