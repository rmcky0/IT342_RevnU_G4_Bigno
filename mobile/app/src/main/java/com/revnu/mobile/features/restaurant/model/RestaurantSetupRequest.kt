package com.revnu.mobile.features.restaurant.model

import com.google.gson.annotations.SerializedName

data class RestaurantSetupRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("physicalLocation")
    val physicalLocation: String,

    @SerializedName("openingHrs")
    val openingHrs: String,

    @SerializedName("closingHrs")
    val closingHrs: String
)