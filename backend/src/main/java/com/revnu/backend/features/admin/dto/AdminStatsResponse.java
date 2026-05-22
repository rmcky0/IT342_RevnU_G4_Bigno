package com.revnu.backend.features.admin.dto;

public record AdminStatsResponse(
        long totalRestaurateurs,
        long totalRestaurants,
        long newRestaurateursThisMonth,
        long activeRestaurateurs,
        long suspendedRestaurateurs
        ) {

}
