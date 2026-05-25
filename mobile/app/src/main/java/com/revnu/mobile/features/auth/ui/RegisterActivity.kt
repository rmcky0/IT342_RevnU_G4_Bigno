package com.revnu.mobile.features.auth.ui

import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.core.session.SessionManager
import com.revnu.mobile.features.auth.model.AuthState
import com.revnu.mobile.features.auth.model.RegisterRequest
import com.revnu.mobile.features.auth.repository.AuthRepository
import com.revnu.mobile.features.auth.viewmodel.AuthViewModel
import com.revnu.mobile.features.dashboard.ui.DashboardActivity
import com.revnu.mobile.features.restaurant.ui.RestaurantSetupActivity
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnCreateAccount: MaterialButton
    private lateinit var btnGoogleSignIn: MaterialButton
    private lateinit var btnAuthBack: ImageButton

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel
    private var isGoogleFlow = false

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.loginWithGoogle(idToken)
                } else {
                    Toast.makeText(this, "Google sign-in failed: no ID token", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val glassCard = findViewById<View>(R.id.bgGlassCard)
        ObjectAnimator.ofFloat(glassCard, "translationY", 0f, -30f).apply {
            duration = 4000 
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            start()
        }
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
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
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


        btnGoogleSignIn.setOnClickListener {
            isGoogleFlow = true
            launchGoogleSignIn()
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
                            if (isGoogleFlow) {
                                if (state.hasRestaurant) {
                                    startActivity(Intent(this@RegisterActivity, DashboardActivity::class.java))
                                } else {
                                    startActivity(Intent(this@RegisterActivity, RestaurantSetupActivity::class.java))
                                }
                            } else {
                                Toast.makeText(this@RegisterActivity, "Registration successful. Please log in.", Toast.LENGTH_SHORT).show()
                                viewModel.logout()
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                            }
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
        if (!isPasswordStrong(password)) {
            etPassword.error = "Must be 8+ chars with uppercase, lowercase, number, and special character"
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

    private fun isPasswordStrong(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.any { it.isUpperCase() }) return false
        if (!password.any { it.isLowerCase() }) return false
        if (!password.any { it.isDigit() }) return false
        if (!password.any { !it.isLetterOrDigit() }) return false
        return true
    }

    private fun launchGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(this, gso)
        client.signOut().addOnCompleteListener {
            googleSignInLauncher.launch(client.signInIntent)
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnCreateAccount.isEnabled = !isLoading
        btnAuthBack.isEnabled = !isLoading
        btnGoogleSignIn.isEnabled = !isLoading
        btnGoogleSignIn.text = if (isLoading) "Signing in..." else "Continue with Google"
    }
}