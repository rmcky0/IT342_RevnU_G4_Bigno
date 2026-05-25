package com.revnu.mobile.features.auth.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import com.revnu.mobile.R
import com.revnu.mobile.core.network.RetrofitClient
import com.revnu.mobile.features.auth.model.ForgotPasswordState
import com.revnu.mobile.features.auth.repository.ForgotPasswordRepository
import com.revnu.mobile.features.auth.viewmodel.ForgotPasswordViewModel
import kotlinx.coroutines.launch

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var viewModel: ForgotPasswordViewModel

    private lateinit var btnBack: ImageButton
    private lateinit var tvTitle: TextView
    private lateinit var tvSubtitle: TextView
    private lateinit var tvError: TextView

    private lateinit var stepEmail: LinearLayout
    private lateinit var etEmail: EditText
    private lateinit var btnSendOtp: MaterialButton

    private lateinit var stepOtp: LinearLayout
    private lateinit var etOtp: EditText
    private lateinit var btnVerifyOtp: MaterialButton
    private lateinit var tvResendOtp: TextView

    private lateinit var stepNewPassword: LinearLayout
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnResetPassword: MaterialButton

    private lateinit var stepDone: LinearLayout
    private lateinit var btnBackToLogin: MaterialButton

    private var currentEmail = ""
    private var verifiedOtp = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        RetrofitClient.init(this)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ForgotPasswordViewModel(ForgotPasswordRepository(RetrofitClient.apiService)) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ForgotPasswordViewModel::class.java]

        bindViews()
        setupListeners()
        observeViewModel()
    }

    private fun bindViews() {
        btnBack = findViewById(R.id.btnBack)
        tvTitle = findViewById(R.id.tvTitle)
        tvSubtitle = findViewById(R.id.tvSubtitle)
        tvError = findViewById(R.id.tvError)

        stepEmail = findViewById(R.id.stepEmail)
        etEmail = findViewById(R.id.etEmail)
        btnSendOtp = findViewById(R.id.btnSendOtp)

        stepOtp = findViewById(R.id.stepOtp)
        etOtp = findViewById(R.id.etOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        tvResendOtp = findViewById(R.id.tvResendOtp)

        stepNewPassword = findViewById(R.id.stepNewPassword)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        stepDone = findViewById(R.id.stepDone)
        btnBackToLogin = findViewById(R.id.btnBackToLogin)
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        btnSendOtp.setOnClickListener {
            viewModel.clearError()
            viewModel.requestOtp(etEmail.text.toString().trim())
        }

        btnVerifyOtp.setOnClickListener {
            viewModel.clearError()
            viewModel.verifyOtp(currentEmail, etOtp.text.toString().trim())
        }

        tvResendOtp.setOnClickListener {
            if (viewModel.cooldown.value == 0) {
                viewModel.clearError()
                viewModel.resendOtp(currentEmail)
            }
        }

        btnResetPassword.setOnClickListener {
            viewModel.clearError()
            viewModel.resetPassword(
                email = currentEmail,
                otp = verifiedOtp,
                newPassword = etNewPassword.text.toString(),
                confirmPassword = etConfirmPassword.text.toString()
            )
        }

        btnBackToLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.state.collect { handleState(it) } }
                launch { viewModel.cooldown.collect { updateResendButton(it) } }
            }
        }
    }

    private fun handleState(state: ForgotPasswordState) {
        hideError()
        when (state) {
            is ForgotPasswordState.Idle -> setLoading(false)

            is ForgotPasswordState.Loading -> setLoading(true)

            is ForgotPasswordState.OtpSent -> {
                setLoading(false)
                currentEmail = state.email
                showStep(Step.OTP)
            }

            is ForgotPasswordState.OtpVerified -> {
                setLoading(false)
                verifiedOtp = state.otp
                showStep(Step.NEW_PASSWORD)
            }

            is ForgotPasswordState.PasswordReset -> {
                setLoading(false)
                showStep(Step.DONE)
            }

            is ForgotPasswordState.Error -> {
                setLoading(false)
                showError(state.message)
            }
        }
    }

    private fun updateResendButton(seconds: Int) {
        if (seconds > 0) {
            tvResendOtp.text = "Resend in ${seconds}s"
            tvResendOtp.alpha = 0.4f
            tvResendOtp.isClickable = false
        } else {
            tvResendOtp.text = "Resend OTP"
            tvResendOtp.alpha = 1f
            tvResendOtp.isClickable = true
        }
    }

    private fun showStep(step: Step) {
        stepEmail.visibility = if (step == Step.EMAIL) View.VISIBLE else View.GONE
        stepOtp.visibility = if (step == Step.OTP) View.VISIBLE else View.GONE
        stepNewPassword.visibility = if (step == Step.NEW_PASSWORD) View.VISIBLE else View.GONE
        stepDone.visibility = if (step == Step.DONE) View.VISIBLE else View.GONE

        when (step) {
            Step.EMAIL -> {
                tvTitle.text = "Forgot Password"
                tvSubtitle.text = "Enter your registered email and we'll send you a one-time code."
            }
            Step.OTP -> {
                tvTitle.text = "Enter OTP"
                tvSubtitle.text = "We sent a 6-digit code to $currentEmail. It expires in 10 minutes."
            }
            Step.NEW_PASSWORD -> {
                tvTitle.text = "Set New Password"
                tvSubtitle.text = "OTP verified. Enter your new password below."
            }
            Step.DONE -> {
                tvTitle.text = "Password Reset"
                tvSubtitle.text = "Your password has been updated successfully."
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        btnSendOtp.isEnabled = !isLoading
        btnVerifyOtp.isEnabled = !isLoading
        btnResetPassword.isEnabled = !isLoading
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun hideError() {
        tvError.visibility = View.GONE
    }

    private enum class Step { EMAIL, OTP, NEW_PASSWORD, DONE }
}
