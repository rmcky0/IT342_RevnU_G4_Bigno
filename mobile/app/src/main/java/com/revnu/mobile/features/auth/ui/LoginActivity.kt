package com.revnu.mobile.features.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.auth.model.LoginRequest
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.admin.ui.AdminPortalActivity
import com.revnu.mobile.features.tenant.ui.TenantPortalActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "REVNU_LOGIN"
    }

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: MaterialButton
    private lateinit var tvGoRegister: TextView

    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        RetrofitClient.init(this)
        sessionManager = SessionManager(this)

        val savedToken = sessionManager.getToken()
        if (!savedToken.isNullOrBlank()) {
            routeToPortal(sessionManager.getUserRole())
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
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (!validateInputs(email, password)) {
                return@setOnClickListener
            }

            login(email, password)
        }

        tvGoRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
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
        return true
    }

    private fun login(email: String, password: String) {
        setLoading(true)

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = RetrofitClient.apiService.login(request)

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body == null) {
                        Toast.makeText(this@LoginActivity, "Empty server response", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    if (!body.status.equals("ACTIVE", ignoreCase = true)) {
                        Toast.makeText(
                            this@LoginActivity,
                            body.message ?: "Your account is not active.",
                            Toast.LENGTH_LONG
                        ).show()
                        return@launch
                    }

                    if (body.accessToken.isNullOrBlank()) {
                        Toast.makeText(this@LoginActivity, "Login succeeded but token is missing", Toast.LENGTH_LONG).show()
                        return@launch
                    }

                    sessionManager.saveSession(body)

                    routeToPortal(body.role)

                } else {
                    val errorText = response.errorBody()?.string()
                    val message = errorText?.ifBlank { "Invalid email or password" } ?: "Invalid email or password"
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.e(TAG, "Network or parsing crash", e)
                Toast.makeText(this@LoginActivity, "Network error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            } finally {
                setLoading(false)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        tvGoRegister.isEnabled = !isLoading
    }

    // 4. Dynamic Routing Function
    private fun routeToPortal(role: String?) {
        when (role?.uppercase()) {
            "ADMIN" -> {
                startActivity(Intent(this, TenantPortalActivity::class.java)) //to be changed to AdminPortalActivity
                finish()
            }
            "TENANT" -> {
                startActivity(Intent(this, TenantPortalActivity::class.java))
                finish()
            }
            else -> {
                Toast.makeText(this, "Unknown user role: $role", Toast.LENGTH_SHORT).show()
                sessionManager.clearSession()
            }
        }
    }
}