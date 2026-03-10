package com.emedicalbooking.service.impl;

import com.emedicalbooking.dto.request.BookAppointmentRequest;
import com.emedicalbooking.dto.request.ConfirmBookingRequest;
import com.emedicalbooking.dto.request.VerifyBookingRequest;
import com.emedicalbooking.entity.AllCode;
import com.emedicalbooking.entity.Booking;
import com.emedicalbooking.entity.PatientProfile;
import com.emedicalbooking.entity.Schedule;
import com.emedicalbooking.entity.User;
import com.emedicalbooking.exception.ResourceNotFoundException;
import com.emedicalbooking.repository.AllCodeRepository;
import com.emedicalbooking.repository.BookingRepository;
import com.emedicalbooking.repository.PatientProfileRepository;
import com.emedicalbooking.repository.ScheduleRepository;
import com.emedicalbooking.repository.UserRepository;
import com.emedicalbooking.service.BookingService;
import com.emedicalbooking.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final AllCodeRepository allCodeRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final ScheduleRepository scheduleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public void bookAppointment(BookAppointmentRequest request) {
        // findOrCreate patient by email
        User patient = userRepository.findByEmail(request.getEmail())
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .email(request.getEmail())
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .firstName(request.getFirstName() != null ? request.getFirstName() :
                                    (request.getFullName() != null ? request.getFullName() : ""))
                            .lastName(request.getLastName())
                            .phoneNumber(request.getPhoneNumber())
                            .address(request.getAddress())
                            .roleData(findAllCode("R3"))
                            .build();
                    if (request.getGender() != null) {
                        newUser.setGenderData(findAllCode(request.getGender()));
                    }
                    return userRepository.save(newUser);
                });

        // Luôn tạo booking MỚI — không kiểm tra booking cũ (1 email có thể đặt nhiều lần
        // cho cùng khung giờ: đặt cho bản thân, đặt hộ người thân, v.v.)

        // Kiểm tra lịch khám tồn tại và còn slot
        Schedule schedule = scheduleRepository.findByDoctorIdAndDateAndTimeType(
                request.getDoctorId(), request.getDate(), request.getTimeType()
        ).orElseThrow(() -> new ResourceNotFoundException(
                "Không tìm thấy lịch khám cho bác sĩ này vào thời gian đã chọn"));

        if (schedule.getCurrentNumber() >= schedule.getMaxNumber()) {
            throw new IllegalStateException("Khung giờ này đã đầy, vui lòng chọn khung giờ khác");
        }

        // Tăng currentNumber bằng UPDATE nguyên tử — an toàn với race condition
        int updated = scheduleRepository.incrementCurrentNumber(schedule.getId());
        if (updated == 0) {
            throw new IllegalStateException("Khung giờ này đã đầy, vui lòng chọn khung giờ khác");
        }

        String token = UUID.randomUUID().toString();
        Booking booking = Booking.builder()
                .patient(patient)
                .doctor(userRepository.findById(request.getDoctorId())
                        .orElseThrow(() -> new ResourceNotFoundException("Doctor", "id", request.getDoctorId())))
                .date(request.getDate())
                .timeTypeData(findAllCode(request.getTimeType()))
                .statusData(findAllCode("S1"))
                .token(token)
                .birthday(request.getBirthday())
                .reason(request.getReason())
                .build();
        booking = bookingRepository.save(booking);

        // Nếu đặt hộ cho người khác, lưu PatientProfile và gắn vào booking
        if (Boolean.TRUE.equals(request.getIsForOther())) {
            PatientProfile profile = PatientProfile.builder()
                    .user(patient)
                    .firstName(request.getProfileFirstName())
                    .lastName(request.getProfileLastName())
                    .phoneNumber(request.getProfilePhoneNumber())
                    .gender(request.getProfileGender())
                    .dateOfBirth(request.getProfileDateOfBirth())
                    .address(request.getProfileAddress())
                    .relationship(request.getRelationship())
                    .medicalHistory(request.getMedicalHistory())
                    .build();
            PatientProfile savedProfile = patientProfileRepository.save(profile);
            booking.setPatientProfile(savedProfile);
            bookingRepository.save(booking);
        }

        // Gửi email xác nhận
        String patientName = request.getFullName() != null ? request.getFullName() :
                (request.getFirstName() + " " + (request.getLastName() != null ? request.getLastName() : "")).trim();

        emailService.sendBookingConfirmationEmail(
                request.getEmail(),
                patientName,
                request.getDoctorName(),
                request.getTimeString(),
                request.getLanguage(),
                booking.getToken(),
                request.getDoctorId()
        );
    }

    @Override
    @Transactional
    public void verifyBooking(VerifyBookingRequest request) {
        Booking booking = bookingRepository.findByTokenAndDoctorId(request.getToken(), request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking không tồn tại hoặc đã được xác nhận"));

        booking.setStatusData(findAllCode("S2"));
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void confirmBooking(int bookingId, ConfirmBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (request.getDoctorId() != null && booking.getDoctor().getId() != request.getDoctorId()) {
            throw new IllegalArgumentException("DoctorId không khớp với booking");
        }

        String statusId = request.getStatusId() != null ? request.getStatusId() : "S3";
        booking.setStatusData(findAllCode(statusId));
        bookingRepository.save(booking);
    }

    private AllCode findAllCode(String keyMap) {
        return allCodeRepository.findAll().stream()
                .filter(a -> a.getKeyMap().equals(keyMap))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", keyMap));
    }
}
