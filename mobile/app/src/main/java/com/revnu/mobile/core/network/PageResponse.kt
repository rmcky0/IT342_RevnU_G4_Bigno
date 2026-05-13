package com.revnu.mobile.core.network


data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int
)