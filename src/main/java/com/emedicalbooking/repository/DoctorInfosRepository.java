package com.emedicalbooking.repository;

import com.emedicalbooking.entity.DoctorInfos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DoctorInfosRepository extends JpaRepository<DoctorInfos, Integer> {

    @Query("SELECT DISTINCT d FROM DoctorInfos d " +
           "LEFT JOIN FETCH d.priceData " +
           "LEFT JOIN FETCH d.provinceData " +
           "LEFT JOIN FETCH d.paymentData " +
           "WHERE d.doctor.id = :doctorId")
    List<DoctorInfos> findByDoctorIdWithRelations(@Param("doctorId") int doctorId);

    Optional<DoctorInfos> findFirstByDoctorId(int doctorId);

    // Trả về danh sách doctorId theo clinicId (dùng cho trang chi tiết clinic)
    @Query("SELECT di.doctor.id FROM DoctorInfos di WHERE di.clinic.id = :clinicId")
    List<Integer> findDoctorIdsByClinicId(@Param("clinicId") int clinicId);

    // Lấy danh sách bác sĩ có count >= minCount, sắp xếp giảm dần
    List<DoctorInfos> findByCountGreaterThanEqualOrderByCountDesc(int count);
}
