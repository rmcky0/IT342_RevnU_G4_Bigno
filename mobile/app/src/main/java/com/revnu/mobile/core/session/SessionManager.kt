package com.revnu.mobile.core.session

import android.content.Context
import android.content.SharedPreferences
import com.revnu.mobile.core.config.Constants

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    fun saveAuthSession(accessToken: String, refreshToken: String, role: String, hasRestaurant: Boolean = false) {
        prefs.edit()
            .putString(Constants.KEY_TOKEN, accessToken)
            .putString(Constants.KEY_REFRESH_TOKEN, refreshToken)
            .putString(Constants.KEY_ROLE, role)
            .putBoolean(Constants.KEY_HAS_RESTAURANT, hasRestaurant)
            .apply()
    }

    fun fetchAccessToken(): String? = prefs.getString(Constants.KEY_TOKEN, null)

    fun fetchRefreshToken(): String? = prefs.getString(Constants.KEY_REFRESH_TOKEN, null)

    fun fetchRole(): String? = prefs.getString(Constants.KEY_ROLE, null)

    fun fetchHasRestaurant(): Boolean = prefs.getBoolean(Constants.KEY_HAS_RESTAURANT, false)

    fun setHasRestaurant(value: Boolean) {
        prefs.edit().putBoolean(Constants.KEY_HAS_RESTAURANT, value).apply()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}