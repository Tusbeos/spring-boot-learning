package com.emedicalbooking.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Booking Entity Tests")
class BookingTest {

    @Test
    @DisplayName("Should create booking with builder pattern")
    void whenBuildBooking_thenAllFieldsSet() {
        // Arrange
        User patient = User.builder()
                .id(1L)
                .fullName("John Patient")
                .email("patient@example.com")
                .password("password")
                .role(User.Role.PATIENT)
                .build();

        User doctorUser = User.builder()
                .id(2L)
                .fullName("Dr. Jane Smith")
                .email("doctor@example.com")
                .password("password")
                .role(User.Role.DOCTOR)
                .build();

        Doctor doctor = Doctor.builder()
                .id(1L)
                .user(doctorUser)
                .specialization("Cardiology")
                .consultationFee(500000)
                .build();

        LocalDate appointmentDate = LocalDate.of(2026, 3, 15);
        LocalTime appointmentTime = LocalTime.of(10, 30);

        // Act
        Booking booking = Booking.builder()
                .id(1L)
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .symptoms("Chest pain and shortness of breath")
                .notes("First visit")
                .status(Booking.BookingStatus.PENDING)
                .build();

        // Assert
        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getPatient()).isEqualTo(patient);
        assertThat(booking.getDoctor()).isEqualTo(doctor);
        assertThat(booking.getAppointmentDate()).isEqualTo(appointmentDate);
        assertThat(booking.getAppointmentTime()).isEqualTo(appointmentTime);
        assertThat(booking.getSymptoms()).isEqualTo("Chest pain and shortness of breath");
        assertThat(booking.getNotes()).isEqualTo("First visit");
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.PENDING);
    }

    @Test
    @DisplayName("Should create booking with minimal fields")
    void whenBuildBooking_withMinimalFields_thenRequiredFieldsSet() {
        // Arrange
        User patient = User.builder().fullName("Patient").email("p@example.com").password("pass").role(User.Role.PATIENT).build();
        Doctor doctor = Doctor.builder().user(patient).specialization("Cardiology").consultationFee(100000).build();

        // Act
        Booking booking = Booking.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.now().plusDays(1))
                .appointmentTime(LocalTime.of(14, 0))
                .status(Booking.BookingStatus.PENDING)
                .build();

        // Assert
        assertThat(booking.getPatient()).isEqualTo(patient);
        assertThat(booking.getDoctor()).isEqualTo(doctor);
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.PENDING);
        assertThat(booking.getSymptoms()).isNull();
        assertThat(booking.getNotes()).isNull();
    }

    @Test
    @DisplayName("Should modify booking fields with setters")
    void whenUseSetters_thenFieldsModified() {
        // Arrange
        Booking booking = new Booking();
        User patient = User.builder().fullName("Patient").email("p@example.com").password("pass").role(User.Role.PATIENT).build();
        Doctor doctor = Doctor.builder().specialization("Cardiology").build();
        LocalDate newDate = LocalDate.of(2026, 4, 20);
        LocalTime newTime = LocalTime.of(15, 30);

        // Act
        booking.setPatient(patient);
        booking.setDoctor(doctor);
        booking.setAppointmentDate(newDate);
        booking.setAppointmentTime(newTime);
        booking.setSymptoms("Updated symptoms");
        booking.setNotes("Updated notes");
        booking.setStatus(Booking.BookingStatus.CONFIRMED);

        // Assert
        assertThat(booking.getPatient()).isEqualTo(patient);
        assertThat(booking.getDoctor()).isEqualTo(doctor);
        assertThat(booking.getAppointmentDate()).isEqualTo(newDate);
        assertThat(booking.getAppointmentTime()).isEqualTo(newTime);
        assertThat(booking.getSymptoms()).isEqualTo("Updated symptoms");
        assertThat(booking.getNotes()).isEqualTo("Updated notes");
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should test all BookingStatus enum values")
    void whenCheckBookingStatusEnum_thenAllValuesExist() {
        // Assert
        assertThat(Booking.BookingStatus.values()).containsExactlyInAnyOrder(
                Booking.BookingStatus.PENDING,
                Booking.BookingStatus.CONFIRMED,
                Booking.BookingStatus.CANCELLED,
                Booking.BookingStatus.COMPLETED
        );
    }

    @Test
    @DisplayName("Should convert BookingStatus enum to string")
    void whenConvertStatusToString_thenCorrectString() {
        // Assert
        assertThat(Booking.BookingStatus.PENDING.toString()).isEqualTo("PENDING");
        assertThat(Booking.BookingStatus.CONFIRMED.toString()).isEqualTo("CONFIRMED");
        assertThat(Booking.BookingStatus.CANCELLED.toString()).isEqualTo("CANCELLED");
        assertThat(Booking.BookingStatus.COMPLETED.toString()).isEqualTo("COMPLETED");
    }

    @Test
    @DisplayName("Should transition through booking statuses")
    void whenUpdateStatus_thenStatusTransitions() {
        // Arrange
        Booking booking = new Booking();

        // Act & Assert - Simulate booking lifecycle
        booking.setStatus(Booking.BookingStatus.PENDING);
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.PENDING);

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);

        booking.setStatus(Booking.BookingStatus.COMPLETED);
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.COMPLETED);

        // Alternative path
        Booking booking2 = new Booking();
        booking2.setStatus(Booking.BookingStatus.PENDING);
        booking2.setStatus(Booking.BookingStatus.CANCELLED);
        assertThat(booking2.getStatus()).isEqualTo(Booking.BookingStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should set timestamps")
    void whenSetTimestamps_thenTimestampsSet() {
        // Arrange
        Booking booking = new Booking();
        LocalDateTime now = LocalDateTime.now();

        // Act
        booking.setCreatedAt(now);
        booking.setUpdatedAt(now);

        // Assert
        assertThat(booking.getCreatedAt()).isEqualTo(now);
        assertThat(booking.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should create booking with NoArgsConstructor")
    void whenUseNoArgsConstructor_thenBookingCreated() {
        // Act
        Booking booking = new Booking();

        // Assert
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNull();
        assertThat(booking.getPatient()).isNull();
        assertThat(booking.getDoctor()).isNull();
        assertThat(booking.getStatus()).isNull();
    }

    @Test
    @DisplayName("Should create booking with AllArgsConstructor")
    void whenUseAllArgsConstructor_thenAllFieldsSet() {
        // Arrange
        User patient = User.builder().fullName("Patient").email("p@example.com").password("pass").role(User.Role.PATIENT).build();
        Doctor doctor = Doctor.builder().specialization("Cardiology").build();
        LocalDate date = LocalDate.of(2026, 5, 10);
        LocalTime time = LocalTime.of(11, 0);
        LocalDateTime now = LocalDateTime.now();

        // Act
        Booking booking = new Booking(
                1L,
                patient,
                doctor,
                date,
                time,
                "Symptoms",
                "Notes",
                Booking.BookingStatus.CONFIRMED,
                now,
                now
        );

        // Assert
        assertThat(booking.getId()).isEqualTo(1L);
        assertThat(booking.getPatient()).isEqualTo(patient);
        assertThat(booking.getDoctor()).isEqualTo(doctor);
        assertThat(booking.getAppointmentDate()).isEqualTo(date);
        assertThat(booking.getAppointmentTime()).isEqualTo(time);
        assertThat(booking.getSymptoms()).isEqualTo("Symptoms");
        assertThat(booking.getNotes()).isEqualTo("Notes");
        assertThat(booking.getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
        assertThat(booking.getCreatedAt()).isEqualTo(now);
        assertThat(booking.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should handle appointment time variations")
    void whenSetAppointmentTime_withDifferentTimes_thenSet() {
        // Arrange
        Booking booking = new Booking();

        // Act & Assert - Test different time slots
        LocalTime[] times = {
                LocalTime.of(9, 0),
                LocalTime.of(10, 30),
                LocalTime.of(14, 15),
                LocalTime.of(16, 45)
        };

        for (LocalTime time : times) {
            booking.setAppointmentTime(time);
            assertThat(booking.getAppointmentTime()).isEqualTo(time);
        }
    }
}
