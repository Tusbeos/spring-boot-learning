package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Booking;
import com.emedicalbooking.entity.Doctor;
import com.emedicalbooking.entity.Clinic;
import com.emedicalbooking.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("BookingRepository Tests")
class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User patient;
    private User doctorUser;
    private Doctor doctor;
    private Booking testBooking;

    @BeforeEach
    void setUp() {
        // Create patient
        patient = User.builder()
                .fullName("John Patient")
                .email("patient@example.com")
                .password("password")
                .role(User.Role.PATIENT)
                .build();
        entityManager.persist(patient);

        // Create doctor user
        doctorUser = User.builder()
                .fullName("Dr. Jane Smith")
                .email("doctor@example.com")
                .password("password")
                .role(User.Role.DOCTOR)
                .build();
        entityManager.persist(doctorUser);

        // Create hospital
        Clinic hospital = Clinic.builder()
                .name("General Hospital")
                .city("New York")
                .build();
        entityManager.persist(hospital);

        // Create doctor
        doctor = Doctor.builder()
                .user(doctorUser)
                .hospital(hospital)
                .specialization("Cardiology")
                .consultationFee(500000)
                .build();
        entityManager.persist(doctor);

        // Create test booking
        testBooking = Booking.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.of(2026, 3, 15))
                .appointmentTime(LocalTime.of(10, 0))
                .symptoms("Chest pain")
                .status(Booking.BookingStatus.PENDING)
                .build();

        entityManager.flush();
    }

    @Test
    @DisplayName("Should save booking with all fields")
    void whenSaveBooking_thenAllFieldsPersisted() {
        // Act
        Booking saved = bookingRepository.save(testBooking);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPatient()).isEqualTo(patient);
        assertThat(saved.getDoctor()).isEqualTo(doctor);
        assertThat(saved.getAppointmentDate()).isEqualTo(LocalDate.of(2026, 3, 15));
        assertThat(saved.getAppointmentTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(saved.getSymptoms()).isEqualTo("Chest pain");
        assertThat(saved.getStatus()).isEqualTo(Booking.BookingStatus.PENDING);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find bookings by patient ID")
    void whenFindByPatientId_thenReturnPatientBookings() {
        // Arrange
        entityManager.persist(testBooking);

        Booking booking2 = Booking.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.of(2026, 3, 20))
                .appointmentTime(LocalTime.of(14, 0))
                .status(Booking.BookingStatus.CONFIRMED)
                .build();
        entityManager.persist(booking2);

        // Create another patient with booking
        User otherPatient = createAndPersistUser("other@example.com", "Other Patient", User.Role.PATIENT);
        Booking otherBooking = Booking.builder()
                .patient(otherPatient)
                .doctor(doctor)
                .appointmentDate(LocalDate.of(2026, 3, 16))
                .appointmentTime(LocalTime.of(11, 0))
                .status(Booking.BookingStatus.PENDING)
                .build();
        entityManager.persist(otherBooking);
        entityManager.flush();

        // Act
        List<Booking> patientBookings = bookingRepository.findByPatientId(patient.getId());

        // Assert
        assertThat(patientBookings).hasSize(2);
        assertThat(patientBookings).extracting(b -> b.getPatient().getId())
                .containsOnly(patient.getId());
    }

    @Test
    @DisplayName("Should return empty list when patient has no bookings")
    void whenFindByPatientId_withNoBookings_thenReturnEmptyList() {
        // Arrange
        User newPatient = createAndPersistUser("new@example.com", "New Patient", User.Role.PATIENT);
        entityManager.flush();

        // Act
        List<Booking> bookings = bookingRepository.findByPatientId(newPatient.getId());

        // Assert
        assertThat(bookings).isEmpty();
    }

    @Test
    @DisplayName("Should find bookings by doctor ID")
    void whenFindByDoctorId_thenReturnDoctorBookings() {
        // Arrange
        entityManager.persist(testBooking);

        Booking booking2 = Booking.builder()
                .patient(createAndPersistUser("patient2@example.com", "Patient Two", User.Role.PATIENT))
                .doctor(doctor)
                .appointmentDate(LocalDate.of(2026, 3, 18))
                .appointmentTime(LocalTime.of(15, 0))
                .status(Booking.BookingStatus.CONFIRMED)
                .build();
        entityManager.persist(booking2);

        // Create another doctor with booking
        User anotherDoctorUser = createAndPersistUser("dr2@example.com", "Dr. John", User.Role.DOCTOR);
        Doctor anotherDoctor = Doctor.builder()
                .user(anotherDoctorUser)
                .specialization("Neurology")
                .consultationFee(600000)
                .build();
        entityManager.persist(anotherDoctor);

        Booking otherDoctorBooking = Booking.builder()
                .patient(patient)
                .doctor(anotherDoctor)
                .appointmentDate(LocalDate.of(2026, 3, 19))
                .appointmentTime(LocalTime.of(9, 0))
                .status(Booking.BookingStatus.PENDING)
                .build();
        entityManager.persist(otherDoctorBooking);
        entityManager.flush();

        // Act
        List<Booking> doctorBookings = bookingRepository.findByDoctorId(doctor.getId());

        // Assert
        assertThat(doctorBookings).hasSize(2);
        assertThat(doctorBookings).extracting(b -> b.getDoctor().getId())
                .containsOnly(doctor.getId());
    }

    @Test
    @DisplayName("Should find bookings by doctor ID and appointment date")
    void whenFindByDoctorIdAndAppointmentDate_thenReturnMatches() {
        // Arrange
        LocalDate targetDate = LocalDate.of(2026, 3, 15);

        entityManager.persist(testBooking);

        Booking booking2 = Booking.builder()
                .patient(createAndPersistUser("patient2@example.com", "Patient Two", User.Role.PATIENT))
                .doctor(doctor)
                .appointmentDate(targetDate)
                .appointmentTime(LocalTime.of(14, 0))
                .status(Booking.BookingStatus.CONFIRMED)
                .build();
        entityManager.persist(booking2);

        Booking booking3 = Booking.builder()
                .patient(createAndPersistUser("patient3@example.com", "Patient Three", User.Role.PATIENT))
                .doctor(doctor)
                .appointmentDate(LocalDate.of(2026, 3, 16))
                .appointmentTime(LocalTime.of(10, 0))
                .status(Booking.BookingStatus.PENDING)
                .build();
        entityManager.persist(booking3);
        entityManager.flush();

        // Act
        List<Booking> bookingsOnDate = bookingRepository.findByDoctorIdAndAppointmentDate(
                doctor.getId(), targetDate);

        // Assert
        assertThat(bookingsOnDate).hasSize(2);
        assertThat(bookingsOnDate).extracting(Booking::getAppointmentDate)
                .containsOnly(targetDate);
        assertThat(bookingsOnDate).extracting(b -> b.getDoctor().getId())
                .containsOnly(doctor.getId());
    }

    @Test
    @DisplayName("Should return empty list when no bookings for doctor on date")
    void whenFindByDoctorIdAndAppointmentDate_withNoMatches_thenReturnEmptyList() {
        // Act
        List<Booking> bookings = bookingRepository.findByDoctorIdAndAppointmentDate(
                doctor.getId(), LocalDate.of(2026, 12, 31));

        // Assert
        assertThat(bookings).isEmpty();
    }

    @Test
    @DisplayName("Should find bookings by status")
    void whenFindByStatus_thenReturnMatchingBookings() {
        // Arrange
        entityManager.persist(testBooking);

        Booking confirmedBooking = Booking.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.of(2026, 3, 20))
                .appointmentTime(LocalTime.of(11, 0))
                .status(Booking.BookingStatus.CONFIRMED)
                .build();
        entityManager.persist(confirmedBooking);

        Booking cancelledBooking = Booking.builder()
                .patient(patient)
                .doctor(doctor)
                .appointmentDate(LocalDate.of(2026, 3, 21))
                .appointmentTime(LocalTime.of(12, 0))
                .status(Booking.BookingStatus.CANCELLED)
                .build();
        entityManager.persist(cancelledBooking);
        entityManager.flush();

        // Act
        List<Booking> pendingBookings = bookingRepository.findByStatus(Booking.BookingStatus.PENDING);
        List<Booking> confirmedBookings = bookingRepository.findByStatus(Booking.BookingStatus.CONFIRMED);

        // Assert
        assertThat(pendingBookings).hasSize(1);
        assertThat(pendingBookings.get(0).getStatus()).isEqualTo(Booking.BookingStatus.PENDING);

        assertThat(confirmedBookings).hasSize(1);
        assertThat(confirmedBookings.get(0).getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should return empty list when no bookings with status")
    void whenFindByStatus_withNoMatches_thenReturnEmptyList() {
        // Act
        List<Booking> completedBookings = bookingRepository.findByStatus(Booking.BookingStatus.COMPLETED);

        // Assert
        assertThat(completedBookings).isEmpty();
    }

    @Test
    @DisplayName("Should update booking status")
    void whenUpdateBookingStatus_thenChangesPersisted() {
        // Arrange
        entityManager.persist(testBooking);
        entityManager.flush();
        Long bookingId = testBooking.getId();

        // Act
        testBooking.setStatus(Booking.BookingStatus.CONFIRMED);
        testBooking.setNotes("Confirmed by receptionist");
        bookingRepository.save(testBooking);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Booking> updated = bookingRepository.findById(bookingId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(Booking.BookingStatus.CONFIRMED);
        assertThat(updated.get().getNotes()).isEqualTo("Confirmed by receptionist");
    }

    @Test
    @DisplayName("Should delete booking successfully")
    void whenDeleteBooking_thenNotFound() {
        // Arrange
        entityManager.persist(testBooking);
        entityManager.flush();
        Long bookingId = testBooking.getId();

        // Act
        bookingRepository.delete(testBooking);
        entityManager.flush();

        // Assert
        Optional<Booking> found = bookingRepository.findById(bookingId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save booking with all status types")
    void whenSaveBooking_withDifferentStatuses_thenAllPersisted() {
        // Arrange & Act & Assert
        for (Booking.BookingStatus status : Booking.BookingStatus.values()) {
            Booking booking = Booking.builder()
                    .patient(patient)
                    .doctor(doctor)
                    .appointmentDate(LocalDate.now().plusDays(1))
                    .appointmentTime(LocalTime.of(10, 0))
                    .status(status)
                    .build();
            Booking saved = bookingRepository.save(booking);
            assertThat(saved.getStatus()).isEqualTo(status);
        }
    }

    // Helper method
    private User createAndPersistUser(String email, String fullName, User.Role role) {
        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .password("password")
                .role(role)
                .build();
        entityManager.persist(user);
        return user;
    }
}
