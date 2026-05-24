package com.revnu.mobile.features.auth.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.dashboard.ui.DashboardActivity
import com.revnu.mobile.features.restaurant.ui.RestaurantSetupActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val glassCard = findViewById<View>(R.id.bgGlassCard)
        ObjectAnimator.ofFloat(glassCard, "translationY", 0f, -30f).apply {
            duration = 4000 // 4 seconds up, 4 seconds down (8s total like your JSX)
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
        RetrofitClient.init(this)
        val sessionManager = SessionManager(this)

        val savedToken = sessionManager.fetchAccessToken()
        val savedRole = sessionManager.fetchRole()
        if (!savedToken.isNullOrBlank() && savedRole == "RESTAURATEUR") {
            lifecycleScope.launch {
                val valid = withContext(Dispatchers.IO) {
                    try {
                        val response = RetrofitClient.apiService.getMe()
                        response.isSuccessful && response.body()?.success == true
                    } catch (e: Exception) {
                        false
                    }
                }
                if (valid) {
                    val dest = if (sessionManager.fetchHasRestaurant()) {
                        Intent(this@WelcomeActivity, DashboardActivity::class.java)
                    } else {
                        Intent(this@WelcomeActivity, RestaurantSetupActivity::class.java)
                    }
                    startActivity(dest)
                    finish()
                } else {
                    sessionManager.clearSession()
                    setupClickListeners()
                }
            }
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        findViewById<MaterialButton>(R.id.btnCreateAccount).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        findViewById<MaterialButton>(R.id.btnGoLogin).setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
