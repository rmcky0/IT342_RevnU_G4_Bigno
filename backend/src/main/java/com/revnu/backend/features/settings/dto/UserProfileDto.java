package com.revnu.backend.features.settings.dto;

import java.util.UUID;

public record UserProfileDto(
        UUID id,
        String fullname,
        String email,
        String provider,
        UUID avatarFileId
        ) {

}
