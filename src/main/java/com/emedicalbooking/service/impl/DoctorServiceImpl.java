package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.BulkCreateDoctorServicesRequest;
import com.emedicalbooking.dto.request.BulkCreateScheduleRequest;
import com.emedicalbooking.dto.request.SaveDoctorInfoRequest;
import com.emedicalbooking.dto.response.*;
import com.emedicalbooking.entity.*;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.*;
import com.emedicalbooking.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final UserRepository userRepository;
    private final DoctorInfosRepository doctorInfosRepository;
    private final MarkdownRepository markdownRepository;
    private final ScheduleRepository scheduleRepository;
    private final DoctorServiceRepository doctorServiceRepository;
    private final DoctorClinicSpecialtyRepository doctorClinicSpecialtyRepository;
    private final AllCodeRepository allCodeRepository;
    private final ClinicRepository clinicRepository;
    private final SpecialtyRepository specialtyRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<DoctorListResponse> getTopDoctors(int limit) {
        // Lấy tất cả doctors (role R2), sắp xếp theo count trong DoctorInfos
        return userRepository.findAllWithRelations().stream()
                .filter(u -> u.getRoleData() != null && "R2".equals(u.getRoleData().getKeyMap()))
                .limit(limit)
                .map(this::toDoctorListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorListResponse> getAllDoctors() {
        return userRepository.findAllWithRelations().stream()
                .filter(u -> u.getRoleData() != null && "R2".equals(u.getRoleData().getKeyMap()))
                .map(this::toDoctorListResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveDoctorInfo(int doctorId, SaveDoctorInfoRequest request) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        // Upsert Markdown
        if ("CREATE".equalsIgnoreCase(request.getAction())) {
            Markdown markdown = Markdown.builder()
                    .doctorId(doctorId)
                    .contentHTML(request.getContentHTML())
                    .contentMarkdown(request.getContentMarkdown())
                    .description(request.getDescription())
                    .build();
            markdownRepository.save(markdown);
        } else {
            Markdown markdown = markdownRepository.findByDoctorId(doctorId)
                    .orElseThrow(() -> new ResourceNotFoundException("Markdown", "doctorId", doctorId));
            markdown.setContentHTML(request.getContentHTML());
            markdown.setContentMarkdown(request.getContentMarkdown());
            markdown.setDescription(request.getDescription());
            markdownRepository.save(markdown);
        }

        // Upsert DoctorInfos
        DoctorInfos doctorInfo = doctorInfosRepository.findByDoctorId(doctorId)
                .orElse(DoctorInfos.builder().doctor(doctor).build());

        doctorInfo.setNameClinic(request.getNameClinic());
        doctorInfo.setAddressClinic(request.getAddressClinic());
        doctorInfo.setNote(request.getNote());
        doctorInfo.setPriceData(findAllCode(request.getSelectedPrice()));
        doctorInfo.setPaymentData(findAllCode(request.getSelectedPayment()));
        doctorInfo.setProvinceData(findAllCode(request.getSelectedProvince()));

        doctorInfosRepository.save(doctorInfo);

        // Rebuild DoctorClinicSpecialty
        doctorClinicSpecialtyRepository.deleteAllByDoctorId(doctorId);

        Clinic clinic = clinicRepository.findById(request.getClinicId())
                .orElseThrow(() -> new ResourceNotFoundException("Clinic", "id", request.getClinicId()));

        for (Integer specialtyId : request.getSpecialtyIds()) {
            Specialty specialty = specialtyRepository.findById(specialtyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Specialty", "id", specialtyId));
            DoctorClinicSpecialty dcs = DoctorClinicSpecialty.builder()
                    .doctor(doctor)
                    .clinic(clinic)
                    .specialty(specialty)
                    .build();
            doctorClinicSpecialtyRepository.save(dcs);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorDetailResponse getDoctorDetail(int doctorId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        DoctorDetailResponse.DoctorDetailResponseBuilder builder = DoctorDetailResponse.builder()
                .id(doctor.getId())
                .email(doctor.getEmail())
                .firstName(doctor.getFirstName())
                .lastName(doctor.getLastName())
                .image(doctor.getImage() != null ? Base64.getEncoder().encodeToString(doctor.getImage()) : null)
                .positionData(toAllCodeResponse(doctor.getPositionData()));

        // Markdown
        markdownRepository.findByDoctorId(doctorId).ifPresent(md ->
                builder.markdown(DoctorDetailResponse.MarkdownData.builder()
                        .description(md.getDescription())
                        .contentHTML(md.getContentHTML())
                        .contentMarkdown(md.getContentMarkdown())
                        .build()));

        // DoctorInfo
        doctorInfosRepository.findByDoctorIdWithRelations(doctorId).ifPresent(info -> {
            List<Integer> specialtyIds = doctorClinicSpecialtyRepository.findSpecialtyIdsByDoctorId(doctorId);
            builder.doctorInfo(DoctorDetailResponse.DoctorInfoData.builder()
                    .priceId(info.getPriceData() != null ? info.getPriceData().getKeyMap() : null)
                    .paymentId(info.getPaymentData() != null ? info.getPaymentData().getKeyMap() : null)
                    .provinceId(info.getProvinceData() != null ? info.getProvinceData().getKeyMap() : null)
                    .nameClinic(info.getNameClinic())
                    .addressClinic(info.getAddressClinic())
                    .note(info.getNote())
                    .specialtyIds(specialtyIds)
                    .priceTypeData(toAllCodeResponse(info.getPriceData()))
                    .provinceTypeData(toAllCodeResponse(info.getProvinceData()))
                    .paymentTypeData(toAllCodeResponse(info.getPaymentData()))
                    .build());
        });

        return builder.build();
    }

    @Override
    @Transactional
    public void bulkCreateSchedule(int doctorId, BulkCreateScheduleRequest request) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        String date = request.getFormattedDate();

        if (request.getArrSchedule() == null || request.getArrSchedule().isEmpty()) {
            // Xóa toàn bộ lịch ngày đó
            scheduleRepository.deleteByDoctorIdAndDate(doctorId, date);
            return;
        }

        // Lấy existing schedules
        List<Schedule> existing = scheduleRepository.findByDoctorIdAndDate(doctorId, date);
        Set<String> requestedTimeTypes = request.getArrSchedule().stream()
                .map(BulkCreateScheduleRequest.ScheduleItem::getTimeType)
                .collect(Collectors.toSet());
        Set<String> existingTimeTypes = existing.stream()
                .map(s -> s.getTimeTypeData().getKeyMap())
                .collect(Collectors.toSet());

        // Xóa các timeType không còn trong payload
        existing.stream()
                .filter(s -> !requestedTimeTypes.contains(s.getTimeTypeData().getKeyMap()))
                .forEach(scheduleRepository::delete);

        // Tạo mới nếu chưa tồn tại
        for (BulkCreateScheduleRequest.ScheduleItem item : request.getArrSchedule()) {
            if (!existingTimeTypes.contains(item.getTimeType())) {
                Schedule schedule = Schedule.builder()
                        .doctor(doctor)
                        .date(date)
                        .timeTypeData(findAllCode(item.getTimeType()))
                        .maxNumber(10)
                        .currentNumber(0)
                        .build();
                scheduleRepository.save(schedule);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponse> getScheduleByDate(int doctorId, String date) {
        return scheduleRepository.findByDoctorIdAndDate(doctorId, date).stream()
                .map(s -> ScheduleResponse.builder()
                        .id(s.getId())
                        .doctorId(doctorId)
                        .date(s.getDate())
                        .timeType(s.getTimeTypeData() != null ? s.getTimeTypeData().getKeyMap() : null)
                        .maxNumber(s.getMaxNumber())
                        .currentNumber(s.getCurrentNumber())
                        .timeTypeData(toAllCodeResponse(s.getTimeTypeData()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void bulkCreateDoctorServices(int doctorId, BulkCreateDoctorServicesRequest request) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", doctorId));

        // Xóa tất cả dịch vụ cũ rồi tạo lại
        doctorServiceRepository.deleteAllByDoctorId(doctorId);

        if (request.getArrDoctorService() != null) {
            for (var item : request.getArrDoctorService()) {
                com.emedicalbooking.entity.DoctorService ds = com.emedicalbooking.entity.DoctorService.builder()
                        .doctor(doctor)
                        .nameVi(item.getNameVi())
                        .nameEn(item.getNameEn())
                        .price(item.getPrice())
                        .descriptionVi(item.getDescriptionVi())
                        .descriptionEn(item.getDescriptionEn())
                        .build();
                doctorServiceRepository.save(ds);
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorServiceResponse> getDoctorServices(int doctorId) {
        return doctorServiceRepository.findByDoctorId(doctorId).stream()
                .map(ds -> DoctorServiceResponse.builder()
                        .nameVi(ds.getNameVi())
                        .nameEn(ds.getNameEn())
                        .price(ds.getPrice())
                        .descriptionVi(ds.getDescriptionVi())
                        .descriptionEn(ds.getDescriptionEn())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DoctorExtraInfoResponse getExtraInfo(int doctorId) {
        DoctorInfos info = doctorInfosRepository.findByDoctorIdWithRelations(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("DoctorInfo", "doctorId", doctorId));

        return DoctorExtraInfoResponse.builder()
                .priceId(info.getPriceData() != null ? info.getPriceData().getKeyMap() : null)
                .paymentId(info.getPaymentData() != null ? info.getPaymentData().getKeyMap() : null)
                .provinceId(info.getProvinceData() != null ? info.getProvinceData().getKeyMap() : null)
                .nameClinic(info.getNameClinic())
                .addressClinic(info.getAddressClinic())
                .note(info.getNote())
                .count(info.getCount())
                .priceTypeData(toAllCodeResponse(info.getPriceData()))
                .provinceTypeData(toAllCodeResponse(info.getProvinceData()))
                .paymentTypeData(toAllCodeResponse(info.getPaymentData()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getSpecialtiesByDoctorId(int doctorId) {
        return doctorClinicSpecialtyRepository.findSpecialtyIdsByDoctorId(doctorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DoctorDetailResponse> getDoctorsBySpecialtyId(int specialtyId) {
        List<Integer> doctorIds = doctorClinicSpecialtyRepository.findDoctorIdsBySpecialtyId(specialtyId);
        return doctorIds.stream()
                .map(this::getDoctorDetail)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Integer> getDoctorIdsByClinicId(int clinicId) {
        return doctorClinicSpecialtyRepository.findDoctorIdsByClinicId(clinicId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PatientBookingResponse> getPatientsByDoctorAndDate(int doctorId, String date) {
        return bookingRepository.findByDoctorAndDate(doctorId, date).stream()
                .map(b -> PatientBookingResponse.builder()
                        .id(b.getId())
                        .statusId(b.getStatusData() != null ? b.getStatusData().getKeyMap() : null)
                        .doctorId(b.getDoctor().getId())
                        .patientId(b.getPatient().getId())
                        .date(b.getDate())
                        .timeType(b.getTimeTypeData() != null ? b.getTimeTypeData().getKeyMap() : null)
                        .token(b.getToken())
                        .birthday(b.getBirthday())
                        .reason(b.getReason())
                        .patientData(PatientBookingResponse.PatientData.builder()
                                .email(b.getPatient().getEmail())
                                .firstName(b.getPatient().getFirstName())
                                .lastName(b.getPatient().getLastName())
                                .phoneNumber(b.getPatient().getPhoneNumber())
                                .build())
                        .bookingTimeTypeData(toAllCodeResponse(b.getTimeTypeData()))
                        .build())
                .collect(Collectors.toList());
    }

    // ===== Helper methods =====

    private DoctorListResponse toDoctorListResponse(User user) {
        return DoctorListResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .image(user.getImage() != null ? Base64.getEncoder().encodeToString(user.getImage()) : null)
                .positionData(toAllCodeResponse(user.getPositionData()))
                .genderData(toAllCodeResponse(user.getGenderData()))
                .build();
    }

    private AllCodeResponse toAllCodeResponse(AllCode allCode) {
        if (allCode == null) return null;
        return AllCodeResponse.builder()
                .keyMap(allCode.getKeyMap())
                .valueEn(allCode.getValueEn())
                .valueVi(allCode.getValueVi())
                .build();
    }

    private AllCode findAllCode(String keyMap) {
        return allCodeRepository.findAll().stream()
                .filter(a -> a.getKeyMap().equals(keyMap))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", keyMap));
    }
}
