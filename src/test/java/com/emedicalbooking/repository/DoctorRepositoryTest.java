package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Doctor;
import com.emedicalbooking.entity.Clinic;
import com.emedicalbooking.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("DoctorRepository Tests")
class DoctorRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private DoctorRepository doctorRepository;

    private User testUser;
    private Clinic testHospital;
    private Doctor testDoctor;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = User.builder()
                .fullName("Dr. Jane Smith")
                .email("dr.jane@example.com")
                .password("password123")
                .role(User.Role.DOCTOR)
                .build();
        entityManager.persist(testUser);

        // Create test hospital
        testHospital = Clinic.builder()
                .name("General Hospital")
                .city("New York")
                .address("123 Main St")
                .build();
        entityManager.persist(testHospital);

        // Create test doctor
        testDoctor = Doctor.builder()
                .user(testUser)
                .hospital(testHospital)
                .specialization("Cardiology")
                .experience("10 years")
                .consultationFee(500000)
                .build();
        
        entityManager.flush();
    }

    @Test
    @DisplayName("Should save doctor with all fields")
    void whenSaveDoctor_thenAllFieldsPersisted() {
        // Act
        Doctor saved = doctorRepository.save(testDoctor);
        entityManager.flush();

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUser()).isEqualTo(testUser);
        assertThat(saved.getHospital()).isEqualTo(testHospital);
        assertThat(saved.getSpecialization()).isEqualTo("Cardiology");
        assertThat(saved.getExperience()).isEqualTo("10 years");
        assertThat(saved.getConsultationFee()).isEqualTo(500000);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find doctors by specialization")
    void whenFindBySpecialization_thenReturnMatchingDoctors() {
        // Arrange
        entityManager.persist(testDoctor);

        Doctor doctor2 = Doctor.builder()
                .user(createAndPersistUser("dr.john@example.com", "Dr. John Doe"))
                .hospital(testHospital)
                .specialization("Cardiology")
                .experience("5 years")
                .consultationFee(400000)
                .build();
        entityManager.persist(doctor2);

        Doctor doctor3 = Doctor.builder()
                .user(createAndPersistUser("dr.mary@example.com", "Dr. Mary Johnson"))
                .hospital(testHospital)
                .specialization("Neurology")
                .experience("8 years")
                .consultationFee(600000)
                .build();
        entityManager.persist(doctor3);
        entityManager.flush();

        // Act
        List<Doctor> cardiologists = doctorRepository.findBySpecialization("Cardiology");

        // Assert
        assertThat(cardiologists).hasSize(2);
        assertThat(cardiologists).extracting(Doctor::getSpecialization)
                .containsOnly("Cardiology");
    }

    @Test
    @DisplayName("Should return empty list when no doctors match specialization")
    void whenFindBySpecialization_withNoMatches_thenReturnEmptyList() {
        // Act
        List<Doctor> doctors = doctorRepository.findBySpecialization("Dermatology");

        // Assert
        assertThat(doctors).isEmpty();
    }

    @Test
    @DisplayName("Should find doctors by hospital ID")
    void whenFindByHospitalId_thenReturnMatchingDoctors() {
        // Arrange
        entityManager.persist(testDoctor);

        Doctor doctor2 = Doctor.builder()
                .user(createAndPersistUser("dr.bob@example.com", "Dr. Bob Wilson"))
                .hospital(testHospital)
                .specialization("Pediatrics")
                .experience("7 years")
                .consultationFee(450000)
                .build();
        entityManager.persist(doctor2);

        Clinic otherHospital = Clinic.builder()
                .name("City Hospital")
                .city("Boston")
                .build();
        entityManager.persist(otherHospital);

        Doctor doctor3 = Doctor.builder()
                .user(createAndPersistUser("dr.alice@example.com", "Dr. Alice Brown"))
                .hospital(otherHospital)
                .specialization("Surgery")
                .experience("12 years")
                .consultationFee(700000)
                .build();
        entityManager.persist(doctor3);
        entityManager.flush();

        // Act
        List<Doctor> doctorsInGeneralHospital = doctorRepository.findByHospitalId(testHospital.getId());

        // Assert
        assertThat(doctorsInGeneralHospital).hasSize(2);
        assertThat(doctorsInGeneralHospital).extracting(d -> d.getHospital().getName())
                .containsOnly("General Hospital");
    }

    @Test
    @DisplayName("Should return empty list when no doctors in hospital")
    void whenFindByHospitalId_withNoMatches_thenReturnEmptyList() {
        // Act
        List<Doctor> doctors = doctorRepository.findByHospitalId(999L);

        // Assert
        assertThat(doctors).isEmpty();
    }

    @Test
    @DisplayName("Should find doctor by ID")
    void whenFindById_withExistingId_thenReturnDoctor() {
        // Arrange
        entityManager.persist(testDoctor);
        entityManager.flush();
        Long doctorId = testDoctor.getId();

        // Act
        Optional<Doctor> found = doctorRepository.findById(doctorId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getSpecialization()).isEqualTo("Cardiology");
        assertThat(found.get().getUser().getFullName()).isEqualTo("Dr. Jane Smith");
    }

    @Test
    @DisplayName("Should update doctor information")
    void whenUpdateDoctor_thenChangesPersisted() {
        // Arrange
        entityManager.persist(testDoctor);
        entityManager.flush();
        Long doctorId = testDoctor.getId();

        // Act
        testDoctor.setSpecialization("Cardiology & Vascular");
        testDoctor.setExperience("12 years");
        testDoctor.setConsultationFee(600000);
        doctorRepository.save(testDoctor);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Doctor> updated = doctorRepository.findById(doctorId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getSpecialization()).isEqualTo("Cardiology & Vascular");
        assertThat(updated.get().getExperience()).isEqualTo("12 years");
        assertThat(updated.get().getConsultationFee()).isEqualTo(600000);
    }

    @Test
    @DisplayName("Should delete doctor successfully")
    void whenDeleteDoctor_thenNotFound() {
        // Arrange
        entityManager.persist(testDoctor);
        entityManager.flush();
        Long doctorId = testDoctor.getId();

        // Act
        doctorRepository.delete(testDoctor);
        entityManager.flush();

        // Assert
        Optional<Doctor> found = doctorRepository.findById(doctorId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save doctor without hospital")
    void whenSaveDoctor_withoutHospital_thenPersisted() {
        // Arrange
        User newUser = createAndPersistUser("dr.freelance@example.com", "Dr. Freelance");
        Doctor freelanceDoctor = Doctor.builder()
                .user(newUser)
                .hospital(null)
                .specialization("Psychology")
                .experience("3 years")
                .consultationFee(300000)
                .build();

        // Act
        Doctor saved = doctorRepository.save(freelanceDoctor);
        entityManager.flush();

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getHospital()).isNull();
        assertThat(saved.getSpecialization()).isEqualTo("Psychology");
    }

    @Test
    @DisplayName("Should handle multiple doctors with same specialization in different hospitals")
    void whenFindBySpecialization_withMultipleHospitals_thenReturnAll() {
        // Arrange
        entityManager.persist(testDoctor);

        Clinic hospital2 = Clinic.builder()
                .name("Metro Hospital")
                .city("Chicago")
                .build();
        entityManager.persist(hospital2);

        Doctor doctor2 = Doctor.builder()
                .user(createAndPersistUser("dr.metro@example.com", "Dr. Metro"))
                .hospital(hospital2)
                .specialization("Cardiology")
                .experience("15 years")
                .consultationFee(800000)
                .build();
        entityManager.persist(doctor2);
        entityManager.flush();

        // Act
        List<Doctor> cardiologists = doctorRepository.findBySpecialization("Cardiology");

        // Assert
        assertThat(cardiologists).hasSize(2);
        assertThat(cardiologists).extracting(d -> d.getHospital().getName())
                .containsExactlyInAnyOrder("General Hospital", "Metro Hospital");
    }

    // Helper method to create and persist users
    private User createAndPersistUser(String email, String fullName) {
        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .password("password")
                .role(User.Role.DOCTOR)
                .build();
        entityManager.persist(user);
        return user;
    }
}
