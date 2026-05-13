package com.revnu.backend.features.notifications.dto;

import java.time.LocalDate;

public record HolidayNotificationRequest(
        LocalDate date,
        String name
        ) {

}
