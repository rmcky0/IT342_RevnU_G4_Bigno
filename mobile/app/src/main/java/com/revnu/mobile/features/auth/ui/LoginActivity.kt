package com.revnu.mobile.features.auth.ui

import android.animation.ObjectAnimator
import android.util.Log
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
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
import com.revnu.mobile.features.auth.model.LoginRequest
import com.revnu.mobile.features.auth.repository.AuthRepository
import com.revnu.mobile.features.auth.viewmodel.AuthViewModel
import com.revnu.mobile.features.restaurant.ui.RestaurantSetupActivity
import com.revnu.mobile.core.ui.WebRedirectActivity
import com.revnu.mobile.features.dashboard.ui.DashboardActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnBack: ImageButton
    private lateinit var btnLogin: MaterialButton
    private lateinit var btnGoogleSignIn: MaterialButton
    private lateinit var tvForgotPassword: TextView

    private lateinit var sessionManager: SessionManager
    private lateinit var viewModel: AuthViewModel

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("GoogleAuth", "resultCode=${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                val idToken = account.idToken
                Log.d("GoogleAuth", "email=${account.email} idToken=${if (idToken != null) "present(${idToken.length} chars)" else "NULL"}")
                if (idToken != null) {
                    viewModel.loginWithGoogle(idToken)
                } else {
                    Log.e("GoogleAuth", "ID token is null — wrong client ID or requestIdToken not set")
                    Toast.makeText(this, "Google sign-in failed: no ID token", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("GoogleAuth", "ApiException statusCode=${e.statusCode}", e)
                Toast.makeText(this, "Google sign-in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.w("GoogleAuth", "Sign-in cancelled or failed, resultCode=${result.resultCode}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val glassCard = findViewById<View>(R.id.bgGlassCard)
        ObjectAnimator.ofFloat(glassCard, "translationY", 0f, -30f).apply {
            duration = 4000 // 4 seconds up, 4 seconds down (8s total like your JSX)
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
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnSignIn)
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)
        tvForgotPassword = findViewById(R.id.tvForgotPassword)
        btnBack =findViewById(R.id.btnAuthBack)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                viewModel.login(LoginRequest(email, password))
            }
        }
        btnBack.setOnClickListener {
            finish()
        }

        tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        btnGoogleSignIn.setOnClickListener {
            launchGoogleSignIn()
        }
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

                            // route based on the flag
                            if (state.hasRestaurant) {
                                startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))
                            } else {
                                startActivity(Intent(this@LoginActivity, RestaurantSetupActivity::class.java))
                            }
                            finish()
                        }
                        is AuthState.AdminDetected -> {
                            setLoading(false)
                            showAdminRedirect()
                        }
                        is AuthState.Error -> {
                            setLoading(false)
                            Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_LONG).show()
                            viewModel.resetState()
                        }
                    }
                }
            }
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

    private fun setLoading(isLoading: Boolean) {
        btnLogin.isEnabled = !isLoading
        btnBack.isEnabled = !isLoading
        btnGoogleSignIn.isEnabled = !isLoading
        btnGoogleSignIn.text = if (isLoading) "Signing in..." else "Google"
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

    private fun showAdminRedirect() {
        val intent = Intent(this, WebRedirectActivity::class.java).apply {
            putExtra(WebRedirectActivity.EXTRA_IS_ADMIN_LOGIN, true)
        }
        startActivity(intent)
        finish()
    }
}