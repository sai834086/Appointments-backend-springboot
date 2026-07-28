package com.appointments.booking.appointments.model.notification;

import com.appointments.booking.appointments.model.patner.PartnerUser;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * A single entry in a partner's activity feed.
 *
 * <p>Deliberately kept separate from {@link Notification} (which belongs to
 * AppUser) so the two feeds can evolve independently and the existing
 * customer-facing flow is untouched.
 *
 * <p>{@code link} is a frontend route the row navigates to when clicked, and
 * {@code referenceId} is the business key of whatever triggered the entry
 * (confirmation number, property id, …) for debugging and de-duplication.
 */
@Entity
@Table(
        name = "partner_notifications",
        indexes = {
                @Index(name = "idx_pn_partner_created", columnList = "partner_id, created_at"),
                @Index(name = "idx_pn_partner_read", columnList = "partner_id, is_read")
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "partner_notification_id")
    private Long partnerNotificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private PartnerUser partner;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private PartnerNotificationType type;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    /** Business key of the source record — confirmation number, property id, etc. */
    @Column(name = "reference_id", length = 64)
    private String referenceId;

    /** Frontend route to open when the row is clicked, e.g. /partner/properties/7. */
    @Column(length = 255)
    private String link;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
