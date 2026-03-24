package com.parking.notification.internal;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "notif_notification_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRecord {
    @Id
    private String notificationId;

    private String event;
    private String userId;
    private Instant sentAt;
    private String channel;
    private String status;
}
