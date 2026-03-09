package com.emedicalbooking.service;

import com.emedicalbooking.dto.request.BookAppointmentRequest;
import com.emedicalbooking.dto.request.ConfirmBookingRequest;
import com.emedicalbooking.dto.request.VerifyBookingRequest;

public interface BookingService {

    void bookAppointment(BookAppointmentRequest request);

    void verifyBooking(VerifyBookingRequest request);

    void confirmBooking(int bookingId, ConfirmBookingRequest request);
}
