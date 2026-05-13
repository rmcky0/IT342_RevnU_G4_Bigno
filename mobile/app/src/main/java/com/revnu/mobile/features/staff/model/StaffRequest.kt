package com.revnu.mobile.features.staff.model

import java.math.BigDecimal

data class StaffRequest(
    val fullname:   String,
    val position:   String,
    val salaryRate: BigDecimal
)