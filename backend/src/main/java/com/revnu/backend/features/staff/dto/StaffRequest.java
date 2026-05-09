package com.revnu.backend.features.staff.dto;

import java.math.BigDecimal;

public record StaffRequest(
        String fullname,
        String position,
        BigDecimal salaryRate
        ) {

}
