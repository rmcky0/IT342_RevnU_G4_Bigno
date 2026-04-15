package com.revnu.mobile.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.api.RetrofitClient
import com.revnu.mobile.model.AuthResponse
import com.revnu.mobile.model.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnCreateAccount: MaterialButton
    private lateinit var tvGoLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        RetrofitClient.init(this)

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        tvGoLogin = findViewById(R.id.tvSignin)
    }

    private fun setupListeners() {
        btnCreateAccount.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (!validateInputs(fullName, email, password, confirmPassword)) return@setOnClickListener

            register(fullName, email, password)
        }

        tvGoLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (fullName.isBlank()) {
            etFullName.error = "Full name is required"
            etFullName.requestFocus()
            return false
        }

        if (email.isBlank()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Enter a valid email"
            etEmail.requestFocus()
            return false
        }

        if (password.isBlank()) {
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return false
        }

        if (password.length < 8) {
            etPassword.error = "Password must be at least 8 characters"
            etPassword.requestFocus()
            return false
        }

        if (confirmPassword.isBlank()) {
            etConfirmPassword.error = "Please confirm your password"
            etConfirmPassword.requestFocus()
            return false
        }

        if (password != confirmPassword) {
            etConfirmPassword.error = "Passwords do not match"
            etConfirmPassword.requestFocus()
            return false
        }

        return true
    }

    private fun register(fullName: String, email: String, password: String) {
        setLoading(true)

        val request = RegisterRequest(
            fullName = fullName,
            email = email,
            password = password
        )

        RetrofitClient.apiService.register(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                setLoading(false)

                if (!response.isSuccessful) {
                    val message = response.errorBody()?.string()?.ifBlank {
                        "Registration failed"
                    } ?: "Registration failed"

                    Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_LONG).show()
                    return
                }

                val body = response.body()
                if (body == null) {
                    Toast.makeText(this@RegisterActivity, "Empty server response", Toast.LENGTH_LONG).show()
                    return
                }

                Toast.makeText(
                    this@RegisterActivity,
                    body.message ?: "Registration successful",
                    Toast.LENGTH_LONG
                ).show()

                if (body.status.equals("PENDING", ignoreCase = true)) {
                    val intent = Intent(this@RegisterActivity, PendingApprovalActivity::class.java)
                    intent.putExtra("fullName", body.fullName)
                    intent.putExtra("restaurantName", "ChiNyMic Restobar")
                    startActivity(intent)
                    finish()
                    return
                }

                if (body.status.equals("ACTIVE", ignoreCase = true) ||
                    body.status.equals("APPROVED", ignoreCase = true)
                ) {
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                    return
                }

                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                setLoading(false)
                Toast.makeText(
                    this@RegisterActivity,
                    "Network error: ${t.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setLoading(isLoading: Boolean) {
        btnCreateAccount.isEnabled = !isLoading
        tvGoLogin.isEnabled = !isLoading
    }
}