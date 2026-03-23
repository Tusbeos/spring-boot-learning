package com.emedicalbooking.repository;

import com.emedicalbooking.entity.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {

    List<Specialty> findByIdIn(List<Integer> ids);
}
