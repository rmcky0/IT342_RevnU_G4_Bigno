package com.revnu.mobile.features.settings.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("fullname") val fullname: String
)
