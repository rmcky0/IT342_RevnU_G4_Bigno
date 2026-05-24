package com.revnu.mobile.core.network

import com.google.gson.annotations.SerializedName


data class PageResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("size") val size: Int,
    @SerializedName("number") val number: Int
)