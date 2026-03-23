package com.emedicalbooking.entity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "specialties")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descriptionHTML;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String descriptionMarkdown;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    
}
