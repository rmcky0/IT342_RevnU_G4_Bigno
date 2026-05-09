package com.revnu.backend.features.staff.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.revnu.backend.features.staff.model.SalaryStatus;

public record SalaryResponse(
        UUID id,
        UUID staffId,
        String staffName,
        BigDecimal amount,
        LocalDate paymentDate,
        SalaryStatus status,
        LocalDateTime createdAt
        ) {

}
