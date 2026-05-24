package com.revnu.mobile.features.sales.model


sealed class SalesState {
    object Idle : SalesState()
    object Loading : SalesState()
    data class Success(val sales: List<SaleResponse>) : SalesState()
    data class Error(val message: String) : SalesState()
}