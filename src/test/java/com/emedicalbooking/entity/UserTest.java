package com.emedicalbooking.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Entity Tests")
class UserTest {

    @Test
    @DisplayName("Should create user with builder pattern")
    void whenBuildUser_thenAllFieldsSet() {
        // Arrange & Act
        User user = User.builder()
                .id(1L)
                .fullName("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .phone("0123456789")
                .address("123 Main St")
                .avatarUrl("https://example.com/avatar.jpg")
                .role(User.Role.PATIENT)
                .build();

        // Assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(user.getPassword()).isEqualTo("encodedPassword");
        assertThat(user.getPhone()).isEqualTo("0123456789");
        assertThat(user.getAddress()).isEqualTo("123 Main St");
        assertThat(user.getAvatarUrl()).isEqualTo("https://example.com/avatar.jpg");
        assertThat(user.getRole()).isEqualTo(User.Role.PATIENT);
    }

    @Test
    @DisplayName("Should create user with DOCTOR role")
    void whenBuildUser_withDoctorRole_thenRoleSet() {
        // Arrange & Act
        User user = User.builder()
                .fullName("Dr. Jane Smith")
                .email("jane@example.com")
                .password("password")
                .role(User.Role.DOCTOR)
                .build();

        // Assert
        assertThat(user.getRole()).isEqualTo(User.Role.DOCTOR);
    }

    @Test
    @DisplayName("Should create user with ADMIN role")
    void whenBuildUser_withAdminRole_thenRoleSet() {
        // Arrange & Act
        User user = User.builder()
                .fullName("Admin User")
                .email("admin@example.com")
                .password("password")
                .role(User.Role.ADMIN)
                .build();

        // Assert
        assertThat(user.getRole()).isEqualTo(User.Role.ADMIN);
    }

    @Test
    @DisplayName("Should modify user fields with setters")
    void whenUseSetters_thenFieldsModified() {
        // Arrange
        User user = User.builder()
                .fullName("Original Name")
                .email("original@example.com")
                .password("password")
                .role(User.Role.PATIENT)
                .build();

        // Act
        user.setFullName("Updated Name");
        user.setEmail("updated@example.com");
        user.setPhone("9876543210");
        user.setAddress("456 Oak Ave");
        user.setAvatarUrl("https://example.com/new-avatar.jpg");
        user.setRole(User.Role.DOCTOR);

        // Assert
        assertThat(user.getFullName()).isEqualTo("Updated Name");
        assertThat(user.getEmail()).isEqualTo("updated@example.com");
        assertThat(user.getPhone()).isEqualTo("9876543210");
        assertThat(user.getAddress()).isEqualTo("456 Oak Ave");
        assertThat(user.getAvatarUrl()).isEqualTo("https://example.com/new-avatar.jpg");
        assertThat(user.getRole()).isEqualTo(User.Role.DOCTOR);
    }

    @Test
    @DisplayName("Should set timestamps")
    void whenSetTimestamps_thenTimestampsSet() {
        // Arrange
        User user = new User();
        LocalDateTime now = LocalDateTime.now();

        // Act
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        // Assert
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should test all Role enum values")
    void whenCheckRoleEnum_thenAllValuesExist() {
        // Assert
        assertThat(User.Role.values()).containsExactlyInAnyOrder(
                User.Role.PATIENT,
                User.Role.DOCTOR,
                User.Role.ADMIN
        );
    }

    @Test
    @DisplayName("Should convert Role enum to string")
    void whenConvertRoleToString_thenCorrectString() {
        // Assert
        assertThat(User.Role.PATIENT.toString()).isEqualTo("PATIENT");
        assertThat(User.Role.DOCTOR.toString()).isEqualTo("DOCTOR");
        assertThat(User.Role.ADMIN.toString()).isEqualTo("ADMIN");
    }

    @Test
    @DisplayName("Should create user with NoArgsConstructor")
    void whenUseNoArgsConstructor_thenUserCreated() {
        // Act
        User user = new User();

        // Assert
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getFullName()).isNull();
        assertThat(user.getEmail()).isNull();
    }

    @Test
    @DisplayName("Should create user with AllArgsConstructor")
    void whenUseAllArgsConstructor_thenAllFieldsSet() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();

        // Act
        User user = new User(
                1L,
                "John Doe",
                "john@example.com",
                "password",
                "0123456789",
                "123 Main St",
                "https://example.com/avatar.jpg",
                User.Role.PATIENT,
                true,
                now,
                now
        );

        // Assert
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getFullName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("password");
        assertThat(user.getPhone()).isEqualTo("0123456789");
        assertThat(user.getAddress()).isEqualTo("123 Main St");
        assertThat(user.getAvatarUrl()).isEqualTo("https://example.com/avatar.jpg");
        assertThat(user.getRole()).isEqualTo(User.Role.PATIENT);
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should set enabled status")
    void whenSetEnabled_thenStatusUpdated() {
        // Arrange
        User user = new User();

        // Act
        user.setEnabled(false);

        // Assert
        assertThat(user.isEnabled()).isFalse();

        // Act
        user.setEnabled(true);

        // Assert
        assertThat(user.isEnabled()).isTrue();
    }
}
