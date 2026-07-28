package com.appointments.booking.appointments.event;

import com.appointments.booking.appointments.model.notification.PartnerNotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * Published by business services when something happens that a partner should
 * know about. Handled by {@link PartnerNotificationListener} after the
 * triggering transaction commits, so a rolled-back operation never leaves a
 * notification behind.
 */
@Getter
@Builder
@AllArgsConstructor
public class PartnerNotificationEvent {

    private final Long partnerId;
    private final PartnerNotificationType type;
    private final String title;
    private final String message;

    /** Business key of the source record — confirmation number, property id, … */
    private final String referenceId;

    /** Frontend route the row links to. May be null. */
    private final String link;
}
