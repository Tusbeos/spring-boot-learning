package com.emedicalbooking.repository;

import com.emedicalbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);

    // JOIN FETCH roleData để tránh LazyInitializationException khi load role
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roleData WHERE u.email = :email")
    Optional<User> findByEmailWithRole(@Param("email") String email);

    // Lấy tất cả user kèm roleData, genderData, positionData (1 câu SQL, tránh N+1)
    @Query("SELECT u FROM User u " +
           "LEFT JOIN FETCH u.roleData " +
           "LEFT JOIN FETCH u.genderData " +
           "LEFT JOIN FETCH u.positionData")
    List<User> findAllWithRelations();
}
