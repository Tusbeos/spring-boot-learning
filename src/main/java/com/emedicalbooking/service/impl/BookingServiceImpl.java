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
import com.emedicalbooking.repository.DoctorInfosRepository;
import com.emedicalbooking.repository.PatientProfileRepository;
import com.emedicalbooking.repository.ScheduleRepository;
import com.emedicalbooking.repository.UserRepository;
import com.emedicalbooking.security.TokenEncryptionUtils;
import com.emedicalbooking.service.BookingService;
import com.emedicalbooking.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    private final TokenEncryptionUtils tokenEncryptionUtils;
    private final DoctorInfosRepository doctorInfosRepository;

    @Value("${app.booking.token-expiry-minutes:5}")
    private int tokenExpiryMinutes;

    @Override
    @Transactional
    public void bookAppointment(BookAppointmentRequest request) {
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
                .tokenExpiry(LocalDateTime.now().plusMinutes(tokenExpiryMinutes))
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

        // Token gửi qua email được mã hoá — người dùng không đọc được. Token thô lưu trong DB.
        String encryptedToken = tokenEncryptionUtils.encrypt(booking.getToken());

        emailService.sendBookingConfirmationEmail(
                request.getEmail(),
                patientName,
                request.getDoctorName(),
                request.getTimeString(),
                request.getLanguage(),
                encryptedToken,
                request.getDoctorId()
        );
    }

    @Override
    @Transactional
    public void verifyBooking(VerifyBookingRequest request) {
        // Giải mã token từ URL email → token thô lưu trong DB
        String rawToken;
        try {
            rawToken = tokenEncryptionUtils.decrypt(request.getToken());
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Token không hợp lệ hoặc đã bị giả mạo");
        }

        Booking booking = bookingRepository.findByTokenAndDoctorId(rawToken, request.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking không tồn tại hoặc đã được xác nhận"));

        // Kiểm tra token hết hạn
        if (booking.getTokenExpiry() != null && LocalDateTime.now().isAfter(booking.getTokenExpiry())) {
            // Giảm slot và xoá booking hết hạn
            scheduleRepository.findByDoctorIdAndDateAndTimeType(
                    booking.getDoctor().getId(), booking.getDate(), booking.getTimeTypeData().getKeyMap()
            ).ifPresent(s -> scheduleRepository.decrementCurrentNumber(s.getId()));
            bookingRepository.delete(booking);
            throw new IllegalStateException("Link xác nhận đặt lịch đã hết hạn (" + tokenExpiryMinutes + " phút). Vui lòng đặt lịch lại.");
        }

        booking.setStatusData(findAllCode("S2"));
        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void confirmBooking(Long bookingId, ConfirmBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", "id", bookingId));

        if (request.getDoctorId() != null && booking.getDoctor().getId() != request.getDoctorId()) {
            throw new IllegalArgumentException("DoctorId không khớp với booking");
        }

        String statusId = request.getStatusId() != null ? request.getStatusId() : "S3";
        booking.setStatusData(findAllCode(statusId));
        bookingRepository.save(booking);

        // Tăng count khi bác sĩ xác nhận khám (status S3 = confirmed)
        if ("S3".equals(statusId)) {
            doctorInfosRepository.findFirstByDoctorId(booking.getDoctor().getId())
                    .ifPresent(info -> {
                        info.setCount(info.getCount() + 1);
                        doctorInfosRepository.save(info);
                    });
        }
    }

    private AllCode findAllCode(String keyMap) {
        if (keyMap == null) return null;
        return allCodeRepository.findByKeyMap(keyMap)
                .orElseThrow(() -> new ResourceNotFoundException("AllCode", "keyMap", keyMap));
    }
}
