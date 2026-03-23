package com.emedicalbooking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;

    @Column(name = "refresh_token", length = 500)
    private String refreshToken;

    @Column(name = "refresh_token_expiry")
    private LocalDateTime refreshTokenExpiry;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "positionId", referencedColumnName = "keyMap")
    private AllCode positionData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender", referencedColumnName = "keyMap")
    private AllCode genderData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId", referencedColumnName = "keyMap")
    private AllCode roleData;

    @OneToOne(mappedBy = "doctor", fetch = FetchType.LAZY)
    private DoctorInfos doctorInfo;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<Schedule> schedules;

    @OneToMany(mappedBy = "doctor", fetch = FetchType.LAZY)
    private List<DoctorClinicSpecialty> doctorSpecialties;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
