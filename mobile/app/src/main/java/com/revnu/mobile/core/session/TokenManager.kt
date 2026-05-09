package com.revnu.mobile.core.session

import android.content.Context
import com.revnu.mobile.core.config.Constants

class TokenManager(context: Context) {
    private val prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(Constants.KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(Constants.KEY_TOKEN, null)

    fun clearToken() {
        prefs.edit().remove(Constants.KEY_TOKEN).apply()
    }
}

