package com.revnu.backend.features.auth.dto;

import com.revnu.backend.features.auth.model.AccountStatus;
import com.revnu.backend.features.auth.model.RoleType;

public record AuthResponse(
        String message,
        String email,
        String fullname,
        RoleType role,
        AccountStatus status,
        String provider,
        boolean hasRestaurant,
        String accessToken
        ) {

}
