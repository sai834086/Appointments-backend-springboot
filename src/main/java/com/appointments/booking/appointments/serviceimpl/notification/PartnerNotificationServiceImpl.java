package com.appointments.booking.appointments.serviceimpl.notification;

import com.appointments.booking.appointments.exception.UnauthorizedAccessOrUnknownException;
import com.appointments.booking.appointments.model.notification.PartnerNotification;
import com.appointments.booking.appointments.model.notification.PartnerNotificationType;
import com.appointments.booking.appointments.model.patner.PartnerUser;
import com.appointments.booking.appointments.payload.response.notification.PartnerNotificationResponse;
import com.appointments.booking.appointments.repository.notification.PartnerNotificationRepository;
import com.appointments.booking.appointments.repository.patner.PartnerUserRepository;
import com.appointments.booking.appointments.service.notification.PartnerNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerNotificationServiceImpl implements PartnerNotificationService {

    /** Guard rail so a bad `size` param can't pull the whole table. */
    private static final int MAX_PAGE_SIZE = 100;

    private final PartnerNotificationRepository partnerNotificationRepository;
    private final PartnerUserRepository partnerUserRepository;

    @Override
    @Transactional
    public void createNotification(Long partnerId,
                                   String title,
                                   String message,
                                   PartnerNotificationType type,
                                   String referenceId,
                                   String link) {

        if (partnerId == null) {
            log.debug("Skipping partner notification of type {} — no partner id resolved", type);
            return;
        }

        PartnerUser partner = partnerUserRepository.findById(partnerId).orElse(null);
        if (partner == null) {
            log.warn("Skipping partner notification of type {} — partner {} not found", type, partnerId);
            return;
        }

        PartnerNotification notification = PartnerNotification.builder()
                .partner(partner)
                .title(title)
                .message(message)
                .type(type)
                .isRead(false)
                .referenceId(referenceId)
                .link(link)
                .build();

        partnerNotificationRepository.save(notification);
        log.info("Stored partner notification [{}] for partner {}", type, partnerId);
    }

    /**
     * The JWT carries the AppUser id, but notifications are keyed by
     * partnerId. Returns null when the caller isn't a partner (e.g. a manager
     * token), in which case the read methods yield an empty feed rather than
     * an error.
     */
    private Long resolvePartnerId(Long appUserId) {
        if (appUserId == null) return null;
        return partnerUserRepository.findByAppUser_UserId(appUserId)
                .map(PartnerUser::getPartnerId)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartnerNotificationResponse> getNotifications(Long appUserId,
                                                              boolean unreadOnly,
                                                              int page,
                                                              int size) {

        Long partnerId = resolvePartnerId(appUserId);
        if (partnerId == null) return List.of();

        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.min(Math.max(size, 1), MAX_PAGE_SIZE));

        Page<PartnerNotification> result = unreadOnly
                ? partnerNotificationRepository
                    .findByPartner_PartnerIdAndIsReadFalseOrderByCreatedAtDesc(partnerId, pageable)
                : partnerNotificationRepository
                    .findByPartner_PartnerIdOrderByCreatedAtDesc(partnerId, pageable);

        return result.getContent().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long appUserId) {
        Long partnerId = resolvePartnerId(appUserId);
        if (partnerId == null) return 0L;
        return partnerNotificationRepository.countByPartner_PartnerIdAndIsReadFalse(partnerId);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long appUserId) {
        Long partnerId = resolvePartnerId(appUserId);
        if (partnerId == null) {
            throw new UnauthorizedAccessOrUnknownException("Not authorized");
        }

        PartnerNotification notification = partnerNotificationRepository.findById(notificationId)
                .orElseThrow(() -> new UnauthorizedAccessOrUnknownException("Notification not found"));

        if (!notification.getPartner().getPartnerId().equals(partnerId)) {
            throw new UnauthorizedAccessOrUnknownException("Not authorized");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            partnerNotificationRepository.save(notification);
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long appUserId) {
        Long partnerId = resolvePartnerId(appUserId);
        if (partnerId == null) return;
        partnerNotificationRepository.markAllAsReadByPartnerId(partnerId);
    }

    private PartnerNotificationResponse toResponse(PartnerNotification n) {
        return PartnerNotificationResponse.builder()
                .notificationId(n.getPartnerNotificationId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .isRead(n.isRead())
                .referenceId(n.getReferenceId())
                .link(n.getLink())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
