package com.appointments.booking.appointments.event;

import com.appointments.booking.appointments.service.notification.PartnerNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Turns {@link PartnerNotificationEvent}s into rows in partner_notifications.
 *
 * <p>Runs AFTER_COMMIT so a rolled-back booking produces no notification, and
 * asynchronously so the write never slows the originating request. Any failure
 * is logged and swallowed — a broken feed must not surface as a failed booking.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PartnerNotificationListener {

    private final PartnerNotificationService partnerNotificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void onPartnerNotification(PartnerNotificationEvent event) {
        try {
            partnerNotificationService.createNotification(
                    event.getPartnerId(),
                    event.getTitle(),
                    event.getMessage(),
                    event.getType(),
                    event.getReferenceId(),
                    event.getLink());
        } catch (Exception ex) {
            log.error("Failed to store partner notification of type {} for partner {}",
                    event.getType(), event.getPartnerId(), ex);
        }
    }
}
