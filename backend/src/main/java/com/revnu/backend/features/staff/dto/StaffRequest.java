package com.revnu.backend.features.staff.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StaffRequest(
        String fullname,
        String position,
        @NotNull(message = "Salary rate is required")
        @Positive(message = "Salary rate must be greater than zero")
        BigDecimal salaryRate
        ) {

}
