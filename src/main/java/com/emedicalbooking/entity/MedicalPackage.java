package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "packages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MedicalPackage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Loại gói khám, nối đến all_codes qua keyMap (VD: PK1, PK2...)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "typeCode", referencedColumnName = "keyMap")
    private AllCode typeData;

    @Column(nullable = false)
    private String name;

    // Phòng khám sở hữu gói khám này
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinicId")
    private Clinic clinic;

    private int price;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(columnDefinition = "TEXT")
    private String note;

    // Danh sách các dịch vụ trong gói khám
    @OneToMany(mappedBy = "medicalPackage", fetch = FetchType.LAZY)
    private List<PackageServiceItem> packageServices;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "statusId", referencedColumnName = "keyMap")
    private AllCode statusData;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
