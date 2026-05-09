package com.revnu.backend.features.admin.dto;

public record AdminStatsResponse(
        long totalTenants,
        long totalRestaurants,
        long newTenantsThisMonth,
        long activeTenants,
        long suspendedTenants
        ) {

}
