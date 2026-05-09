package com.revnu.backend.features.staff.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record StaffProfileResponse(
        UUID id,
        String fullname,
        String position,
        BigDecimal salaryRate,
        List<SalaryResponse> salaryHistory
        ) {

}
