package com.revnu.mobile.core.config

object Constants {
    const val BASE_URL = "http://10.0.2.2:8080/api/v1/revnu/"
    const val WEB_APP_URL_DEBUG = "http://10.0.2.2:5173/login"
    const val WEB_APP_URL_RELEASE = "https://revnu-bigno.vercel.app/login"

    const val PREFS_NAME = "RevnU_Preferences"
    const val KEY_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_EMAIL = "user_email"
    const val KEY_FULL_NAME = "user_full_name"
    const val KEY_ROLE = "user_role"
    const val KEY_STATUS = "user_status"
    const val KEY_PROVIDER = "user_provider"
    const val KEY_HAS_RESTAURANT = "has_restaurant"
}