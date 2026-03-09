package com.emedicalbooking.repository;

import com.emedicalbooking.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("UserRepository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword123")
                .phone("0123456789")
                .address("123 Main St")
                .role(User.Role.PATIENT)
                .build();
    }

    @Test
    @DisplayName("Should find user by email when email exists")
    void whenFindByEmail_withExistingEmail_thenReturnUser() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();

        // Act
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john.doe@example.com");
        assertThat(found.get().getFullName()).isEqualTo("John Doe");
        assertThat(found.get().getRole()).isEqualTo(User.Role.PATIENT);
    }

    @Test
    @DisplayName("Should return empty when finding by non-existent email")
    void whenFindByEmail_withNonExistentEmail_thenReturnEmpty() {
        // Act
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should return true when email exists")
    void whenExistsByEmail_withExistingEmail_thenReturnTrue() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByEmail("john.doe@example.com");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void whenExistsByEmail_withNonExistentEmail_thenReturnFalse() {
        // Act
        boolean exists = userRepository.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    @DisplayName("Should save user with all fields correctly")
    void whenSaveUser_thenAllFieldsPersisted() {
        // Arrange
        User user = User.builder()
                .fullName("Jane Smith")
                .email("jane.smith@example.com")
                .password("password456")
                .phone("0987654321")
                .address("456 Oak Ave")
                .avatarUrl("https://example.com/avatar.jpg")
                .role(User.Role.DOCTOR)
                .build();

        // Act
        User saved = userRepository.save(user);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFullName()).isEqualTo("Jane Smith");
        assertThat(saved.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(saved.getRole()).isEqualTo(User.Role.DOCTOR);
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should save user with ADMIN role")
    void whenSaveUser_withAdminRole_thenPersisted() {
        // Arrange
        User admin = User.builder()
                .fullName("Admin User")
                .email("admin@example.com")
                .password("adminPass")
                .role(User.Role.ADMIN)
                .build();

        // Act
        User saved = userRepository.save(admin);

        // Assert
        assertThat(saved.getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should find user by email case-sensitively")
    void whenFindByEmail_withDifferentCase_thenNotFound() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();

        // Act
        Optional<User> found = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");

        // Assert
        // Email should be case-sensitive by default
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should update user successfully")
    void whenUpdateUser_thenChangesPersisted() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();
        Long userId = testUser.getId();

        // Act
        testUser.setFullName("John Updated");
        testUser.setPhone("1112223333");
        userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Optional<User> updated = userRepository.findById(userId);
        assertThat(updated).isPresent();
        assertThat(updated.get().getFullName()).isEqualTo("John Updated");
        assertThat(updated.get().getPhone()).isEqualTo("1112223333");
    }

    @Test
    @DisplayName("Should delete user successfully")
    void whenDeleteUser_thenNotFound() {
        // Arrange
        entityManager.persist(testUser);
        entityManager.flush();
        String email = testUser.getEmail();

        // Act
        userRepository.delete(testUser);
        entityManager.flush();

        // Assert
        Optional<User> found = userRepository.findByEmail(email);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should handle multiple users with different emails")
    void whenSaveMultipleUsers_thenAllPersisted() {
        // Arrange
        User user1 = User.builder()
                .fullName("User One")
                .email("user1@example.com")
                .password("pass1")
                .role(User.Role.PATIENT)
                .build();

        User user2 = User.builder()
                .fullName("User Two")
                .email("user2@example.com")
                .password("pass2")
                .role(User.Role.DOCTOR)
                .build();

        // Act
        userRepository.save(user1);
        userRepository.save(user2);
        entityManager.flush();

        // Assert
        assertThat(userRepository.findByEmail("user1@example.com")).isPresent();
        assertThat(userRepository.findByEmail("user2@example.com")).isPresent();
        assertThat(userRepository.existsByEmail("user1@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("user2@example.com")).isTrue();
    }
}
