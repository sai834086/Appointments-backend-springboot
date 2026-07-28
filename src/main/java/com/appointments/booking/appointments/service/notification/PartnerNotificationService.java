package com.appointments.booking.appointments.service.notification;

import com.appointments.booking.appointments.model.notification.PartnerNotificationType;
import com.appointments.booking.appointments.payload.response.notification.PartnerNotificationResponse;

import java.util.List;

public interface PartnerNotificationService {

    /**
     * Persist a notification for a partner. Silently no-ops when the partner id
     * is null or unknown — a notification failure must never break the business
     * operation that triggered it.
     */
    void createNotification(Long partnerId,
                            String title,
                            String message,
                            PartnerNotificationType type,
                            String referenceId,
                            String link);

    // ------------------------------------------------------------------
    // Read side.
    //
    // These take the *AppUser* id — the value carried in the JWT and passed
    // around by every other partner service — and resolve the owning
    // PartnerUser internally. Note this differs from createNotification
    // above, which takes a real partnerId because the publisher already has
    // the Property (and therefore the PartnerUser) in hand.
    // ------------------------------------------------------------------

    /** Page of the partner's feed, newest first. */
    List<PartnerNotificationResponse> getNotifications(Long appUserId, boolean unreadOnly, int page, int size);

    long getUnreadCount(Long appUserId);

    void markAsRead(Long notificationId, Long appUserId);

    void markAllAsRead(Long appUserId);
}
