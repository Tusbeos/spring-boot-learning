package com.emedicalbooking.repository;

import com.emedicalbooking.entity.DoctorService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorServiceRepository extends JpaRepository<DoctorService, Integer> {

    List<DoctorService> findByDoctorId(int doctorId);

    void deleteAllByDoctorId(int doctorId);
}
