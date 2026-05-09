package com.revnu.mobile.core.session

import android.content.Context
import android.content.SharedPreferences
import com.revnu.mobile.core.config.Constants
import com.revnu.mobile.features.auth.model.AuthResponse

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(response: AuthResponse) {
        prefs.edit()
            .putString(Constants.KEY_TOKEN, response.accessToken)
            .putString(Constants.KEY_EMAIL, response.email)
            .putString(Constants.KEY_FULL_NAME, response.fullName)
            .putString(Constants.KEY_ROLE, response.role)
            .putString(Constants.KEY_STATUS, response.status)
            .putString(Constants.KEY_PROVIDER, response.provider)
            .putBoolean(Constants.KEY_HAS_RESTAURANT, response.hasRestaurant)
            .apply()
    }

    fun getToken(): String? = prefs.getString(Constants.KEY_TOKEN, null)
    fun getUserEmail(): String? = prefs.getString(Constants.KEY_EMAIL, null)
    fun getFullName(): String? = prefs.getString(Constants.KEY_FULL_NAME, null)
    fun getUserRole(): String? = prefs.getString(Constants.KEY_ROLE, null)
    fun getHasRestaurant(): Boolean = prefs.getBoolean(Constants.KEY_HAS_RESTAURANT, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}