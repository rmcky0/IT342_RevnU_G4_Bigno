package com.revnu.backend.features.admin.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;

public record AdminUserResponse(
        UUID id,
        String email,
        String fullname,
        RoleType role,
        AccountStatus status,
        LocalDateTime createdAt,
        UUID restaurantId,
        String restaurantName
        ) {

}
