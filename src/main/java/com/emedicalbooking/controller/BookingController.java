package com.emedicalbooking.controller;

import com.emedicalbooking.dto.request.BookAppointmentRequest;
import com.emedicalbooking.dto.request.ConfirmBookingRequest;
import com.emedicalbooking.dto.request.VerifyBookingRequest;
import com.emedicalbooking.dto.response.ApiResponse;
import com.emedicalbooking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> bookAppointment(
            @Valid @RequestBody BookAppointmentRequest request) {
        bookingService.bookAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đặt lịch khám thành công", null));
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyBooking(
            @Valid @RequestBody VerifyBookingRequest request) {
        bookingService.verifyBooking(request);
        return ResponseEntity.ok(ApiResponse.success("Xác nhận lịch khám thành công", null));
    }

    @PostMapping("/{bookingId}/confirm")
    public ResponseEntity<ApiResponse<Void>> confirmBooking(
            @PathVariable Long bookingId,
            @Valid @RequestBody ConfirmBookingRequest request) {
        bookingService.confirmBooking(bookingId, request);
        return ResponseEntity.ok(ApiResponse.success("Xác nhận bệnh nhân thành công", null));
    }
}
