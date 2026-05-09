package com.revnu.backend.features.settings.dto;

public record SystemSettingsDto(
        boolean emailNotifications,
        boolean pushNotifications,
        boolean requireReceiptPhoto,
        boolean softLockRecords
        ) {

}
