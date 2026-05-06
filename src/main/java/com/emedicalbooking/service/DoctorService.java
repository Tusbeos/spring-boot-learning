package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.BulkCreateDoctorServicesRequest;
import com.emedicalbooking.dto.request.BulkCreateScheduleRequest;
import com.emedicalbooking.dto.request.SaveDoctorInfoRequest;
import com.emedicalbooking.dto.response.*;

import java.util.List;

public interface DoctorService {

    List<DoctorListResponse> getTopDoctors(int limit);

    List<DoctorListResponse> getAllDoctors();

    DoctorPageResponse getDoctorsPaginated(int page, int limit, String search, String specialty, String clinic);

    void saveDoctorInfo(Long doctorId, SaveDoctorInfoRequest request);

    DoctorDetailResponse getDoctorDetail(Long doctorId);

    void bulkCreateSchedule(Long doctorId, BulkCreateScheduleRequest request);

    List<ScheduleResponse> getScheduleByDate(Long doctorId, String date);

    void bulkCreateDoctorServices(Long doctorId, BulkCreateDoctorServicesRequest request);

    List<DoctorServiceResponse> getDoctorServices(Long doctorId);

    DoctorExtraInfoResponse getExtraInfo(Long doctorId);

    List<Long> getSpecialtiesByDoctorId(Long doctorId);

    List<DoctorDetailResponse> getDoctorsBySpecialtyId(Long specialtyId);

    List<Long> getDoctorIdsByClinicId(Long clinicId);

    List<DoctorListResponse> getDoctorsByClinicId(Long clinicId);

    List<PatientBookingResponse> getPatientsByDoctorAndDate(Long doctorId, String date);

    void changeDoctorStatus(Long doctorId, String currentStatusKey, String nextStatusKey);

    void setDoctorStatus(Long doctorId, String statusKey);
}
