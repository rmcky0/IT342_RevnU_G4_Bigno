package com.revnu.backend.features.settings.dto;

public record RestaurantProfileRequest(
        String restaurantName,
        String physicalAddress,
        String openingHours,
        String closingHours
        ) {

}
