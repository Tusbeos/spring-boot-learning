package com.emedicalbooking.repository;

import com.emedicalbooking.entity.DoctorClinicSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorClinicSpecialtyRepository extends JpaRepository<DoctorClinicSpecialty, Integer> {

    void deleteAllByDoctorId(int doctorId);

    @Query("SELECT dcs.specialty.id FROM DoctorClinicSpecialty dcs WHERE dcs.doctor.id = :doctorId")
    List<Integer> findSpecialtyIdsByDoctorId(@Param("doctorId") int doctorId);

    @Query("SELECT DISTINCT dcs.doctor.id FROM DoctorClinicSpecialty dcs WHERE dcs.specialty.id = :specialtyId")
    List<Integer> findDoctorIdsBySpecialtyId(@Param("specialtyId") int specialtyId);

    @Query("SELECT DISTINCT dcs.doctor.id FROM DoctorClinicSpecialty dcs WHERE dcs.clinic.id = :clinicId")
    List<Integer> findDoctorIdsByClinicId(@Param("clinicId") int clinicId);

    void deleteAllBySpecialtyId(int specialtyId);

    void deleteAllByClinicId(int clinicId);
}
