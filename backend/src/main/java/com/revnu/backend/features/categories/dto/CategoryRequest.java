package com.revnu.backend.features.categories.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        @Size(max = 50, message = "Category name must not exceed 50 characters")
        String name,
        @NotBlank(message = "Category type is required")
        @Pattern(regexp = "SALE|EXPENSE", message = "Type must be SALE or EXPENSE")
        String type
        ) {

}
