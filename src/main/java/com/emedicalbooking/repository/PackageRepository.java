package com.emedicalbooking.repository;

import com.emedicalbooking.entity.MedicalPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRepository extends JpaRepository<MedicalPackage, Long> {

    List<MedicalPackage> findByClinic_Id(Long clinicId);
}
