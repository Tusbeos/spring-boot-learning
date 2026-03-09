package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "markdowns")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Markdown {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String contentHTML;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String contentMarkdown;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;
    private int doctorId;
    private int clinicId;
    private int specialtyId;

    


    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
}
