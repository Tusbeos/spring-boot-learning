package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "histories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookingId", nullable = false, unique = true)
    private Booking booking;

  
    @Column(columnDefinition = "TEXT")
    private String diagnosis;


    @Column(columnDefinition = "TEXT")
    private String prescription;


    @Column(columnDefinition = "TEXT")
    private String notes;


    private LocalDate examinationDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

