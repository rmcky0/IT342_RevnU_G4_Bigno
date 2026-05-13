package com.revnu.backend.features.tags.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TagDto(
        UUID id,
        @NotBlank(message = "Tag name is required")
        @Size(max = 50, message = "Tag name is too long")
        String name,
        @NotBlank(message = "Tag type is required")
        String type
        ) {

}
