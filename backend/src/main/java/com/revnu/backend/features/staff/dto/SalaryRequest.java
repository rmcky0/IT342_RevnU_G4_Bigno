package com.revnu.backend.features.staff.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

public record SalaryRequest(
        @NotNull(message = "Staff is required")
        UUID staffId,
        @NotNull(message = "Salary amount is required")
        @Positive(message = "Salary amount must be greater than zero")
        BigDecimal amount,
        @NotNull(message = "Payment date is required")
        @PastOrPresent(message = "Payment date cannot be in the future")
        LocalDate paymentDate
        ) {

}
