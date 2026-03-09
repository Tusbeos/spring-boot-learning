package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.BulkCreateDoctorServicesRequest;
import com.emedicalbooking.dto.request.BulkCreateScheduleRequest;
import com.emedicalbooking.dto.request.SaveDoctorInfoRequest;
import com.emedicalbooking.dto.response.*;

import java.util.List;

public interface DoctorService {

    List<DoctorListResponse> getTopDoctors(int limit);

    List<DoctorListResponse> getAllDoctors();

    void saveDoctorInfo(int doctorId, SaveDoctorInfoRequest request);

    DoctorDetailResponse getDoctorDetail(int doctorId);

    void bulkCreateSchedule(int doctorId, BulkCreateScheduleRequest request);

    List<ScheduleResponse> getScheduleByDate(int doctorId, String date);

    void bulkCreateDoctorServices(int doctorId, BulkCreateDoctorServicesRequest request);

    List<DoctorServiceResponse> getDoctorServices(int doctorId);

    DoctorExtraInfoResponse getExtraInfo(int doctorId);

    List<Integer> getSpecialtiesByDoctorId(int doctorId);

    List<DoctorDetailResponse> getDoctorsBySpecialtyId(int specialtyId);

    List<Integer> getDoctorIdsByClinicId(int clinicId);

    List<PatientBookingResponse> getPatientsByDoctorAndDate(int doctorId, String date);
}
