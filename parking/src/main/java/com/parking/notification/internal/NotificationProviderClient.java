package com.parking.notification.internal;

import com.parking.usermanagement.UserService;
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
    private final UserService userService;

    @Value("${app.mail.from:noreply@parking.com}")
    private String fromAddress;

    public boolean sendEmail(Long userId, String subject, String body) {
        try {
            String recipientEmail = resolveEmail(userId);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipientEmail);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("Email sent to userId={} subject='{}'", userId, subject);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to userId={}: {}", userId, e.getMessage(), e);
            return false;
        }
    }

    private String resolveEmail(Long userId) {
        return userService.findUserById(userId).getEmail();
    }
}
