package com.revnu.backend.features.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.revnu.backend.features.auth.model.AccountStatus;

public record AdminRestaurantResponse(
        UUID id,
        String name,
        String location,
        LocalDateTime createdAt,
        UUID ownerId,
        String ownerName,
        String ownerEmail,
        AccountStatus ownerStatus,
        UUID logoFileId,
        UUID ownerAvatarFileId
        ) {

}
