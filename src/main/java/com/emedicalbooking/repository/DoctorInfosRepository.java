package com.emedicalbooking.repository;

import com.emedicalbooking.entity.DoctorInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DoctorInfosRepository extends JpaRepository<DoctorInfos, Integer> {

    @Query("SELECT d FROM DoctorInfos d " +
           "LEFT JOIN FETCH d.priceData " +
           "LEFT JOIN FETCH d.provinceData " +
           "LEFT JOIN FETCH d.paymentData " +
           "WHERE d.doctor.id = :doctorId")
    Optional<DoctorInfos> findByDoctorIdWithRelations(@Param("doctorId") int doctorId);

    Optional<DoctorInfos> findByDoctorId(@Param("doctorId") int doctorId);
}
