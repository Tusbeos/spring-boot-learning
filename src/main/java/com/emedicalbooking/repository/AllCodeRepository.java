package com.emedicalbooking.repository;

import com.emedicalbooking.entity.AllCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AllCodeRepository extends JpaRepository<AllCode, Integer> {
    List<AllCode> findByType(String type);
    Optional<AllCode> findByKeyMap(String keyMap);
}
