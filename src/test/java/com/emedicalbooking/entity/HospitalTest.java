package com.emedicalbooking.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Hospital Entity Tests")
class HospitalTest {

    @Test
    @DisplayName("Should create hospital with builder pattern")
    void whenBuildHospital_thenAllFieldsSet() {
        // Arrange & Act
        Clinic hospital = Clinic.builder()
                .id(1L)
                .name("General Hospital")
                .address("123 Main St")
                .city("New York")
                .phone("555-1234")
                .email("info@generalhospital.com")
                .description("Leading medical center")
                .imageUrl("https://example.com/hospital.jpg")
                .build();

        // Assert
        assertThat(hospital.getId()).isEqualTo(1L);
        assertThat(hospital.getName()).isEqualTo("General Hospital");
        assertThat(hospital.getAddress()).isEqualTo("123 Main St");
        assertThat(hospital.getCity()).isEqualTo("New York");
        assertThat(hospital.getPhone()).isEqualTo("555-1234");
        assertThat(hospital.getEmail()).isEqualTo("info@generalhospital.com");
        assertThat(hospital.getDescription()).isEqualTo("Leading medical center");
        assertThat(hospital.getImageUrl()).isEqualTo("https://example.com/hospital.jpg");
    }

    @Test
    @DisplayName("Should create hospital with minimal fields")
    void whenBuildHospital_withMinimalFields_thenRequiredFieldsSet() {
        // Arrange & Act
        Clinic hospital = Clinic.builder()
                .name("City Hospital")
                .build();

        // Assert
        assertThat(hospital.getName()).isEqualTo("City Hospital");
        assertThat(hospital.getAddress()).isNull();
        assertThat(hospital.getCity()).isNull();
        assertThat(hospital.getPhone()).isNull();
    }

    @Test
    @DisplayName("Should modify hospital fields with setters")
    void whenUseSetters_thenFieldsModified() {
        // Arrange
        Clinic hospital = Clinic.builder()
                .name("Original Hospital")
                .city("Boston")
                .build();

        // Act
        hospital.setName("Updated Hospital");
        hospital.setAddress("456 Oak Ave");
        hospital.setCity("Chicago");
        hospital.setPhone("555-9999");
        hospital.setEmail("updated@hospital.com");
        hospital.setDescription("Updated description");
        hospital.setImageUrl("https://example.com/updated.jpg");

        // Assert
        assertThat(hospital.getName()).isEqualTo("Updated Hospital");
        assertThat(hospital.getAddress()).isEqualTo("456 Oak Ave");
        assertThat(hospital.getCity()).isEqualTo("Chicago");
        assertThat(hospital.getPhone()).isEqualTo("555-9999");
        assertThat(hospital.getEmail()).isEqualTo("updated@hospital.com");
        assertThat(hospital.getDescription()).isEqualTo("Updated description");
        assertThat(hospital.getImageUrl()).isEqualTo("https://example.com/updated.jpg");
    }

    @Test
    @DisplayName("Should manage doctors collection")
    void whenSetDoctors_thenCollectionManaged() {
        // Arrange
        Clinic hospital = new Clinic();
        List<Doctor> doctors = new ArrayList<>();

        User user1 = User.builder()
                .fullName("Dr. John")
                .email("john@example.com")
                .password("pass")
                .role(User.Role.DOCTOR)
                .build();

        User user2 = User.builder()
                .fullName("Dr. Jane")
                .email("jane@example.com")
                .password("pass")
                .role(User.Role.DOCTOR)
                .build();

        Doctor doctor1 = Doctor.builder()
                .user(user1)
                .specialization("Cardiology")
                .build();

        Doctor doctor2 = Doctor.builder()
                .user(user2)
                .specialization("Neurology")
                .build();

        doctors.add(doctor1);
        doctors.add(doctor2);

        // Act
        hospital.setDoctors(doctors);

        // Assert
        assertThat(hospital.getDoctors()).hasSize(2);
        assertThat(hospital.getDoctors()).containsExactly(doctor1, doctor2);
    }

    @Test
    @DisplayName("Should handle empty doctors list")
    void whenSetEmptyDoctorsList_thenListIsEmpty() {
        // Arrange
        Clinic hospital = new Clinic();

        // Act
        hospital.setDoctors(new ArrayList<>());

        // Assert
        assertThat(hospital.getDoctors()).isEmpty();
    }

    @Test
    @DisplayName("Should set timestamps")
    void whenSetTimestamps_thenTimestampsSet() {
        // Arrange
        Clinic hospital = new Clinic();
        LocalDateTime now = LocalDateTime.now();

        // Act
        hospital.setCreatedAt(now);
        hospital.setUpdatedAt(now);

        // Assert
        assertThat(hospital.getCreatedAt()).isEqualTo(now);
        assertThat(hospital.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create hospital with NoArgsConstructor")
    void whenUseNoArgsConstructor_thenHospitalCreated() {
        // Act
        Clinic hospital = new Clinic();

        // Assert
        assertThat(hospital).isNotNull();
        assertThat(hospital.getId()).isNull();
        assertThat(hospital.getName()).isNull();
        assertThat(hospital.getCity()).isNull();
    }

    @Test
    @DisplayName("Should create hospital with AllArgsConstructor")
    void whenUseAllArgsConstructor_thenAllFieldsSet() {
        // Arrange
        List<Doctor> doctors = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        // Act
        Clinic hospital = new Clinic(
                1L,
                "General Hospital",
                "123 Main St",
                "New York",
                "555-1234",
                "info@hospital.com",
                "Description",
                "https://example.com/image.jpg",
                doctors,
                now,
                now
        );

        // Assert
        assertThat(hospital.getId()).isEqualTo(1L);
        assertThat(hospital.getName()).isEqualTo("General Hospital");
        assertThat(hospital.getAddress()).isEqualTo("123 Main St");
        assertThat(hospital.getCity()).isEqualTo("New York");
        assertThat(hospital.getPhone()).isEqualTo("555-1234");
        assertThat(hospital.getEmail()).isEqualTo("info@hospital.com");
        assertThat(hospital.getDescription()).isEqualTo("Description");
        assertThat(hospital.getImageUrl()).isEqualTo("https://example.com/image.jpg");
        assertThat(hospital.getDoctors()).isEqualTo(doctors);
        assertThat(hospital.getCreatedAt()).isEqualTo(now);
        assertThat(hospital.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle null doctors list")
    void whenSetNullDoctorsList_thenListIsNull() {
        // Arrange
        Clinic hospital = new Clinic();

        // Act
        hospital.setDoctors(null);

        // Assert
        assertThat(hospital.getDoctors()).isNull();
    }
}
