package com.revnu.mobile.core.ui

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.config.Constants
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.auth.ui.LoginActivity

class WebRedirectActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_IS_ADMIN_LOGIN = "extra_is_admin_login"
    }

    private lateinit var tvTitle: TextView
    private lateinit var tvMessage: TextView
    private lateinit var ivQrCode: ImageView
    private lateinit var btnOpenBrowser: MaterialButton
    private lateinit var btnClose: MaterialButton

    private lateinit var sessionManager: SessionManager
    private var isAdminLogin: Boolean = false

    private val webAppUrl: String
        get() = if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Constants.WEB_APP_URL_DEBUG
        } else {
            Constants.WEB_APP_URL_RELEASE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_redirect)

        sessionManager = SessionManager(this)

        // Check if they got here because an Admin just tried to log in
        isAdminLogin = intent.getBooleanExtra(EXTRA_IS_ADMIN_LOGIN, false)

        bindViews()
        setupUI()
        setupListeners()
    }

    private fun bindViews() {
        tvTitle = findViewById(R.id.tvRedirectTitle)
        tvMessage = findViewById(R.id.tvRedirectMessage)
        ivQrCode = findViewById(R.id.ivQrCode)
        btnOpenBrowser = findViewById(R.id.btnOpenBrowser)
        btnClose = findViewById(R.id.btnClose)
    }

    private fun setupUI() {
        if (isAdminLogin) {
            tvTitle.text = "Admin Access Restricted"
            tvMessage.text = "The mobile application is designed exclusively for Restaurateur floor operations (Sales & Expenses). Please log in to the web application to manage users and view platform analytics."
            btnClose.text = "Back to Login"

            } else {
            tvTitle.text = "Switch to Web"
            tvMessage.text = "Configuration, Settings, and deep Analytics are managed on the Web Application. Please visit the portal on your computer or browser."
            btnClose.text = "Return to Dashboard"
        }

        ivQrCode.setImageResource(R.drawable.revnu_qr_code)
    }

    private fun setupListeners() {
        btnOpenBrowser.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(webAppUrl))
            startActivity(browserIntent)
        }

        btnClose.setOnClickListener {
            if (isAdminLogin) {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                // If it was a Restaurateur trying to access Settings, just close this activity
                finish()
            }
        }
    }
}