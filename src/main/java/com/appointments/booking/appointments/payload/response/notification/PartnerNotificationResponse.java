package com.appointments.booking.appointments.payload.response.notification;

import com.appointments.booking.appointments.model.notification.PartnerNotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerNotificationResponse {

    private Long notificationId;
    private String title;
    private String message;
    private PartnerNotificationType type;

    // Force the JSON key to "isRead" — Lombok's isRead() would serialize as "read".
    @JsonProperty("isRead")
    private boolean isRead;

    private String referenceId;
    private String link;
    private LocalDateTime createdAt;
}
