package com.revnu.backend.features.staff.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record SalaryRequest(
        UUID staffId,
        BigDecimal amount,
        LocalDate paymentDate
        ) {

}
