package com.revnu.mobile.features.settings.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    @SerializedName("id") val id: String,
    @SerializedName("fullname") val fullname: String,
    @SerializedName("email") val email: String,
    @SerializedName("provider") val provider: String?
)
