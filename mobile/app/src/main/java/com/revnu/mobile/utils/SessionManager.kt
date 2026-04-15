package com.revnu.mobile.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("revnu_session", Context.MODE_PRIVATE)

    fun saveUser(email: String, fullName: String, role: String, status: String) {
        prefs.edit()
            .putString("email", email)
            .putString("fullName", fullName)
            .putString("role", role)
            .putString("status", status)
            .apply()
    }

    fun getUserEmail(): String? = prefs.getString("email", null)

    fun getFullName(): String? = prefs.getString("fullName", null)

    fun getUserRole(): String? = prefs.getString("role", null)

    fun getUserStatus(): String? = prefs.getString("status", null)

    fun clearSession() {
        prefs.edit()
            .remove(Constants.KEY_EMAIL)
            .remove(Constants.KEY_FULL_NAME)
            .remove(Constants.KEY_ROLE)
            .apply()
    }
}
