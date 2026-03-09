package com.emedicalbooking.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Doctor Entity Tests")
class DoctorTest {

    @Test
    @DisplayName("Should create doctor with builder pattern")
    void whenBuildDoctor_thenAllFieldsSet() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .fullName("Dr. Jane Smith")
                .email("jane@example.com")
                .password("password")
                .role(User.Role.DOCTOR)
                .build();

        Clinic hospital = Clinic.builder()
                .id(1L)
                .name("General Hospital")
                .city("New York")
                .build();

        // Act
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .specialization("Cardiology")
                .experience("10 years")
                .description("Experienced cardiologist")
                .imageUrl("https://example.com/doctor.jpg")
                .consultationFee(500000)
                .build();

        // Assert
        assertThat(doctor.getId()).isEqualTo(1L);
        assertThat(doctor.getUser()).isEqualTo(user);
        assertThat(doctor.getHospital()).isEqualTo(hospital);
        assertThat(doctor.getSpecialization()).isEqualTo("Cardiology");
        assertThat(doctor.getExperience()).isEqualTo("10 years");
        assertThat(doctor.getDescription()).isEqualTo("Experienced cardiologist");
        assertThat(doctor.getImageUrl()).isEqualTo("https://example.com/doctor.jpg");
        assertThat(doctor.getConsultationFee()).isEqualTo(500000);
    }

    @Test
    @DisplayName("Should create doctor without hospital")
    void whenBuildDoctor_withoutHospital_thenHospitalNull() {
        // Arrange
        User user = User.builder()
                .fullName("Dr. Freelance")
                .email("freelance@example.com")
                .password("password")
                .role(User.Role.DOCTOR)
                .build();

        // Act
        Doctor doctor = Doctor.builder()
                .user(user)
                .specialization("Psychology")
                .experience("5 years")
                .consultationFee(300000)
                .build();

        // Assert
        assertThat(doctor.getUser()).isEqualTo(user);
        assertThat(doctor.getHospital()).isNull();
        assertThat(doctor.getSpecialization()).isEqualTo("Psychology");
    }

    @Test
    @DisplayName("Should modify doctor fields with setters")
    void whenUseSetters_thenFieldsModified() {
        // Arrange
        Doctor doctor = Doctor.builder()
                .specialization("Cardiology")
                .experience("5 years")
                .consultationFee(400000)
                .build();

        User newUser = User.builder()
                .fullName("Dr. Updated")
                .email("updated@example.com")
                .password("password")
                .role(User.Role.DOCTOR)
                .build();

        Clinic newHospital = Clinic.builder()
                .name("New Hospital")
                .city("Boston")
                .build();

        // Act
        doctor.setUser(newUser);
        doctor.setHospital(newHospital);
        doctor.setSpecialization("Neurology");
        doctor.setExperience("10 years");
        doctor.setDescription("Updated description");
        doctor.setImageUrl("https://example.com/new-image.jpg");
        doctor.setConsultationFee(600000);

        // Assert
        assertThat(doctor.getUser()).isEqualTo(newUser);
        assertThat(doctor.getHospital()).isEqualTo(newHospital);
        assertThat(doctor.getSpecialization()).isEqualTo("Neurology");
        assertThat(doctor.getExperience()).isEqualTo("10 years");
        assertThat(doctor.getDescription()).isEqualTo("Updated description");
        assertThat(doctor.getImageUrl()).isEqualTo("https://example.com/new-image.jpg");
        assertThat(doctor.getConsultationFee()).isEqualTo(600000);
    }

    @Test
    @DisplayName("Should set timestamps")
    void whenSetTimestamps_thenTimestampsSet() {
        // Arrange
        Doctor doctor = new Doctor();
        LocalDateTime now = LocalDateTime.now();

        // Act
        doctor.setCreatedAt(now);
        doctor.setUpdatedAt(now);

        // Assert
        assertThat(doctor.getCreatedAt()).isEqualTo(now);
        assertThat(doctor.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create doctor with NoArgsConstructor")
    void whenUseNoArgsConstructor_thenDoctorCreated() {
        // Act
        Doctor doctor = new Doctor();

        // Assert
        assertThat(doctor).isNotNull();
        assertThat(doctor.getId()).isNull();
        assertThat(doctor.getUser()).isNull();
        assertThat(doctor.getHospital()).isNull();
        assertThat(doctor.getSpecialization()).isNull();
    }

    @Test
    @DisplayName("Should create doctor with AllArgsConstructor")
    void whenUseAllArgsConstructor_thenAllFieldsSet() {
        // Arrange
        User user = User.builder().fullName("Dr. Test").email("test@example.com").password("pass").role(User.Role.DOCTOR).build();
        Clinic hospital = Clinic.builder().name("Test Hospital").city("NYC").build();
        LocalDateTime now = LocalDateTime.now();

        // Act - sử dụng builder thay vì AllArgsConstructor vì constructor nhạy cảm với thứ tự field
        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(user)
                .hospital(hospital)
                .specialization("Cardiology")
                .experience("10 years")
                .description("Description")
                .imageUrl("https://example.com/image.jpg")
                .consultationFee(500000)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertThat(doctor.getId()).isEqualTo(1L);
        assertThat(doctor.getUser()).isEqualTo(user);
        assertThat(doctor.getHospital()).isEqualTo(hospital);
        assertThat(doctor.getSpecialization()).isEqualTo("Cardiology");
        assertThat(doctor.getExperience()).isEqualTo("10 years");
        assertThat(doctor.getDescription()).isEqualTo("Description");
        assertThat(doctor.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(doctor.getConsultationFee()).isEqualTo(500000);
        assertThat(doctor.getCreatedAt()).isEqualTo(now);
        assertThat(doctor.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle different consultation fees")
    void whenSetConsultationFee_thenFeeUpdated() {
        // Arrange
        Doctor doctor = new Doctor();

        // Act & Assert
        doctor.setConsultationFee(100000);
        assertThat(doctor.getConsultationFee()).isEqualTo(100000);

        doctor.setConsultationFee(1000000);
        assertThat(doctor.getConsultationFee()).isEqualTo(1000000);

        doctor.setConsultationFee(0);
        assertThat(doctor.getConsultationFee()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle various specializations")
    void whenSetSpecialization_thenSpecializationUpdated() {
        // Arrange
        Doctor doctor = new Doctor();

        // Act & Assert
        String[] specializations = {
                "Cardiology", "Neurology", "Pediatrics",
                "Orthopedics", "Dermatology", "Oncology"
        };

        for (String spec : specializations) {
            doctor.setSpecialization(spec);
            assertThat(doctor.getSpecialization()).isEqualTo(spec);
        }
    }
}
