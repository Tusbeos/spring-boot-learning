package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "package_services")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PackageServiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Gói khám chứa dịch vụ này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packageId", nullable = false)
    private MedicalPackage medicalPackage;

    // Nhóm dịch vụ, nối đến all_codes qua keyMap (VD: GS1, GS2...)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupServiceCode", referencedColumnName = "keyMap")
    private AllCode groupServiceData;

    @Column(nullable = false)
    private String serviceName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
