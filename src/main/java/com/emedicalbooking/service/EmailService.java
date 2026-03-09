package com.emedicalbooking.service;

public interface EmailService {

    void sendBookingConfirmationEmail(String toEmail, String patientName, String doctorName,
                                       String timeString, String language, String token, int doctorId);
}
