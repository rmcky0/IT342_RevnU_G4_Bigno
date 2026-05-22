package com.revnu.mobile.features.auth.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.auth.model.AuthState
import com.revnu.mobile.features.auth.model.RegisterRequest
import com.revnu.mobile.features.auth.repository.AuthRepository
import com.revnu.mobile.features.auth.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnCreateAccount: MaterialButton
    private lateinit var btnAuthBack: ImageButton

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        RetrofitClient.init(this)
        sessionManager = SessionManager(this)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repository = AuthRepository(RetrofitClient.apiService, sessionManager)
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        bindViews()
        setupListeners()
        observeViewModel()
    }

    private fun bindViews() {
        etFullName = findViewById(R.id.etFullName)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnCreateAccount = findViewById(R.id.btnCreateAccount)
        btnAuthBack = findViewById(R.id.btnAuthBack)
    }

    private fun setupListeners() {
        btnCreateAccount.setOnClickListener {
            val fullname = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInputs(fullname, email, password, confirmPassword)) {
                val request = RegisterRequest(
                    email = email,
                    password = password,
                    restaurantName = fullname
                )
                viewModel.register(request)
            }
        }


        btnAuthBack.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.authState.collect { state ->
                    when (state) {
                        is AuthState.Idle -> setLoading(false)
                        is AuthState.Loading -> setLoading(true)
                        is AuthState.Success -> {
                            setLoading(false)
                            Toast.makeText(this@RegisterActivity, "registration successful. please log in.", Toast.LENGTH_SHORT).show()

                            // wipe the token so they are forced to log in
                            viewModel.logout()

                            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            finish()
                        }
                        is AuthState.AdminDetected -> {
                            setLoading(false)
                            Toast.makeText(this@RegisterActivity, "Admin accounts cannot be registered here.", Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                        is AuthState.Error -> {
                            setLoading(false)
                            Toast.makeText(this@RegisterActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                    }
                }
            }
        }
    }

    private fun validateInputs(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        if (fullName.isBlank()) {
            etFullName.error = "This field is required"
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

    private fun setLoading(isLoading: Boolean) {
        btnCreateAccount.isEnabled = !isLoading
        btnAuthBack.isEnabled = !isLoading
    }
}