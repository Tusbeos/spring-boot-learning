package com.emedicalbooking.repository;

import com.emedicalbooking.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {

    @Query("SELECT p FROM PatientProfile p WHERE p.user.id = :userId")
    List<PatientProfile> findByUserId(@Param("userId") Long userId);
}
