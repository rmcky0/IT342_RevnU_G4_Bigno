package com.revnu.backend.features.categories.dto;

import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        String type,
        boolean isDefault,
        UUID restaurantId
) {
}
