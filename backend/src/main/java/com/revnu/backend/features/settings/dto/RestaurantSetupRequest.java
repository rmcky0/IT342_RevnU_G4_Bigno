package com.revnu.backend.features.settings.dto;

import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record RestaurantSetupRequest(
        String name,
        String physicalLocation,
        @JsonFormat(pattern = "HH:mm")
        LocalTime openingHrs,
        @JsonFormat(pattern = "HH:mm")
        LocalTime closingHrs
        ) {

}
