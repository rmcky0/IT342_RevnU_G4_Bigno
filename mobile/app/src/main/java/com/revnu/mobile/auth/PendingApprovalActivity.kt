package com.revnu.mobile.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R

class PendingApprovalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_approval)

        val tvPendingMessage = findViewById<TextView>(R.id.tvPendingMessage)
        val btnBackToLogin = findViewById<MaterialButton>(R.id.btnBackToLogin)

        val fullName = intent.getStringExtra("fullName") ?: "User"
        val restaurantName = intent.getStringExtra("restaurantName") ?: "your restaurant"

        tvPendingMessage.text =
            "Hi $fullName, thanks for signing up for the RevnU portal for $restaurantName."

        btnBackToLogin.setOnClickListener {
            finish()
        }
    }
}