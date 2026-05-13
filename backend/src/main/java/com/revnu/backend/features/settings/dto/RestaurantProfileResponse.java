package com.revnu.backend.features.settings.dto;

import java.util.UUID;

public record RestaurantProfileResponse(
        UUID id,
        String restaurantName,
        String physicalAddress,
        String openingHours,
        String closingHours,
        UUID logoFileId
        ) {

}
