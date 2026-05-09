package com.revnu.backend.features.notifications.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        String title,
        String message,
        String type,
        boolean read,
        LocalDateTime createdAt
        ) {

}
