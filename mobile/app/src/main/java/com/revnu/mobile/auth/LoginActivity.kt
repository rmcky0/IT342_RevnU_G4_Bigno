package com.revnu.mobile.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.api.RetrofitClient
import com.revnu.mobile.model.AuthResponse
import com.revnu.mobile.model.LoginRequest
import com.revnu.mobile.ui.main.MainActivity
import com.revnu.mobile.utils.SessionManager
import com.revnu.mobile.utils.TokenManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "REVNU_LOGIN"
    }

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvGoRegister: TextView

    private lateinit var tokenManager: TokenManager
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContentView(R.layout.activity_login)

        RetrofitClient.init(this)
        tokenManager = TokenManager(this)
        sessionManager = SessionManager(this)

        val savedToken = tokenManager.getToken()
        Log.d(TAG, "Existing token found: ${!savedToken.isNullOrBlank()}")

        if (!savedToken.isNullOrBlank()) {
            Log.d(TAG, "User already logged in, opening dashboard")
            openDashboard()
            return
        }

        bindViews()
        setupListeners()
    }

    private fun bindViews() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnSignIn)
        tvGoRegister = findViewById(R.id.tvRegister)
        Log.d(TAG, "Views bound successfully")
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            Log.d(TAG, "Login button clicked")
            Log.d(TAG, "Entered email: $email")
            Log.d(TAG, "Password blank: ${password.isBlank()}")

            if (!validateInputs(email, password)) {
                Log.d(TAG, "Input validation failed")
                return@setOnClickListener
            }

            login(email, password)
        }

        tvGoRegister.setOnClickListener {
            Log.d(TAG, "Register text clicked")
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isBlank()) {
            Log.d(TAG, "Validation error: email is blank")
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d(TAG, "Validation error: invalid email format")
            etEmail.error = "Enter a valid email"
            etEmail.requestFocus()
            return false
        }

        if (password.isBlank()) {
            Log.d(TAG, "Validation error: password is blank")
            etPassword.error = "Password is required"
            etPassword.requestFocus()
            return false
        }

        Log.d(TAG, "Input validation passed")
        return true
    }

    private fun login(email: String, password: String) {
        setLoading(true)

        val request = LoginRequest(email = email, password = password)
        Log.d(TAG, "Sending login request to API for email=$email")

        RetrofitClient.apiService.login(request)
            .enqueue(object : Callback<AuthResponse> {

                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    try {
                        setLoading(false)

                        Log.d(TAG, "onResponse called")
                        Log.d(TAG, "HTTP code: ${response.code()}")
                        Log.d(TAG, "HTTP message: ${response.message()}")
                        Log.d(TAG, "Request URL: ${call.request().url}")

                        if (!response.isSuccessful) {
                            val errorText = try {
                                response.errorBody()?.string()
                            } catch (e: Exception) {
                                Log.e(TAG, "Failed reading error body", e)
                                null
                            }

                            Log.e(TAG, "Login failed")
                            Log.e(TAG, "Error body: $errorText")

                            val message = errorText?.ifBlank {
                                "Invalid email or password"
                            } ?: "Invalid email or password"

                            Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                            return
                        }

                        val body = response.body()
                        Log.d(TAG, "Response successful")
                        Log.d(TAG, "Response body null: ${body == null}")

                        if (body == null) {
                            Log.e(TAG, "Empty server response body")
                            Toast.makeText(
                                this@LoginActivity,
                                "Empty server response",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        Log.d(TAG, "Body status: ${body.status}")
                        Log.d(TAG, "Body role: ${body.role}")
                        Log.d(TAG, "Body email: ${body.email}")
                        Log.d(TAG, "Body fullName: ${body.fullName}")
                        Log.d(TAG, "Token exists: ${!body.accessToken.isNullOrBlank()}")

                        if (body.status.equals("PENDING", ignoreCase = true)) {
                            Log.d(TAG, "User status is PENDING, opening pending approval screen")
                            openPendingApproval(body.fullName)
                            return
                        }

                        if (body.status.equals("INACTIVE", ignoreCase = true)) {
                            Log.d(TAG, "User status is INACTIVE")
                            Toast.makeText(
                                this@LoginActivity,
                                body.message ?: "Your account is inactive",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        val token = body.accessToken
                        if (token.isNullOrBlank()) {
                            Log.e(TAG, "Login succeeded but token is missing")
                            Toast.makeText(
                                this@LoginActivity,
                                "Login succeeded but token is missing",
                                Toast.LENGTH_LONG
                            ).show()
                            return
                        }

                        Log.d(TAG, "Saving token and session")
                        tokenManager.saveToken(token)
                        sessionManager.saveUser(
                            body.email,
                            body.fullName,
                            body.role,
                            body.status ?: ""
                        )
                        Log.d(TAG, "Opening dashboard")
                        openDashboard()

                    } catch (e: Exception) {
                        Log.e(TAG, "Crash inside onResponse", e)
                        Toast.makeText(
                            this@LoginActivity,
                            "Crash: ${e.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    try {
                        setLoading(false)
                        Log.e(TAG, "onFailure called")
                        Log.e(TAG, "Request URL: ${call.request().url}")
                        Log.e(TAG, "Failure message: ${t.localizedMessage}", t)

                        Toast.makeText(
                            this@LoginActivity,
                            "Network error: ${t.localizedMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    } catch (e: Exception) {
                    Log.e(TAG, "Crash inside onFailure", e)
                    }
                }
            })
    }

    private fun setLoading(isLoading: Boolean) {
        Log.d(TAG, "setLoading: $isLoading")
        btnLogin.isEnabled = !isLoading
        tvGoRegister.isEnabled = !isLoading
    }

    private fun openPendingApproval(fullName: String) {
        Log.d(TAG, "openPendingApproval called for fullName=$fullName")
        val intent = Intent(this, PendingApprovalActivity::class.java)
        intent.putExtra("fullName", fullName)
        intent.putExtra("restaurantName", "ChiNyMic Restobar")
        startActivity(intent)
    }

    private fun openDashboard() {
        Log.d(TAG, "openDashboard called")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}