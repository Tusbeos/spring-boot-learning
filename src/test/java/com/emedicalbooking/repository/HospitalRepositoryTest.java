package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Clinic;
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
@DisplayName("HospitalRepository Tests")
class HospitalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private HospitalRepository hospitalRepository;

    private Clinic testHospital;

    @BeforeEach
    void setUp() {
        testHospital = Clinic.builder()
                .name("General Hospital")
                .address("123 Main St")
                .city("New York")
                .phone("555-1234")
                .email("info@generalhospital.com")
                .description("Leading medical center")
                .build();
    }

    @Test
    @DisplayName("Should save hospital with all fields")
    void whenSaveHospital_thenAllFieldsPersisted() {
        // Act
        Clinic saved = hospitalRepository.save(testHospital);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("General Hospital");
        assertThat(saved.getCity()).isEqualTo("New York");
        assertThat(saved.getAddress()).isEqualTo("123 Main St");
        assertThat(saved.getPhone()).isEqualTo("555-1234");
        assertThat(saved.getEmail()).isEqualTo("info@generalhospital.com");
        assertThat(saved.getDescription()).isEqualTo("Leading medical center");
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find hospitals by city")
    void whenFindByCity_thenReturnMatchingHospitals() {
        // Arrange
        entityManager.persist(testHospital);

        Clinic hospital2 = Clinic.builder()
                .name("City Hospital")
                .city("New York")
                .address("456 Oak Ave")
                .build();
        entityManager.persist(hospital2);

        Clinic hospital3 = Clinic.builder()
                .name("Metro Hospital")
                .city("Boston")
                .address("789 Elm St")
                .build();
        entityManager.persist(hospital3);
        entityManager.flush();

        // Act
        List<Clinic> nyHospitals = hospitalRepository.findByCity("New York");

        // Assert
        assertThat(nyHospitals).hasSize(2);
        assertThat(nyHospitals).extracting(Clinic::getCity)
                .containsOnly("New York");
        assertThat(nyHospitals).extracting(Clinic::getName)
                .containsExactlyInAnyOrder("General Hospital", "City Hospital");
    }

    @Test
    @DisplayName("Should return empty list when no hospitals in city")
    void whenFindByCity_withNoMatches_thenReturnEmptyList() {
        // Act
        List<Clinic> hospitals = hospitalRepository.findByCity("Chicago");

        // Assert
        assertThat(hospitals).isEmpty();
    }

    @Test
    @DisplayName("Should find hospitals by name containing (case-insensitive)")
    void whenFindByNameContainingIgnoreCase_thenReturnMatches() {
        // Arrange
        entityManager.persist(testHospital);

        Clinic hospital2 = Clinic.builder()
                .name("City General Medical Center")
                .city("Boston")
                .build();
        entityManager.persist(hospital2);

        Clinic hospital3 = Clinic.builder()
                .name("St. Mary's Hospital")
                .city("Chicago")
                .build();
        entityManager.persist(hospital3);
        entityManager.flush();

        // Act
        List<Clinic> generalHospitals = hospitalRepository.findByNameContainingIgnoreCase("general");

        // Assert
        assertThat(generalHospitals).hasSize(2);
        assertThat(generalHospitals).extracting(Clinic::getName)
                .containsExactlyInAnyOrder("General Hospital", "City General Medical Center");
    }

    @Test
    @DisplayName("Should find hospitals by name containing with uppercase search")
    void whenFindByNameContainingIgnoreCase_withUppercaseSearch_thenReturnMatches() {
        // Arrange
        entityManager.persist(testHospital);
        entityManager.flush();

        // Act
        List<Clinic> hospitals = hospitalRepository.findByNameContainingIgnoreCase("GENERAL");

        // Assert
        assertThat(hospitals).hasSize(1);
        assertThat(hospitals.get(0).getName()).isEqualTo("General Hospital");
    }

    @Test
    @DisplayName("Should find hospitals by partial name match")
    void whenFindByNameContainingIgnoreCase_withPartialName_thenReturnMatches() {
        // Arrange
        Clinic hospital1 = Clinic.builder()
                .name("Memorial Hospital")
                .city("New York")
                .build();
        entityManager.persist(hospital1);

        Clinic hospital2 = Clinic.builder()
                .name("Memorial Medical Center")
                .city("Boston")
                .build();
        entityManager.persist(hospital2);

        Clinic hospital3 = Clinic.builder()
                .name("City Hospital")
                .city("Chicago")
                .build();
        entityManager.persist(hospital3);
        entityManager.flush();

        // Act
        List<Clinic> memorialHospitals = hospitalRepository.findByNameContainingIgnoreCase("memorial");

        // Assert
        assertThat(memorialHospitals).hasSize(2);
        assertThat(memorialHospitals).extracting(Clinic::getName)
                .allMatch(name -> name.toLowerCase().contains("memorial"));
    }

    @Test
    @DisplayName("Should return empty list when no hospitals match name search")
    void whenFindByNameContainingIgnoreCase_withNoMatches_thenReturnEmptyList() {
        // Arrange
        entityManager.persist(testHospital);
        entityManager.flush();

        // Act
        List<Clinic> hospitals = hospitalRepository.findByNameContainingIgnoreCase("NonExistent");

        // Assert
        assertThat(hospitals).isEmpty();
    }

    @Test
    @DisplayName("Should find hospital by ID")
    void whenFindById_withExistingId_thenReturnHospital() {
        // Arrange
        entityManager.persist(testHospital);
        entityManager.flush();
        Long hospitalId = testHospital.getId();

        // Act
        Optional<Clinic> found = hospitalRepository.findById(hospitalId);

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("General Hospital");
        assertThat(found.get().getCity()).isEqualTo("New York");
    }

    @Test
    @DisplayName("Should return empty when finding by non-existent ID")
    void whenFindById_withNonExistentId_thenReturnEmpty() {
        // Act
        Optional<Clinic> found = hospitalRepository.findById(999L);

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should update hospital information")
    void whenUpdateHospital_thenChangesPersisted() {
        // Arrange
        entityManager.persist(testHospital);
        entityManager.flush();
        Long hospitalId = testHospital.getId();

        // Act
        testHospital.setName("General Hospital - Updated");
        testHospital.setPhone("555-9999");
        testHospital.setDescription("Updated description");
        hospitalRepository.save(testHospital);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<Clinic> updated = hospitalRepository.findById(hospitalId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("General Hospital - Updated");
        assertThat(updated.get().getPhone()).isEqualTo("555-9999");
        assertThat(updated.get().getDescription()).isEqualTo("Updated description");
    }

    @Test
    @DisplayName("Should delete hospital successfully")
    void whenDeleteHospital_thenNotFound() {
        // Arrange
        entityManager.persist(testHospital);
        entityManager.flush();
        Long hospitalId = testHospital.getId();

        // Act
        hospitalRepository.delete(testHospital);
        entityManager.flush();

        // Assert
        Optional<Clinic> found = hospitalRepository.findById(hospitalId);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save hospital with minimal fields")
    void whenSaveHospital_withMinimalFields_thenPersisted() {
        // Arrange
        Clinic minimalHospital = Clinic.builder()
                .name("Minimal Hospital")
                .build();

        // Act
        Clinic saved = hospitalRepository.save(minimalHospital);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Minimal Hospital");
        assertThat(saved.getCity()).isNull();
        assertThat(saved.getAddress()).isNull();
    }

    @Test
    @DisplayName("Should handle multiple hospitals in same city")
    void whenFindByCity_withMultipleHospitals_thenReturnAll() {
        // Arrange
        for (int i = 1; i <= 3; i++) {
            Clinic hospital = Clinic.builder()
                    .name("Hospital " + i)
                    .city("Los Angeles")
                    .address("Address " + i)
                    .build();
            entityManager.persist(hospital);
        }
        entityManager.flush();

        // Act
        List<Clinic> laHospitals = hospitalRepository.findByCity("Los Angeles");

        // Assert
        assertThat(laHospitals).hasSize(3);
        assertThat(laHospitals).extracting(Clinic::getCity)
                .containsOnly("Los Angeles");
    }
}
