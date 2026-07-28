package com.appointments.booking.appointments.repository.notification;

import com.appointments.booking.appointments.model.notification.PartnerNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerNotificationRepository extends JpaRepository<PartnerNotification, Long> {

    /** Newest first — backs the notifications page. */
    Page<PartnerNotification> findByPartner_PartnerIdOrderByCreatedAtDesc(Long partnerId, Pageable pageable);

    /** Unread-only variant, for the "Unread" filter tab. */
    Page<PartnerNotification> findByPartner_PartnerIdAndIsReadFalseOrderByCreatedAtDesc(
            Long partnerId, Pageable pageable);

    /** Backs the header bell badge. */
    long countByPartner_PartnerIdAndIsReadFalse(Long partnerId);

    @Modifying
    @Query("UPDATE PartnerNotification n SET n.isRead = true "
            + "WHERE n.partner.partnerId = :partnerId AND n.isRead = false")
    int markAllAsReadByPartnerId(@Param("partnerId") Long partnerId);
}
