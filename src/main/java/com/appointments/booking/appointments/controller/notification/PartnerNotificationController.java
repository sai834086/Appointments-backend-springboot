package com.appointments.booking.appointments.controller.notification;

import com.appointments.booking.appointments.payload.response.ApiResponse;
import com.appointments.booking.appointments.payload.response.notification.PartnerNotificationResponse;
import com.appointments.booking.appointments.security.JwtUserDetails;
import com.appointments.booking.appointments.service.notification.PartnerNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Partner activity feed.
 *
 * <p>The recipient is always taken from the JWT principal — never from a
 * request parameter — so a partner can only ever read their own feed.
 *
 * <p>{@code jwtUserDetails.getId()} is the AppUser id (matching every other
 * partner controller); the service resolves the owning PartnerUser from it.
 */
@RestController
@RequestMapping("/appointments/partnerUser")
@RequiredArgsConstructor
public class PartnerNotificationController {

    private final PartnerNotificationService partnerNotificationService;

    /** GET the partner's feed, newest first. */
    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotifications(
            @AuthenticationPrincipal JwtUserDetails jwtUserDetails,
            @RequestParam(defaultValue = "false") boolean unread,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "30") int size) {

        Long appUserId = jwtUserDetails.getId();

        List<PartnerNotificationResponse> notifications =
                partnerNotificationService.getNotifications(appUserId, unread, page, size);

        Map<String, Object> payload = new HashMap<>();
        payload.put("notifications", notifications);
        payload.put("unreadCount", partnerNotificationService.getUnreadCount(appUserId));

        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }

    /** GET the unread count only — polled by the header bell. */
    @GetMapping("/notifications/unreadCount")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUnreadCount(
            @AuthenticationPrincipal JwtUserDetails jwtUserDetails) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("unreadCount",
                partnerNotificationService.getUnreadCount(jwtUserDetails.getId()));

        return ResponseEntity.ok(new ApiResponse<>(true, "success", payload));
    }

    /** PATCH mark a single notification as read. */
    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal JwtUserDetails jwtUserDetails,
            @PathVariable Long notificationId) {

        partnerNotificationService.markAsRead(notificationId, jwtUserDetails.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "Marked as read", null));
    }

    /** PATCH mark the whole feed as read. */
    @PatchMapping("/notifications/markAllRead")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal JwtUserDetails jwtUserDetails) {

        partnerNotificationService.markAllAsRead(jwtUserDetails.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, "All marked as read", null));
    }
}
