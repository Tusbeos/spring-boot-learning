package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "all_codes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AllCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String keyMap; 

    @Column(nullable = true)
    private String type; 

    @Column(nullable = true)
    private String valueEn;

    @Column(nullable = true)
    private String valueVi;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;


}
