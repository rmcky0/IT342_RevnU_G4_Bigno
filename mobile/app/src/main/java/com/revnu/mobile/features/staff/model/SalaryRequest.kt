package com.revnu.mobile.features.staff.model

import java.math.BigDecimal
import java.util.UUID

data class SalaryRequest(
    val staffId: UUID,
    val amount: BigDecimal,
    val paymentDate: java.time.LocalDate
)
