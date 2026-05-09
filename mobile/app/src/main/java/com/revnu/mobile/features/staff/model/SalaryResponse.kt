package com.revnu.mobile.features.staff.model

import java.io.Serializable
import java.math.BigDecimal

data class SalaryResponse(
    val id:           String,
    val staffId:      String?,
    val employeeName: String?,
    val amount: BigDecimal,
    val paymentDate:  String?,    // "yyyy-MM-dd" string from API
    val status:       SalaryStatus?,
    val createdAt:    String?
) : Serializable