package com.emedicalbooking.repository;

import com.emedicalbooking.entity.DoctorClinicSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorClinicSpecialtyRepository extends JpaRepository<DoctorClinicSpecialty, Long> {

    void deleteAllByDoctorId(Long doctorId);

    @Query("SELECT dcs.specialty.id FROM DoctorClinicSpecialty dcs WHERE dcs.doctor.id = :doctorId")
    List<Long> findSpecialtyIdsByDoctorId(@Param("doctorId") Long doctorId);

    @Query("SELECT DISTINCT dcs.doctor.id FROM DoctorClinicSpecialty dcs WHERE dcs.specialty.id = :specialtyId")
    List<Long> findDoctorIdsBySpecialtyId(@Param("specialtyId") Long specialtyId);

    @Query("SELECT DISTINCT dcs.doctor.id FROM DoctorClinicSpecialty dcs WHERE dcs.clinic.id = :clinicId")
    List<Long> findDoctorIdsByClinicId(@Param("clinicId") Long clinicId);

    void deleteAllBySpecialtyId(Long specialtyId);

    void deleteAllByClinicId(Long clinicId);
}
