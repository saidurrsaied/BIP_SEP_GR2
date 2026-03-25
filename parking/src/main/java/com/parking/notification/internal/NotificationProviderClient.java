package com.parking.notification.internal;

import org.springframework.stereotype.Component;

@Component
public class NotificationProviderClient {
    public boolean sendEmail(Long userId, String subject, String body) {
        // Placeholder for external notification service API call
        return true;
    }

    public boolean sendSms(Long userId, String message) {
        // Placeholder for external notification service API call
        return true;
    }
}
