package com.revnu.backend.features.notifications.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.revnu.backend.features.notifications.dto.HolidayNotificationRequest;
import com.revnu.backend.features.notifications.dto.NotificationResponse;
import com.revnu.backend.features.notifications.dto.SystemNotificationRequest;
import com.revnu.backend.features.notifications.service.NotificationService;

@RestController
@RequestMapping("/revnu/notifications")
@PreAuthorize("hasRole('TENANT')")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            Principal principal,
            @RequestParam(defaultValue = "15") int limit
    ) {
        List<NotificationResponse> notifications = notificationService.getRecent(principal.getName(), limit);
        return ResponseEntity.ok(Map.of("success", true, "data", notifications));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(Principal principal) {
        long count = notificationService.getUnreadCount(principal.getName());
        return ResponseEntity.ok(Map.of("success", true, "data", count));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markRead(Principal principal, @PathVariable UUID id) {
        NotificationResponse response = notificationService.markRead(principal.getName(), id);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllRead(Principal principal) {
        int count = notificationService.markAllRead(principal.getName());
        return ResponseEntity.ok(Map.of("success", true, "data", count));
    }

    @PostMapping("/holiday")
    public ResponseEntity<Map<String, Object>> createHolidayAlert(
            Principal principal,
            @RequestBody HolidayNotificationRequest request
    ) {
        NotificationResponse response = notificationService.createHolidayNotification(principal.getName(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @PostMapping("/system")
    public ResponseEntity<Map<String, Object>> createSystemAlert(
            Principal principal,
            @RequestBody SystemNotificationRequest request
    ) {
        NotificationResponse response = notificationService.createSystemNotification(principal.getName(), request);
        return ResponseEntity.ok(Map.of("success", true, "data", response));
    }

    @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(Principal principal) {
        return notificationService.subscribe(principal.getName());
    }
}
