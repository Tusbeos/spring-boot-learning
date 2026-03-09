package com.emedicalbooking.service.impl;

import com.emedicalbooking.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Override
    @Async
    public void sendBookingConfirmationEmail(String toEmail, String patientName, String doctorName,
                                              String timeString, String language, String token, int doctorId) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);

            String verifyLink = frontendUrl + "/verify-booking?token=" + token + "&doctorId=" + doctorId;

            if ("vi".equals(language)) {
                helper.setSubject("Xác nhận đặt lịch khám bệnh");
                helper.setText(buildVietnameseEmail(patientName, doctorName, timeString, verifyLink), true);
            } else {
                helper.setSubject("Booking Appointment Confirmation");
                helper.setText(buildEnglishEmail(patientName, doctorName, timeString, verifyLink), true);
            }

            mailSender.send(message);
            log.info("Đã gửi email xác nhận đặt lịch đến: {}", toEmail);
        } catch (MessagingException e) {
            log.error("Lỗi gửi email xác nhận đến {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildVietnameseEmail(String patientName, String doctorName, String timeString, String verifyLink) {
        return "<h3>Xin chào " + escapeHtml(patientName) + ",</h3>"
                + "<p>Bạn đã đặt lịch khám bệnh thành công.</p>"
                + "<p><b>Bác sĩ:</b> " + escapeHtml(doctorName) + "</p>"
                + "<p><b>Thời gian:</b> " + escapeHtml(timeString) + "</p>"
                + "<p>Vui lòng nhấn vào link bên dưới để xác nhận lịch hẹn:</p>"
                + "<p><a href=\"" + verifyLink + "\">Xác nhận lịch hẹn</a></p>"
                + "<p>Trân trọng,<br/>E-Medical Booking</p>";
    }

    private String buildEnglishEmail(String patientName, String doctorName, String timeString, String verifyLink) {
        return "<h3>Dear " + escapeHtml(patientName) + ",</h3>"
                + "<p>Your appointment has been booked successfully.</p>"
                + "<p><b>Doctor:</b> " + escapeHtml(doctorName) + "</p>"
                + "<p><b>Time:</b> " + escapeHtml(timeString) + "</p>"
                + "<p>Please click the link below to confirm your appointment:</p>"
                + "<p><a href=\"" + verifyLink + "\">Confirm Appointment</a></p>"
                + "<p>Best regards,<br/>E-Medical Booking</p>";
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
}
