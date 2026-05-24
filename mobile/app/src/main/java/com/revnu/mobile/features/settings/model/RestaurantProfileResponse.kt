package com.revnu.mobile.features.settings.model

import com.google.gson.annotations.SerializedName

data class RestaurantProfileResponse(
    @SerializedName("id") val id: String,
    @SerializedName("restaurantName") val restaurantName: String,
    @SerializedName("physicalAddress") val physicalAddress: String,
    @SerializedName("openingHours") val openingHours: String,
    @SerializedName("closingHours") val closingHours: String,
    @SerializedName("logoFileId") val logoFileId: String?
)
