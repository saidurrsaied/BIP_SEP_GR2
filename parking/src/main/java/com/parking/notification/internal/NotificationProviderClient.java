package com.parking.notification.internal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationProviderClient {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:noreply@parking.com}")
    private String fromAddress;

    public boolean sendEmail(String email, String subject, String body) {
        try {
            if (email == null || email.isEmpty()) {
                log.warn("Cannot send email: recipient email is missing");
                return false;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(email);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent to {} subject='{}'", email, subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage(), e);
            return false;
        }
    }
}
