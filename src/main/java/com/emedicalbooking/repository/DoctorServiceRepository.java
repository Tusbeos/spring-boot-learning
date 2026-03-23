package com.emedicalbooking.repository;

import com.emedicalbooking.entity.DoctorService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorServiceRepository extends JpaRepository<DoctorService, Long> {

    List<DoctorService> findByDoctorId(Long doctorId);

    void deleteAllByDoctorId(Long doctorId);
}
