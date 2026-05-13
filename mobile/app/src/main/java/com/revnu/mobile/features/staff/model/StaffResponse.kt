package com.revnu.mobile.features.staff.model

import java.io.Serializable
import java.math.BigDecimal

data class StaffResponse(
    val id:         String,       // String for easy Bundle passing (UUID.toString())
    val fullname:   String,
    val position:   String,
    val salaryRate: BigDecimal
) : Serializable