package com.emedicalbooking.repository;

import com.emedicalbooking.entity.MedicalPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRepository extends JpaRepository<MedicalPackage, Integer> {

    List<MedicalPackage> findByClinic_Id(int clinicId);
}
