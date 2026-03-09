package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "clinics")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Clinic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;
    private String address;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageCover;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String descriptionHTML;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String descriptionMarkdown;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
