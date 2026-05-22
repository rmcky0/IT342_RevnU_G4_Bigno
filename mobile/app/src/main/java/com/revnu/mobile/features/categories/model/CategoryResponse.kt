package com.revnu.mobile.features.categories.model

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String,
    @SerializedName("isDefault") val isDefault: Boolean,
    @SerializedName("restaurantId") val restaurantId: String?
)
